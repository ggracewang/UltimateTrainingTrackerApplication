package model;

import org.json.JSONObject;


public class Date {
    private Integer day;
    private Integer month;
    private Integer year;

    // EFFECTS: constructs a date with given day, month, year
    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // REQUIRES: 31 >= day > 0, 12 >= month > 0, year > current year
    // EFFECTS: sets date to new, given date
    public void setDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }


    // EFFECTS: returns the day 
    public int getDay() {
        return day; 
    }

    // EFFECTS: returns the month 
    public int getMonth() {
        return month; //stub
    }

    // EFFECTS: returns the year
    public int getYear() {
        return year;
    }

    // REQUIRES: date != null
    // EFFECTS: returns the date in string format
    public String getFullDateInStringFormat() {
        String stringYear = (getMonth() + "/" + getDay() + "/" + getYear());
        return stringYear; 
    }

    // EFFECTS: returns this date as JSON object
    public JSONObject toJson() {
    JSONObject json = new JSONObject();
    json.put("day", day);
    json.put("month", month);
    json.put("year", year);
    return json;
    }
}
