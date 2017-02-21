package de.EventCrawler;

import de.EventCrawler.ChildCrawler.NWZInsideCrawler;
import de.EventCrawler.ChildCrawler.OldenburgDeCrawler;
import de.EventCrawler.ChildCrawler.VirtualNightCrawler;

/**
 * Created by Dario on 09.06.15.
 */
public class CrawlerMain {

	/**
	 * maybe the args can be fix uri. should be easier to deal with
	 * This main starts all Crawler added inside. After first start all crawler will run forever
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// OldenburgDe-Crawler
			//new OldenburgDeCrawler("http://www.oldenburg.de/sonderseiten/veranstaltungen.html").start();

			/**and more Crawler**/

			// virtual Night-Crawler
			new VirtualNightCrawler("http://www.virtualnights.com/oldenburg/events?direction=asc&sort=start_date&city=260&musictypes[0]=0&time=all&categories[0]=0&additionalsort=flyer_small&additionaldirection=desc&secondadditionalsort=start_date&secondadditionaldirection=asc&page=1&limit=20").start();

			// NWZ-Inside-Crawler
			new NWZInsideCrawler("http://www.nwz-inside.de/Events/Terminkalender/?Suchbegriff=&von=&bis=&wo[]=Bremen&wo[]=Stadt_Oldenburg&wo[]=Kreis_Oldenburg&wo[]=Region&Suchen=Suche+starten").start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
