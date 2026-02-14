package model;

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

    // EFFECTS: returns the month 
    public String getMonthInStringFormat() {
        String stringMonth = "";
        if (month == 1) {
            stringMonth = "January";
        } else if (month == 2) {
            stringMonth = "February";
        } else if (month == 3) {
            stringMonth = "March";
        } else if (month == 4) {
            stringMonth = "April";
        } else if (month == 5) {
            stringMonth = "May";
        } else if (month == 6) {
            stringMonth = "June";
        } else if (month == 7) {
            stringMonth = "July";
        } else if (month == 8) {
            stringMonth = "August";
        } else if (month == 9) {
            stringMonth = "September";
        } else if (month == 10) {
            stringMonth = "October";
        } else if (month == 11) {
            stringMonth = "November";
        } else {
            stringMonth = "December";
        }
        return stringMonth;
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
}
