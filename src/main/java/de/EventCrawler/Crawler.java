package de.EventCrawler;

import de.Database.DatabasePreparer;
import de.Events.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Icebreaker on 05.06.2015.
 */
public abstract class Crawler extends Thread {

	/**
	 * Varios Attributes
	 */
	protected String urlToRead = "";
	private String charset;
	ArrayList<Event> lastResults;

	/**
	 * Construktor to instantiate and a childCrawler and use this parentcrawlers methods
	 *
	 * @param urlToRead
	 */
	public Crawler(String urlToRead, String charset) {
		this.urlToRead = urlToRead;
		this.charset = charset;
		this.lastResults = new ArrayList<Event>();
	}

	@Override
	public void run() {

		while (true) { // run until the sun circuit the earth^^
			getEvents();
			try {
				Thread.sleep(1000 * 60 * 60 * 6); // sleep 6 hours and than begin new
			} catch (InterruptedException ex) {
				// do nothing
			}
		}

	}

	/**
	 * @return a Document with the html-Content
	 */
	protected Document getHTML() {

		// parse the result and return it as an Document
		return Jsoup.parse(fetchPage(this.urlToRead));
	}

	/**
	 * This Method is extension of the above method. It is used if there is an a[href] to use for more informations
	 *
	 * @return a Document with the html-Content
	 */
	protected Document getHTML(String urlToRead) {

		// parse the result and return it as an Document
		return Jsoup.parse(fetchPage(urlToRead));
	}

	private String fetchPage(String urlToRead) {
		URL url; // The URL to read
		HttpURLConnection conn; // The actual connection to the web page
		BufferedReader rd; // Used to read results from the web page a buffer can be read faster than a normal string
		String line; // An individual line of the web page HTML

		String result = "";

		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), this.charset));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("Fetched Page: " + urlToRead);
		return result;
	}

	/**
	 * this Method are implemented by all child-crawlers and determine what should be done with the html-Document found by parenMethods
	 */
	protected abstract void getEvents();

	/**
	 * This method is used to fetch the shares on an facebook-link. The specific desing of this method depends on the crawled page
	 *
	 * @param urlToRead
	 * @return integer count of the shares by the given link
	 */
	protected abstract int getSharesFromUrl(String urlToRead);

	/**
	 * insert all events found by a ChildCrawler and deletes all previous found events
	 *
	 * @param events
	 */
	protected void insertEvents(ArrayList<Event> events) {

		new DatabasePreparer().insertEvents(events); // needs to be an instance because of the non-static dbHandler instance in dbPreparer

		// check for old events
		if (!this.lastResults.isEmpty()) {
			this.deleteEvents();
		}

		// now add the remaining events and not replace them to prevent reference and empty lists after a further crawler activities
		for (Event event : events) {
			this.lastResults.add(event);
		}
	}

	/**
	 * delete the events if the crawler already has been active
	 */
	private void deleteEvents() {
		long currentDate = new Date().getTime(); // date in millis
		ArrayList<Event> delete = new ArrayList<Event>();
		for (Event event : this.lastResults) {
			SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = simple.parse(event.getDate());
				if (date.getTime() < (currentDate - 1000 * 60 * 60 * 24)) { // the date of the event is younger than the current date
					// add the event to delete
					//System.out.println("Delete found : " + event.getTitle() + ", EventDate: " + date.getTime() + " compared with " + currentDate);
					delete.add(event);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		// now delete the entries on db and remove the events from the last results -> otherwise the crawlers will always try to remove already deleted events
		for(Event event : delete) {
			this.lastResults.remove(event);
		}
		new DatabasePreparer().deleteEvents(delete);
	}
}
