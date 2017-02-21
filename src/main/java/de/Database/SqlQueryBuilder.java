package de.Database;

import de.Events.Event;

import java.util.ArrayList;

/**
 * Created by Dario on 28.05.15.
 */
public class SqlQueryBuilder {

	final static String SQL_Table = "events";

	public static String buildSearchQuery(ArrayList<String> searchTags, String date) {
		String query = "";

		if (searchTags.isEmpty() && date.isEmpty()) { // to create an empty query for the insert-query
			query = "SELECT DISTINCT * FROM " + SQL_Table;
		} else {
			query = "SELECT DISTINCT * FROM " + SQL_Table + " WHERE";
		}

		for (String tag : searchTags) {
			if (tag.contains("'")) { // check for tick mark
				tag = tag.replace("'", "\''");
			}
			query = query + " (" + SQL_Table + ".title LIKE '%" + tag + "%' OR " + SQL_Table + ".location LIKE '%" + tag + "%' OR " + SQL_Table + ".description LIKE '%" + tag + "%' OR " + SQL_Table + ".tags LIKE '%" + tag + "%')";
		}
		if (!date.equals("")) {
            if (!date.contains("/")){
                query = query + " (" + SQL_Table + ".date LIKE '%" + date + "%')";
            } else {
                String[] week = date.split("/");
                //System.out.println(week.length);
                if (week.length == 1){
                    query = query + " (" + SQL_Table + ".date LIKE '%" + week[0] + "%')";
                } else {
                    query = query + " (" + SQL_Table + ".date LIKE '%" + week[0] + "%') OR (" + SQL_Table + ".date LIKE '%" + week[1] + "%')";
                }
            }
		}
		query = query.replace(") (", ") AND (");
		return query + " ORDER BY " + SQL_Table + ".date ASC";
	}

	public static String insertEvents(Event event) { // values needs to to be in order of db-schema

		String[] values = {event.getCategories(), event.getTitle(), event.getLocation(), event.getDate(), event.getDescription(), String.valueOf(event.getLngPoint()), String.valueOf(event.getLatPoint()), event.getInfoUrl(), String.valueOf(event.getShares())};
		String insertQuery = "INSERT INTO " + SQL_Table + " (tags, title, location, date, description, lngPoint, latPoint, infoUrl, shares) VALUES (";

		for (int i = 0; i < values.length - 1; i++) {
			if (values[i].contains("'")) { // check for tick mark
				values[i] = values[i].replace("'", "\''");
			} else if (values[i].contains("\u0096")) {
				//System.out.println("Encoding error found");
				values[i] = values[i].replace("\u0096", "-");
			}
			insertQuery = insertQuery + "'" + values[i] + "', ";
		}

		// last value cannot have a dot
		insertQuery = insertQuery + "'" + values[values.length - 1] + "'";
		return insertQuery + ")";
	}

	public static String updateEvents(Event event) {
		String insertQuery = "UPDATE " + SQL_Table + " SET shares = '" + event.getShares() + "' WHERE " + SQL_Table + ".date = '" + event.getDate() + "' AND "
				+ SQL_Table + ".location = '" + event.getLocation() + "'";

		return insertQuery;
	}

	public static String deleteEvent(Event event) {

		String delete = "DELETE FROM " + SQL_Table + " WHERE ";
		// check for critical characters to the sql-query in the events
		if (event.getTitle().contains("'")) {
			event.setTitle(event.getTitle().replace("'", "\''"));
		}
		if (event.getLocation().contains("'")) {
			event.setLocation(event.getLocation().replace("'", "\''"));
		}
		if (event.getCategories().contains("'")) {
			event.setCategories(event.getCategories().replace("'", "\''"));
		}
		delete = delete + SQL_Table + ".date = '" + event.getDate() + "' AND " +
				SQL_Table + ".location = '" + event.getLocation() + "'";

		return delete;
	}
}
