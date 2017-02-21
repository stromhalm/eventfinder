package de.EventCrawler.ChildCrawler;

import de.EventCrawler.Crawler;
import de.Events.Event;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Dario on 01.06.15.
 */
public class OldenburgDeCrawler extends Crawler {


	public OldenburgDeCrawler(String urlToRead) {
		super(urlToRead, "UTF-8"); // TODO check charset of your page
	}


	@Override
	public void getEvents() {
		Document doc = getHTML();
		Elements eventList = doc.getElementsByClass("eventlist");
		ArrayList<Event> events = new ArrayList<Event>();

		if (eventList.size() > 0) {
			Elements eventRows = eventList.select("tr");
			if (eventRows.size() > 0) {

				for (Element row : eventRows) {
					Elements tds = row.select("td");
					if (tds.size() > 0) {
						Event event = new Event();

						event.setDate(tds.get(1).text());

						Element tdInfo = tds.get(2);

						Element titel = tdInfo.select("a").first();
						if (titel != null)
							event.setTitle(titel.text());

						Element info = tdInfo.select("p").first();
						if (info != null)
							event.setDescription(info.text());

						Element ort = tdInfo.select("span.eventlist_ort").first();
						if (ort != null)
							event.setLocation(ort.text());

						events.add(event);
					}
				}
			}
		}

		//insertEvents(events);
	}

	@Override
	protected int getSharesFromUrl(String urlToRead) {
		return 0;
	}
}
