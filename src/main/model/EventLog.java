package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

// REFERENCED FROM ALARM SYSTEM DEMO (COPIED)

/**
 * Represents a log of training tracker system events.
 * We use the Singleton Design Pattern to ensure that there is only
 * one EventLog in the system and that the system has global access
 * to the single instance of the EventLog.
 */
public class EventLog implements Iterable<Event> {
    /** the only EventLog in the system (Singleton Design Pattern) */
    private static EventLog theLog;
    private Collection<Event> events;

    // EFFECTS: constructs and EventLog that is empty
    private EventLog() {
        events = new ArrayList<Event>();
    }

    // EFFECTS: returns the instance of EventLog. if it doesn't exist, creates it
    public static EventLog getInstance() {
        if (theLog == null) {
            theLog = new EventLog();
        }
        return theLog;
    }

    // MODIFIES: this
    // EFFECTS: adds the given event to the log
    public void logEvent(Event e) {
        events.add(e);
    }

    // MODIFIES: this 
    // EFFECTS: clears the log and adds a new "Event log cleared." event
    public void clear() {
        events.clear();
        logEvent(new Event("Event log cleared."));
    }

    @Override
    // EFFECTS: returns an iterator over the events in the log
    public Iterator<Event> iterator() {
        return events.iterator();
    }
}
