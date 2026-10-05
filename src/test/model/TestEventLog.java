package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Referenced from the AlarmSystem demo tests
public class TestEventLog {
    private Event event1;
    private Event event2;
    private Event event3;

    @BeforeEach
    void runBefore() {
        event1 = new Event("First event");
        event2 = new Event("Second event");
        event3 = new Event("Third event");

        // the event log is a singleton shared by the whole program, so each test
        // starts by emptying it
        EventLog.getInstance().clear();
    }

    @Test
    void testGetInstanceAlwaysReturnsTheSameLog() {
        assertSame(EventLog.getInstance(), EventLog.getInstance());
    }

    @Test
    void testClearLeavesOnlyTheClearedEvent() {
        List<Event> events = eventsInLog();

        assertEquals(1, events.size());
        assertEquals("Event log cleared.", events.get(0).getDescription());
    }

    @Test
    void testLogEvent() {
        EventLog.getInstance().logEvent(event1);

        List<Event> events = eventsInLog();
        assertEquals(2, events.size()); // the "cleared" event, then event1
        assertSame(event1, events.get(1));
    }

    @Test
    void testLogMultipleEventsKeepsThemInOrder() {
        EventLog.getInstance().logEvent(event1);
        EventLog.getInstance().logEvent(event2);
        EventLog.getInstance().logEvent(event3);

        List<Event> events = eventsInLog();
        assertEquals(4, events.size());
        assertSame(event1, events.get(1));
        assertSame(event2, events.get(2));
        assertSame(event3, events.get(3));
    }

    @Test
    void testClearAfterLoggingEvents() {
        EventLog.getInstance().logEvent(event1);
        EventLog.getInstance().logEvent(event2);
        assertEquals(3, eventsInLog().size());

        EventLog.getInstance().clear();

        List<Event> events = eventsInLog();
        assertEquals(1, events.size());
        assertEquals("Event log cleared.", events.get(0).getDescription());
        assertTrue(!events.contains(event1));
    }

    // EFFECTS: returns the events currently in the event log, in order, by
    //          walking the iterator the log hands out
    private List<Event> eventsInLog() {
        List<Event> events = new ArrayList<>();
        for (Event e : EventLog.getInstance()) {
            events.add(e);
        }
        return events;
    }
}
