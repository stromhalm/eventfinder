package de.EventCrawler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.*;

/**
 * Created by Icebreaker on 17.06.2015.
 */
public class GeoFinder {

	private static String link = "https://maps.googleapis.com/maps/api/geocode/json";

	public static synchronized JSONObject findGeoLocation(String location) {

		// initialize the resultJson and the json to fetch the response
		JSONObject latLngPointsAdress = new JSONObject();
		JSONObject json = null;

		// prepare a URL for the geocoder-rest
		URL url = null;
		try {
			// create url
			url = new URL(link + "?address=" + URLEncoder.encode(location, "UTF-8") + "&region=de&sensor=false&key=AIzaSyDjUOtpQAUYFd5Dyu3TyL43BdRgxBgElug");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// send request and get the response written in the urlConnection
		try {
			URLConnection conn = url.openConnection();
			//System.out.println("GEOCODER-URL: " + conn.getURL());

			// convert the response to a string readable by org.json-simple.*
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();
			String inputStr;
			while ((inputStr = streamReader.readLine()) != null) responseStrBuilder.append(inputStr);

			// parse the result send by google in a json that has been requested
			json = (JSONObject) JSONValue.parse(responseStrBuilder.toString()); // first parse the whole response

			// check whether there are no results
			if (json.get("status").toString().equals("OK")) {
				JSONObject results = (JSONObject) ((JSONArray) json.get("results")).get(0); // first array-Element
				JSONObject geoTags = (JSONObject) (((JSONObject) results.get("geometry"))).get("location"); // exakt path in the json-objekt to the latLng-Points

				// add the points and return them
				latLngPointsAdress.put("lng", Double.parseDouble(geoTags.get("lng").toString()));
				latLngPointsAdress.put("lat", Double.parseDouble(geoTags.get("lat").toString()));

				// remove the germany tag
				String address = results.get("formatted_address").toString();
				try {
					latLngPointsAdress.put("address", address.substring(0, address.indexOf(", Germany")));
				} catch (Exception e) { // index out of bounds
					latLngPointsAdress.put("address", address);
				}

				return latLngPointsAdress;
			} else if (json.get("status").toString().equals("OVER_QUERY_LIMIT")) {
				try {
					//System.out.println("Sleeping");
					Thread.sleep(1000 * 60 * 60); // wait 1 hour and then let others try it, will minimize the failures
					findGeoLocation(location); // call again and return the result recursive
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				//System.out.println("ExitCode: " + json.get("status") + " and response is: " + json);
			}
		} catch (IOException e) {
			//System.out.println("IOException: " + json);
			e.printStackTrace();
		} catch (Exception e) {
			//System.out.println("Other Exception: " + json);
			e.printStackTrace();
		}
		return null;
	}
}
