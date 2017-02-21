package de.EventCrawler.ChildCrawler;

import de.Database.DatabasePreparer;
import de.EventCrawler.Crawler;
import de.EventCrawler.GeoFinder;
import de.Events.Event;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Created by Icebreaker on 18.06.2015.
 */
public class NWZInsideCrawler extends Crawler {

	public NWZInsideCrawler(String urlToRead) {
		super(urlToRead, "iso-8859-1");
	}


	@Override
	protected void getEvents() {

		Document doc = getHTML();
		Elements eventList = doc.getElementsByTag("table").first().getElementsByTag("tr");
		ArrayList<Event> events = new ArrayList<Event>();

		String urlMain = doc.select("a[href]").first().attr("abs:href").toString();  // contains the main uri && removes the last '/' from the url
		urlMain = urlMain.substring(0, urlMain.length() - 1);
		String urlToRead = ""; // add the specific path to the single page
		String date = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date dateFormatted = null;
		int page = 0;

		do {
			for (Element eventListElem : eventList) {

				if (date.isEmpty() || dateFormatted == null || dateFormatted.getTime() <= System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000)) { // is the event-date within the next 14 days?

					if (eventListElem.child(0).tag().toString().equals("th")) {
						// changing date
						date = DatabasePreparer.convertDate(eventListElem.child(0).text());
						//System.out.println("New Date: " + date);
						try {
							dateFormatted = new Date(dateFormat.parse(date).getTime()); // converts the fetched date-string to the mysql-schema of a date and then commit this value as long-value to the mysql-date
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else {
						Event event = new Event();
						event.setDate(date); // date will be set automatically

						// title
						event.setTitle(eventListElem.getElementsByTag("a").first().text());

						// prepare url
						urlToRead = eventListElem.getElementsByAttribute("href").attr("href");
						// fetch the infoSide for more content
						Document subDoc = getHTML(urlToRead);

						// set the shares
						event.setShares(getSharesFromUrl(urlToRead));

						// get location
						Element subDocList = subDoc.getElementsByClass("location").first();

						// add url to the event as an infoPage for further information
						String subUrl = subDocList.getElementsByAttributeValue("target", "_blank").attr("href");
						if (subUrl != null && !subUrl.isEmpty()) {
							urlToRead = subUrl;
						} // else the url is as before to the infoPage
						event.setInfoUrl(urlToRead);

						// delete the stuff we don't need
						String locationName = subDocList.getElementsByTag("h4").text() + ", ";
						subDocList.getElementsByTag("h4").remove();
						subDocList.getElementsByTag("strong").remove();

						subDocList.getElementsByTag("a").remove();
						String location = subDocList.text();
						// remove phone number
						try {
							boolean isCrap = true;
							for (int i = location.indexOf('/') - 1; i > 0; i--) {
								if (location.charAt(i) != ' ' && !Character.isDigit(location.charAt(i))) {
									location = location.substring(0, i + 1);
									isCrap = false;
									break;
								}
							}
							if (isCrap) { // the telephone number sometimes isn't following the above pattern
								for (int i = location.length() - 1; i > 0; i--) {
									if (location.charAt(i) != ' ' && !Character.isDigit(location.charAt(i))) {
										location = location.substring(0, i + 1);
										break;
									}
								}
							}

						} catch (Exception e) {
							//System.out.println("An error occurred while removing the phone Number!");
							e.printStackTrace();
						}

						// description
						String description = subDoc.getElementsByClass("clearfix").first().text();
						event.setDescription(description.isEmpty() ? "Beschreibung des Events nicht vorhanden. Aber du wirst auch so Spaß hier haben." : description);

						// TODO categories
						event.setCategories("none");

						// fetch address and geoTags from the GeoFinder
						JSONObject geoResult = GeoFinder.findGeoLocation(location);

						if (geoResult != null) {
							event.setLngPoint(Double.parseDouble(geoResult.get("lng").toString()));
							event.setLatPoint(Double.parseDouble(geoResult.get("lat").toString()));

							// now set the formatted adress
							event.setLocation(locationName + geoResult.get("address").toString());

							// check if the event already exists or the date is to far
							// TODO implement better
							boolean alreadyExists = false;
							for (Event subEvent : events) {

								if (subEvent.getTitle().equals(event.getTitle()) && subEvent.getLocation().equals(event.getLocation()) && subEvent.getDate().equals(event.getDate())) { // skip
									//System.out.println("Skipped");
									alreadyExists = true;
									break;
								}
							}
							if (!alreadyExists) {
								// add the event
								events.add(event);
							}
						} else {
							//System.out.println("Wrong location");
						}
					}
				} else {
					//System.out.println("Too Late");
				}
			}
			try {
				// fetch new page for more content
				Element pagination = doc.getElementsByClass("pagination").first().getElementsByClass("pages").first();
				//System.out.println("PaginationSize: " + pagination.children().size() + ", pageSize: " + page + ", Comparison: " + (page < pagination.children().size()));
				if (page < pagination.children().size() - 1) {
					urlToRead = urlMain + pagination.child(++page).attr("href");
					//System.out.println("New Page ");
					doc = getHTML(urlToRead);
					eventList = doc.getElementsByTag("table").first().getElementsByTag("tr");
				} else {
					urlToRead = ""; // if empty there is no further page to crawl
					//System.out.println("End of pages");
				}
			} catch (Exception e) {
				e.printStackTrace();
				//System.out.println("Im still alive. Don't worry");
			}
		} while (!urlToRead.isEmpty());

		// insert events
		insertEvents(events);
	}

	protected int getSharesFromUrl(String urlToRead) {
		Document tmp = getHTML(buildApiUrl(urlToRead));
		String shares = tmp.getAllElements().select("body").toString();
		shares = shares.substring(shares.lastIndexOf("shares") + 8, shares.length() - 9);
		if (shares.length() > 10) {
			//System.out.println("No shares found");
			return 0;
		}
		int result = Integer.parseInt(shares);
		return result;
	}

	protected String buildApiUrl(String urlToRead) {
		String url = urlToRead.substring(0, urlToRead.length() - 1);
		String apiUrl = "http://graph.facebook.com/?id=" + url + "&format=html";
		return apiUrl;
	}
}
