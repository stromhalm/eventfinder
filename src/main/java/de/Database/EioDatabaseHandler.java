package de.Database;

import de.Events.Event;

import java.sql.*;
import java.util.ArrayList;

public class EioDatabaseHandler {

	//PropertyReader settings = new PropertyReader();
	final String JDBC_Driver = "com.mysql.jdbc.Driver";//settings.readFromProperties("JDBC_Driver");
	final String DB_URL = "jdbc:mysql://ems.informatik.uni-oldenburg.de:55000/it15g03";//settings.readFromProperties("DB_URL");
	final String user = "it15g03";//settings.readFromProperties("user");
	final String pw = "ivolk0t";//settings.readFromProperties("pw");

	public static final int TIMEOUT_MS = 5000;

	ResultSet results;
	protected Connection connection = null;
	PreparedStatement prep = null;

	public String getJDBC_Driver() {
		return JDBC_Driver;
	}

	public EioDatabaseHandler() throws ClassNotFoundException {
		Class.forName(getJDBC_Driver());
		// System.out.println("Database loaded with driver: " + getJDBC_Driver());
	}

	public boolean connect() throws SQLException {
		//already connected
		try {
			if (connection != null && connection.isValid(TIMEOUT_MS)) {
				return true;
			} else {
				connection = DriverManager.getConnection(DB_URL, user, pw);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public ArrayList<Event> fetchSearchResults(String query) {

		ArrayList<Event> fetchResults = new ArrayList<Event>();

		try {
			// Connect to the database
			if (connect()) {
				// System.out.println("Connected to database" + DB_URL);
			}
			// prepare statement and prevent injection
			prep = connection.prepareStatement(query);

			// System.out.println(prep.toString());

			//execute query (prep)
			results = prep.executeQuery();

			//Iterate through ResultSet
			while (results.next()) {
				Event partialEvent = new Event();
				partialEvent.setDate(results.getString("date"));
				partialEvent.setDescription(results.getString("description"));
				partialEvent.setTitle(results.getString("title"));
				partialEvent.setLocation(results.getString("location"));
				partialEvent.setLatPoint(results.getDouble("latPoint"));
				partialEvent.setLngPoint(results.getDouble("lngPoint"));
				partialEvent.setInfoUrl(results.getString("infoUrl"));
				partialEvent.setShares(results.getInt("shares"));
				fetchResults.add(partialEvent);
			}

			closeQueryConnection();

		} catch (java.sql.SQLException sql_e) {
			// System.err.println("SQLException: ");
			// System.err.println(sql_e.getMessage());
		}

		return fetchResults;
	}

	public void changeDBStateQuery(String query) {

		try {
			// Connect to the database
			if (connect()) {
				// System.out.println("Connected to database " + DB_URL);
			}

			// Create Prepared-Statement and prevent injection
			prep = connection.prepareStatement(query);

			//execute query (prep)
			prep.executeUpdate();
			// System.out.println(prep.toString());

			closeInsertUpdateConnection();

		} catch (java.sql.SQLException sql_e) {
			// System.err.println("SQLException: ");
			// System.err.println(sql_e.getMessage());
		}
	}

	private void closeInsertUpdateConnection() {
		try {
			connection.close();
			prep.close();
			// System.out.println("Closed connection");
		} catch (Throwable e) {
			// System.err.println("SQLException thrown: ");
			// errorPrint(e);
		}
	}


	private void closeQueryConnection() {
		try {
			results.close();
			connection.close();
			prep.close();
			// System.out.println("Closed connection");
		} catch (Throwable e) {
			// System.err.println("SQLException thrown: ");
			// errorPrint(e);
		}
	}

	private void errorPrint(Throwable e) {
		if (e instanceof SQLException)
			SQLExceptionPrint((SQLException) e);
		else {
			// System.out.println("A non SQL error occured.");
			// e.printStackTrace();
		}
	}

	private void SQLExceptionPrint(SQLException sqle) {
		while (sqle != null) {
			/*
			System.out.println("\n---SQLException caught---\n");
			System.out.println("SQLState:   " + (sqle).getSQLState());
			System.out.println("Severity:   " + (sqle).getErrorCode());
			System.out.println("Message:    " + (sqle).getMessage());
			sqle.printStackTrace();

			*/
			sqle = sqle.getNextException();
		}
	}
}