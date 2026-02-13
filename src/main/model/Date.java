package model;

public class Date {

    // EFFECTS: constructs a date with given day, month, year
    public Date(int day, int month, int year) {

    }

    // REQUIRES: 31 >= day > 0, 12 >= month > 0, year > current year
    // EFFECTS: sets date to new, given date
    public void setDate(int day, int month, int year) {
        //TODO
    }


    // EFFECTS: returns the day 
    public int getDay() {
        return 0; //stub
    }

    // EFFECTS: returns the month 
    public int getMonth() {
        return 0; //stub
    }

    // EFFECTS: returns the month 
    public int getMonthInStringFormat() {
        return 0; //stub
    }

    // EFFECTS: returns the year
    public int getYear() {
        return 0; //stub
    }

    // REQUIRES: date != null
    // EFFECTS: returns the date in string format
    public String getFullDateInStringFormat() {
        return ""; //stub
    }
}
