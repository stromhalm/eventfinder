package de.Servlets;

import de.Database.DatabasePreparer;
import de.Events.Event;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Icebreaker on 20.05.2015.
 */
@WebServlet("/api/search")
public class SearchServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// fetch Strings and separate them by " " to available date-queries and AND-Connections on Query
		String query = request.getParameter("searchText");

		if (request.getParameterMap().containsKey("category")) {
			query += " " + request.getParameter("category");
		}

        Pattern p = Pattern.compile("heute|morgen", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(query);

        if (m.find()) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
            Date today = new Date();
            String todayString = sdfDate.format(today);

            Date tomorrow = new Date(today.getTime() + (24 * 60 * 60 * 1000));
            String tomorrowString = sdfDate.format(tomorrow);

            query = query.replaceAll("(?i)heute", todayString);
            query = query.replaceAll("(?i)morgen", tomorrowString);
        }

		ArrayList<Event> results = new DatabasePreparer().searchRequest(query.split(" "));

		if (results != null) {
			// response part
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.write(prepareJSON(results));
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	private String prepareJSON(ArrayList<Event> results) {
		// Convert results to JSON
		JSONObject jsonResult = new JSONObject();
        Collections.reverse(results);

        int i = 0;
		for (Event partialResult : results) {
			JSONObject partialJSON = new JSONObject();
			partialJSON.put("title", partialResult.getTitle());
			partialJSON.put("location", partialResult.getLocation());
			partialJSON.put("date", DatabasePreparer.revertDate(partialResult.getDate())); // default
			partialJSON.put("description", partialResult.getDescription());
			partialJSON.put("longitude", partialResult.getLngPoint());
			partialJSON.put("latitude", partialResult.getLatPoint()); // latitude
			partialJSON.put("infoUrl", partialResult.getInfoUrl());
			partialJSON.put("likes", partialResult.getShares());
			partialJSON.put("isToday", partialResult.isToday());
            partialJSON.put("isTomorrow", partialResult.isTomorrow());
            partialJSON.put("isInNextThreeDays", partialResult.isInNextThreeDays());
            partialJSON.put("isNextWeek", partialResult.isNextWeek());
			jsonResult.put(i, partialJSON);
			i++;
		}
		return jsonResult.toJSONString();
	}

}