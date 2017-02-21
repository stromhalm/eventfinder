package de.Events;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dario on 01.06.15.
 */
public class Event {

	/**
	 * Varios Methods of a event, that can store all attributes within our EIO databse-event-table
	 */
	private String date; // notice this event has to be reverted mostly
	private String title;
	private String description;
	private String location;
	private double latPoint;
	private double lngPoint;
	private String categories;
	private String infoUrl;
	private int shares;

	/**
	 * Constructor
	 */

	public Event() {
	}

	public Event(String date, String location, int shares) {
		this.date = date;
		this.location = location;
		this.shares = shares;
	}

	/**
	 * Getter and Setter
	 */
	public String getInfoUrl() {
		return this.infoUrl;
	}

	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}

	public String getCategories() {
		return this.categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public double getLatPoint() {
		return this.latPoint;
	}

	public void setLatPoint(double latPoint) {
		this.latPoint = latPoint;
	}

	public double getLngPoint() {
		return this.lngPoint;
	}

	public void setLngPoint(double lngPoint) {
		this.lngPoint = lngPoint;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTitle() {

		return this.title;
	}

	public String getDescription() {

		if (this.description.length() > 250) { // trim if too long
			for (int i = 250; i < description.length(); i++) {
				if (this.description.charAt(i) == ' ') { // is blank
					return description.substring(0, i) + " [...]";
				} // else description has nearly ended so print all
			}
		}

		return this.description; // else return the normal description which is not too long
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getShares() {
		return shares;
	}

	public void setShares(int shares) {
		this.shares = shares;
	}

	public boolean isToday() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		String currentDate = sdfDate.format(now);
		if(currentDate.equals(this.getDate())) {
			return true;
		}
		return false;
	}

    public boolean isTomorrow(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + (1 * 24 * 60 * 60 * 1000));
        String currentDate = sdfDate.format(tomorrow);
        if((currentDate).equals(this.getDate())){
            return true;
        }
        return false;
    }

    public boolean isInNextThreeDays(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        Date twoDays = new Date(now.getTime() + (2 * 24 * 60 * 60 * 1000));
        String twoDayString = sdfDate.format(twoDays);
        Date threeDays = new Date(now.getTime() + (3 * 24 * 60 * 60 * 1000));
        String threeDayString = sdfDate.format(threeDays);

        if(twoDayString.equals(this.getDate()) || threeDayString.equals(this.getDate())){
            return true;
        }
        return false;
    }

    public boolean isNextWeek(){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        Date fourDays = new Date(now.getTime() + (4 * 24 * 60 * 60 * 1000));
        String fourDayString = sdfDate.format(fourDays);
        Date fiveDays = new Date(now.getTime() + (5 * 24 * 60 * 60 * 1000));
        String fiveDayString = sdfDate.format(fiveDays);
        Date sixDays = new Date(now.getTime() + (6 * 24 * 60 * 60 * 1000));
        String sixDayString = sdfDate.format(sixDays);
        Date sevenDays = new Date(now.getTime() + (7 * 24 * 60 * 60 * 1000));
        String sevenDayString = sdfDate.format(sevenDays);

        if(fourDayString.equals(this.getDate()) || fiveDayString.equals(this.getDate()) || sixDayString.equals(this.getDate()) ||  sevenDayString.equals(this.getDate())){
            return true;
        }
        return false;
    }


}