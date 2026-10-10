package persistence;

// Holds the four summary numbers the app reports about training sessions.
//
// They arrive together because the database works them all out in one pass
// over the rows, in a single query, rather than being asked four separate
// times. Bundling them keeps that fact visible in the code.
public class SessionStats {

    private static final int MINUTES_PER_HOUR = 60;

    private final int sessionCount;
    private final int totalMinutes;
    private final double averageMinutes;
    private final int longestMinutes;

    // REQUIRES: every value >= 0
    // EFFECTS: constructs a set of session statistics with the given values
    public SessionStats(int sessionCount, int totalMinutes,
            double averageMinutes, int longestMinutes) {
        this.sessionCount = sessionCount;
        this.totalMinutes = totalMinutes;
        this.averageMinutes = averageMinutes;
        this.longestMinutes = longestMinutes;
    }

    // EFFECTS: returns how many sessions have been logged
    public int getSessionCount() {
        return sessionCount;
    }

    // EFFECTS: returns the total number of minutes practised
    public int getTotalMinutes() {
        return totalMinutes;
    }

    // EFFECTS: returns the total number of hours practised
    public double getTotalHours() {
        return (double) totalMinutes / MINUTES_PER_HOUR;
    }

    // EFFECTS: returns the average length in minutes of a session, or 0 if no
    //          sessions have been logged
    public double getAverageMinutes() {
        return averageMinutes;
    }

    // EFFECTS: returns the length in minutes of the longest session, or 0 if no
    //          sessions have been logged
    public int getLongestMinutes() {
        return longestMinutes;
    }

    // EFFECTS: returns true if no sessions have been logged
    public boolean isEmpty() {
        return sessionCount == 0;
    }
}
