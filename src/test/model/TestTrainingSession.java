package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestTrainingSession {
    private TrainingSession testSession1;
    private TrainingSession testSession2;
    private Date date1;
    private Date date2;
    private Date date3;

    @BeforeEach
    void runBefore() {
        testSession1 = new TrainingSession();
        testSession2 = new TrainingSession();

        date1 = new Date(31, 12, 2026);
        date2 = new Date(1, 1, 2027);
        date3 = new Date(11, 23, 2028);
    }

    @Test
    void testConstructor() {
        assertEquals(null, testSession1.getDate());
        assertEquals(0, testSession1.getDuration());
        assertEquals("", testSession1.getSkills());
        assertEquals("", testSession1.getSkills());
    }


    @Test
    void testSetDate() {
        //last day of year
        testSession1.setDate(date1);
        //first day of year
        testSession2.setDate(date2);

        assertEquals(date1, testSession1.getDate());
        assertEquals(date2, testSession2.getDate());

        //set date of a session again
        testSession2.setDate(date3);
        assertEquals(date3, testSession2.getDate());

    }

    @Test
    void testSetDuration() {
        testSession1.setDuration(1); // minimum
        testSession2.setDuration(60);

        assertEquals(1, testSession1.getDuration());
        assertEquals(60, testSession2.getDuration());

        //set duration of a session again
        testSession2.setDuration(300);
        assertEquals(300, testSession2.getDate());

    }

    @Test
    void testSetSkills() {
        testSession1.setSkills("skill 1, skill 2");
        testSession2.setSkills(""); //no skills specified

        assertEquals("skill 1,, skill 2", testSession1.getSkills());
        assertEquals("", testSession2.getSkills());

        //set skill of a session again
        testSession1.setSkills("skill 3");
        assertEquals("skill 3", testSession1.getSkills());
    }

    @Test
    void testSetNotes() {
        testSession1.setNotes("notes blah blah");
        testSession2.setNotes(""); //no skills specified

        assertEquals("notes blah blah", testSession1.getNotes());
        assertEquals("", testSession2.getNotes());

        //set date of a session again, with special characters
        testSession1.setSkills("new notes: / $ @");
        assertEquals("new notes: / $ @", testSession1.getNotes());
    }

}
