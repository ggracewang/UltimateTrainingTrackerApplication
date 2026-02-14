package model;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestTrainingLog {

    private TrainingLog testTrainingLog;

    private TrainingSession session1; 
    private TrainingSession session2;
    private TrainingSession session3;

    private Date date1;
    private Date date2;
    private Date date3;

    @BeforeEach
    void runBefore() {

        session1 = new TrainingSession();
        session1.setDate(date1);
        session1.setDuration(60);
        session1.setNotes("Morning practice");
        session1.setSkills("Forehand, Backhand");

        session2 = new TrainingSession();
        session2.setDate(date2);
        session2.setDuration(90);
        session2.setNotes("Evening practice");
        session2.setSkills("Defense");
        
        session3 = new TrainingSession();
        session3.setDate(date3);
        session3.setDuration(45);
        session3.setNotes("Cardio");
        session3.setSkills("endurance");

        date1 = new Date(31, 12, 2026);
        date2 = new Date(1, 1, 2027);
        date3 = new Date(11, 23, 2028);

        testTrainingLog = new TrainingLog();

    }

    @Test
    void testConstructor() {
        assertTrue(testTrainingLog.getTrainingLog().isEmpty());
    }

    @Test
    void testAddSession() {
        assertEquals(0, testTrainingLog.getTotalDurationPracticed()); //no sessions added yet
        assertTrue(testTrainingLog.getTrainingLog().isEmpty());

        testTrainingLog.addSession(session1);
        assertEquals(1, testTrainingLog.getTrainingLog().size());
        assertEquals(session1, testTrainingLog.getTrainingLog().get(0));

        testTrainingLog.addSession(session2);
        testTrainingLog.addSession(session3);
        assertEquals(3, testTrainingLog.getTrainingLog().size()); //multiple sessions
        assertEquals(session1, testTrainingLog.getTrainingLog().get(0));
        assertEquals(session2, testTrainingLog.getTrainingLog().get(1));
        assertEquals(session3, testTrainingLog.getTrainingLog().get(2));
    }

    @Test
    void testRemoveSession() {
        testTrainingLog.addSession(session1);
        testTrainingLog.addSession(session2);
        testTrainingLog.addSession(session3);

        testTrainingLog.removeSession(session1);
        assertEquals(2, testTrainingLog.getTrainingLog().size());
        assertEquals(session2, testTrainingLog.getTrainingLog().get(0));
        assertEquals(session3, testTrainingLog.getTrainingLog().get(1));
        assertFalse(testTrainingLog.getTrainingLog().contains(session1));
        assertEquals(135, testTrainingLog.getTotalDurationPracticed());

        TrainingSession newSession = new TrainingSession();
        testTrainingLog.removeSession(newSession); // session that isn't in training log (nothing changes)
        assertEquals(2, testTrainingLog.getTrainingLog().size());
        assertEquals(session2, testTrainingLog.getTrainingLog().get(0));
        assertEquals(session3, testTrainingLog.getTrainingLog().get(1));
        assertFalse(testTrainingLog.getTrainingLog().contains(session1));
        assertEquals(135, testTrainingLog.getTotalDurationPracticed());

        testTrainingLog.removeSession(session3);
        assertEquals(1, testTrainingLog.getTrainingLog().size());
        assertEquals(session2, testTrainingLog.getTrainingLog().get(0));
        assertFalse(testTrainingLog.getTrainingLog().contains(session1));
        assertFalse(testTrainingLog.getTrainingLog().contains(session3));
        assertEquals(90, testTrainingLog.getTotalDurationPracticed());

        testTrainingLog.removeSession(session2);//no more sessions 
        assertTrue(testTrainingLog.getTrainingLog().isEmpty());
        assertEquals(0, testTrainingLog.getTrainingLog().size());
        assertFalse(testTrainingLog.getTrainingLog().contains(session1));
        assertFalse(testTrainingLog.getTrainingLog().contains(session2));
        assertFalse(testTrainingLog.getTrainingLog().contains(session3));
        assertEquals(0, testTrainingLog.getTotalDurationPracticed());
    }

    @Test
    void testGetTotalDurationPracticed() {
        assertEquals(0, testTrainingLog.getTotalDurationPracticed()); //no sessions added yet

        testTrainingLog.addSession(session1);
        assertEquals(60, testTrainingLog.getTotalDurationPracticed()); //one session

        testTrainingLog.addSession(session2);
        testTrainingLog.addSession(session3);
        assertEquals(195, testTrainingLog.getTotalDurationPracticed()); //multiple sessions
    }
}
