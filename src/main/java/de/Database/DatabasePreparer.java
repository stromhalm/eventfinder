package de.Database;

import de.Events.Event;
import org.json.simple.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Icebreaker on 06.06.2015.
 */
public class DatabasePreparer {
    private EioDatabaseHandler database = null; // DatabaseConnector

    public synchronized ArrayList<Event> searchRequest(String[] searchTags) {

        ArrayList<String> searchTagList = new ArrayList<String>(Arrays.asList(searchTags));

        // initialize attributes
        String dateRevert = "";
        boolean hasDate = true;
        String toDelete = ""; // needs to be cached otherwise it will throw a ConcurrentModificationException
        // Examine for date and Convert Tag-String-Array to ArrayList to delete Date tag if found
        for (String tag : searchTagList) { // search whether the tag is a date or something else
            for (int i = 0; i < tag.length(); i++) {
                if (!Character.isDigit(tag.charAt(i))) { // the character is no digit
                    if (tag.charAt(i) != '.') { // so check whether it is a dot if not, then this tag can not be a date
                        hasDate = false;
                        break;
                    }
                }
            }

            Pattern p = Pattern.compile("heute|morgen|montag|dienstag|mittwoch|donnerstag|freitag|samstag|sonntag|wochenende", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(tag);

            if (hasDate) { // format the date in a form that enables sql-like queries
                dateRevert = convertDate(tag);
                toDelete = tag;
            } else if (m.find()){
                dateRevert = this.checkForWeekdays(tag);
                toDelete = tag;
            }

        }

        // now delete the tag from the normal list
        searchTagList.remove(toDelete);

        // this stage is only accessible, if just a date, just a normal tag, both or nothing (onLoad of the page) has been entered
        if (!searchTagList.isEmpty() || !dateRevert.isEmpty() || (searchTagList.isEmpty() && dateRevert.isEmpty())) {

            openDatabase();
            // request to the server
            return database.fetchSearchResults(SqlQueryBuilder.buildSearchQuery(searchTagList, dateRevert));
        }
        // else
        return null;
    }

    private String checkForWeekdays(String tag) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        java.util.Date today = new java.util.Date();
        calendar.setTime(today);
        String date;

        if (tag.equalsIgnoreCase("montag")){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DATE, 1);
            }
        } else if (tag.equalsIgnoreCase("dienstag")){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY) {
                calendar.add(Calendar.DATE, 1);
            }
        } else if (tag.equalsIgnoreCase("mittwoch")){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
                calendar.add(Calendar.DATE, 1);
            }
        } else if (tag.equalsIgnoreCase("donnerstag")){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
                calendar.add(Calendar.DATE, 1);
            }
        } else if (tag.equalsIgnoreCase("freitag")){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                calendar.add(Calendar.DATE, 1);
            }
        } else if (tag.equalsIgnoreCase("samstag")){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                calendar.add(Calendar.DATE, 1);
            }
        } else if (tag.equalsIgnoreCase("sonntag")){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, 1);
            }
        }

        date = convertDate(sdfDate.format(calendar.getTime())) + "/";

        if (tag.equalsIgnoreCase("wochenende")) {
            calendar.setTime(today);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            String saturdayString = convertDate(sdfDate.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            String sundayString = convertDate(sdfDate.format(calendar.getTime()));
            date = (saturdayString + "/" + sundayString);
        }

        return date;
    }

    public synchronized void insertEvents(ArrayList<Event> events) {
        if (!events.isEmpty()) {
            openDatabase();
        }
        // first check which events already exists
        ArrayList<Event> existingEvents = database.fetchSearchResults(SqlQueryBuilder.buildSearchQuery(new ArrayList<String>(), ""));
        for (Event existing : existingEvents) {
            for (Event event : events) {
                if (existing.getDate().equals(event.getDate())) { // skip
                    //System.out.println("Date even");
                    if (existing.getLocation().equals(event.getLocation()) || (existing.getLatPoint() == event.getLatPoint() && existing.getLngPoint() == event.getLngPoint())) { // location is the same?
                        // System.out.println("possible update on " + existing.getTitle() +": " + event.getShares() + " compared with " + existing.getShares());
                        if (existing.getShares() != event.getShares()) { // has the shares changed?
                            // update the event
                            existing.setShares(event.getShares()); // location sometimes is different because of the given location title
                            database.changeDBStateQuery(SqlQueryBuilder.updateEvents(existing));
                        }
                        // leave existing event untouched by deleting crawled event
                        events.remove(event);
                        break; // breaks the inner loop
                    }
                }
            }
        }

        // request to Server with the (shortened) events
        for (Event event : events) {
            database.changeDBStateQuery(SqlQueryBuilder.insertEvents(event));
        }
    }

    public synchronized void updateEvent(Event event) {
        openDatabase();
        database.changeDBStateQuery(SqlQueryBuilder.updateEvents(event));
    }

    public synchronized void deleteEvents(ArrayList<Event> events) {
        if (!events.isEmpty()) { // if the eventList is empty and we open the db --> ugly
            openDatabase();
        }
        // request to Server
        for (Event event : events) {
            database.changeDBStateQuery(SqlQueryBuilder.deleteEvent(event));
        }
    }

    private synchronized void openDatabase() {
        try {
            database = new EioDatabaseHandler();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String convertDate(String date) {

        String dateRevert = "";

        // String formatting is faster than date parsing and enables like search in query
        int lastBreak = date.length();
        for (int j = date.length() - 1; j >= 0; j--) {
            if (date.charAt(j) == '.') {
                dateRevert = dateRevert + date.substring(j + 1, lastBreak) + '-';
                lastBreak = j;
            }
        }

        return dateRevert + date.substring(0, lastBreak); // last time will be no dot
    }

    public static String revertDate(String date) {
        String dateRevert = "";

        // String formatting is faster than date parsing and enables like search in query
        int lastBreak = date.length();
        for (int j = date.length() - 1; j >= 0; j--) {
            if (date.charAt(j) == '-') {
                dateRevert = dateRevert + date.substring(j + 1, lastBreak) + '.';
                lastBreak = j;
            }
        }

        return dateRevert + date.substring(0, lastBreak); // last time will be no dot
    }

}
