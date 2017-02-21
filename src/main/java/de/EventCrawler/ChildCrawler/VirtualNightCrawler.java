package de.EventCrawler.ChildCrawler;

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
 * Created by Icebreaker on 17.06.2015.
 */
public class VirtualNightCrawler extends Crawler {


	public VirtualNightCrawler(String urlToRead) {
		super(urlToRead, "UTF-8");
	}

	@Override
	public void getEvents() {
		Document doc = getHTML();

		// create mainUrl which is needed to switch through the pages
		String urlMain = doc.select("a[href]").first().attr("abs:href").toString();  // contains the main uri && removes the last '/' from the url
		urlMain = urlMain.substring(0, urlMain.length() - 1);
		String urlToRead = ""; // add the specific path to the single page

		Elements eventList = doc.getElementsByTag("article");
		ArrayList<Event> events = new ArrayList<Event>();

		do {
			for (Element docEvent : eventList) {
				Event event = new Event();

				Element headerElem = docEvent.getElementsByTag("header").first();

				// date
				// convert Date to mysql
				boolean isToLate = false;
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date date = new Date(dateFormat.parse(headerElem.getElementsByAttribute("datetime").attr("datetime")).getTime()); // converts the fetched date-string to the mysql-schema of a date and then commit this value as long-value to the mysql-date
					if (date.getTime() > System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000)) { // is the event-date within the next 14 days?
						//System.out.println("Too Late");
						isToLate = true;
					} else {
						event.setDate(date.toString());
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (!isToLate) {
					// title
					event.setTitle(headerElem.getElementsByAttributeValue("itemprop", "name").text());

					// prepare url
					urlToRead = urlMain + headerElem.getElementsByAttribute("href").attr("href");

					// fetch the infoSide for more content
					Document subDoc = getHTML(urlToRead);

					// description
					String description = subDoc.getElementsByClass("event-description").first().text();

					event.setDescription(description.isEmpty() ? "Beschreibung des Events nicht vorhanden. Aber du wirst auch so Spaß hier haben." : description);

					// add url to the event as an infoPage for further information
					String subUrl = subDoc.getElementsByClass("event-description").first().getElementsByTag("a").attr("href");
					if (subUrl != null && !subUrl.isEmpty()) {
						urlToRead = subUrl;
					} // else the url is as before to the infoPage
					event.setInfoUrl(urlToRead);

					event.setShares(getSharesFromUrl(urlToRead));

					// location and geo
					event.setLocation(subDoc.getElementsByClass("event-data-inline").first().text()); // contains the location part of each article

					// fetch address and geoTags from the GeoFinder
					JSONObject geoResult = GeoFinder.findGeoLocation(event.getLocation());
					if (geoResult != null) {
						event.setLngPoint(Double.parseDouble(geoResult.get("lng").toString()));
						event.setLatPoint(Double.parseDouble(geoResult.get("lat").toString()));
					} else {
					//	System.out.println("Wrong location");
					}

					// categories
					String categories = "";
					for (Element category : docEvent.getElementsByClass("tags")) {
						categories = categories + " " + category.text();
					}
					event.setCategories((categories.isEmpty() ? "None" : categories));

					// check if the event already exists or the date is to far
					boolean alreadyExists = false;
					for (Event subEvent : events) {

						if (subEvent.getTitle().equals(event.getTitle()) && subEvent.getLocation().equals(event.getLocation()) && subEvent.getDate().equals(event.getDate())) { // skip
							//System.out.println("Skipped");
							alreadyExists = true;
							break;
						}
					}
					if (!alreadyExists) {
						events.add(event);
					}
				}
			}
			try {
				// fetch new page for more content
				Elements pagination = doc.getElementsByClass("nextpage");
				if (!doc.getElementsByClass("nextpage").isEmpty()) {
					urlToRead = urlMain + pagination.first().getElementsByAttribute("href").attr("href");
					//System.out.println("New Page ");
					doc = getHTML(urlToRead);
					// now change the eventList and iterate through again
					eventList = doc.getElementsByTag("article");
				} else {
					urlToRead = ""; // if empty there is no further page to crawl
					//System.out.println("End of pages");
				}
			} catch (Exception e) {
				e.printStackTrace();
				//System.out.println("Im still alive. Don't worry");
			}
		} while (!urlToRead.isEmpty());

		// insert the events
		insertEvents(events);
	}

	protected int getSharesFromUrl(String urlToRead) {

		Document tmp = getHTML(buildApiUrl(urlToRead));
		String shares = tmp.getAllElements().select("total_count").toString();
		shares = shares.substring(15, shares.length() - 15);
		int result = Integer.parseInt(shares);
		return result;
	}

	protected String buildApiUrl(String urlToRead) {
		String apiUrl = "https://api.facebook.com/method/links.getStats?urls=" + urlToRead + "&format=html";
		return apiUrl;
	}
}
