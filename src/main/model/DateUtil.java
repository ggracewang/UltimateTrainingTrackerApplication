package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

// Holds the one date format the whole app displays dates in, so that the
// session table, the goal table, the stats window, and the event log can never
// drift apart from each other.
//
// Excluded from the coverage report because a utility class like this one has a
// private constructor that exists only to stop anyone instantiating it, and
// that constructor can never be called by a test. The format method itself is
// tested in TestDateUtil.
@ExcludeFromJacocoGeneratedReport
public final class DateUtil {

    // the month/day/year format shown to the user, e.g. 12/31/2026
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("M/d/uuuu");

    // EFFECTS: prevents this utility class from being instantiated
    private DateUtil() {
    }

    // EFFECTS: returns the given date formatted for display, or "" if it is null
    public static String format(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DISPLAY_FORMAT);
    }
}
