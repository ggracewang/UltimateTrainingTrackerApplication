package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Referenced from the AlarmSystem demo tests
public class TestEvent {
    private static final int HASH_CONSTANT = 13;

    private Event testEvent;
    private Date timeBefore;
    private Date timeAfter;

    @BeforeEach
    void runBefore() {
        timeBefore = new Date();
        testEvent = new Event("60 min training session on 12/31/2026 added to Training Log.");
        timeAfter = new Date();
    }

    @Test
    void testConstructor() {
        assertEquals("60 min training session on 12/31/2026 added to Training Log.",
                testEvent.getDescription());

        // the event stamps itself with the time it was created, so its timestamp
        // must fall between the readings taken either side of it
        assertFalse(testEvent.getDate().before(timeBefore));
        assertFalse(testEvent.getDate().after(timeAfter));
    }

    @Test
    void testEqualsSameEvent() {
        assertTrue(testEvent.equals(testEvent));
    }

    @Test
    void testEqualsNull() {
        assertFalse(testEvent.equals(null));
    }

    @Test
    void testEqualsDifferentClass() {
        assertFalse(testEvent.equals("not an event"));
    }

    @Test
    void testEqualsDifferentDescription() {
        Event otherEvent = new Event("Goal \"Master huck\" added to Goal Log.");
        assertFalse(testEvent.equals(otherEvent));
        assertNotEquals(testEvent.hashCode(), otherEvent.hashCode());
    }

    @Test
    void testHashCode() {
        int expected = HASH_CONSTANT * testEvent.getDate().hashCode()
                + testEvent.getDescription().hashCode();
        assertEquals(expected, testEvent.hashCode());
    }

    @Test
    void testToString() {
        String expected = testEvent.getDate().toString() + "\n" + testEvent.getDescription();
        assertEquals(expected, testEvent.toString());
    }
}
