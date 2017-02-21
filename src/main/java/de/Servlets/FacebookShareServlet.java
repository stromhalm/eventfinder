package de.Servlets;

import de.Database.DatabasePreparer;
import de.Events.Event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Icebreaker on 24.06.2015.
 */
@WebServlet("/api/shares")
public class FacebookShareServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String date = request.getParameter("date");
		int shares = Integer.parseInt(request.getParameter("shares")) + 1; // fetch and increase
		String location = request.getParameter("location");

		new DatabasePreparer().updateEvent(new Event(DatabasePreparer.convertDate(date), location, shares));

		// response part
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.write("{\"shares\": \"" + shares + "\"}");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}
