package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestTrainingLog {

    private TrainingLog testTrainingLog;

    private TrainingSession session1;
    private TrainingSession session2;
    private TrainingSession session3;

    @BeforeEach
    void runBefore() {
        session1 = new TrainingSession(LocalDate.of(2026, 12, 31), 60,
                "Forehand, Backhand", "Morning practice");
        session2 = new TrainingSession(LocalDate.of(2027, 1, 1), 90,
                "Defense", "Evening practice");
        session3 = new TrainingSession(LocalDate.of(2028, 11, 23), 45,
                "Endurance", "Cardio");

        testTrainingLog = new TrainingLog();
    }

    @Test
    void testConstructor() {
        assertTrue(testTrainingLog.isEmpty());
        assertEquals(0, testTrainingLog.size());
        assertTrue(testTrainingLog.getAll().isEmpty());
        assertEquals(0, testTrainingLog.getTotalDurationPracticed());
    }

    @Test
    void testAddSession() {
        testTrainingLog.add(session1);
        assertEquals(1, testTrainingLog.size());
        assertFalse(testTrainingLog.isEmpty());
        assertEquals(session1, testTrainingLog.getAll().get(0));

        testTrainingLog.add(session2);
        testTrainingLog.add(session3);
        assertEquals(3, testTrainingLog.size());
        assertEquals(session1, testTrainingLog.getAll().get(0));
        assertEquals(session2, testTrainingLog.getAll().get(1));
        assertEquals(session3, testTrainingLog.getAll().get(2));
    }

    @Test
    void testAddSessionRecordsEvent() {
        EventLog.getInstance().clear();
        testTrainingLog.add(session1);

        String lastDescription = lastEventDescription();
        assertEquals("60 min training session on 12/31/2026 added to Training Log.", lastDescription);
    }

    @Test
    void testRemoveExistingSession() {
        testTrainingLog.add(session1);
        testTrainingLog.add(session2);
        testTrainingLog.add(session3);

        assertTrue(testTrainingLog.remove(session1));

        assertEquals(2, testTrainingLog.size());
        assertEquals(session2, testTrainingLog.getAll().get(0));
        assertEquals(session3, testTrainingLog.getAll().get(1));
        assertFalse(testTrainingLog.getAll().contains(session1));
        assertEquals(135, testTrainingLog.getTotalDurationPracticed());
    }

    @Test
    void testRemoveSessionNotInLog() {
        testTrainingLog.add(session1);
        testTrainingLog.add(session2);

        TrainingSession strangerSession = new TrainingSession(LocalDate.of(2026, 5, 5), 30, "", "");
        assertFalse(testTrainingLog.remove(strangerSession));

        assertEquals(2, testTrainingLog.size());
        assertEquals(session1, testTrainingLog.getAll().get(0));
        assertEquals(session2, testTrainingLog.getAll().get(1));
    }

    @Test
    void testRemoveSessionRecordsEvent() {
        testTrainingLog.add(session1);
        EventLog.getInstance().clear();
        testTrainingLog.remove(session1);

        assertEquals("60 min training session on 12/31/2026 removed from Training Log.",
                lastEventDescription());
    }

    @Test
    void testRemoveSessionNotInLogRecordsNoEvent() {
        EventLog.getInstance().clear();
        testTrainingLog.remove(session1);

        // clear() logs one event of its own, and nothing should have been added after it
        assertEquals("Event log cleared.", lastEventDescription());
    }

    @Test
    void testRemoveMultipleSessions() {
        testTrainingLog.add(session1);
        testTrainingLog.add(session2);
        testTrainingLog.add(session3);

        assertTrue(testTrainingLog.remove(session1));
        assertTrue(testTrainingLog.remove(session3));

        assertEquals(1, testTrainingLog.size());
        assertEquals(session2, testTrainingLog.getAll().get(0));
        assertEquals(90, testTrainingLog.getTotalDurationPracticed());
    }

    @Test
    void testRemoveAllSessions() {
        testTrainingLog.add(session1);
        testTrainingLog.add(session2);
        testTrainingLog.add(session3);

        testTrainingLog.remove(session1);
        testTrainingLog.remove(session2);
        testTrainingLog.remove(session3);

        assertTrue(testTrainingLog.isEmpty());
        assertEquals(0, testTrainingLog.size());
        assertEquals(0, testTrainingLog.getTotalDurationPracticed());
    }

    @Test
    void testRestoreDoesNotRecordEvent() {
        EventLog.getInstance().clear();
        testTrainingLog.restore(session1);
        testTrainingLog.restore(session2);

        assertEquals(2, testTrainingLog.size());
        assertEquals(session1, testTrainingLog.getAll().get(0));
        assertEquals(session2, testTrainingLog.getAll().get(1));
        assertEquals("Event log cleared.", lastEventDescription());
    }

    @Test
    void testGetAllIsReadOnly() {
        testTrainingLog.add(session1);
        assertThrows(UnsupportedOperationException.class, () -> testTrainingLog.getAll().add(session2));
        assertEquals(1, testTrainingLog.size());
    }

    @Test
    void testGetTotalDurationPracticed() {
        assertEquals(0, testTrainingLog.getTotalDurationPracticed());

        testTrainingLog.add(session1);
        assertEquals(60, testTrainingLog.getTotalDurationPracticed());

        testTrainingLog.add(session2);
        testTrainingLog.add(session3);
        assertEquals(195, testTrainingLog.getTotalDurationPracticed());
    }

    @Test
    void testGetTotalHoursPracticed() {
        assertEquals(0.0, testTrainingLog.getTotalHoursPracticed(), 0.001);

        testTrainingLog.add(session1);
        assertEquals(1.0, testTrainingLog.getTotalHoursPracticed(), 0.001);

        testTrainingLog.add(session2);
        assertEquals(2.5, testTrainingLog.getTotalHoursPracticed(), 0.001);
    }

    @Test
    void testGetAverageSessionDuration() {
        // boundary case: no sessions, so no division by zero
        assertEquals(0.0, testTrainingLog.getAverageSessionDuration(), 0.001);

        testTrainingLog.add(session1);
        assertEquals(60.0, testTrainingLog.getAverageSessionDuration(), 0.001);

        testTrainingLog.add(session2);
        assertEquals(75.0, testTrainingLog.getAverageSessionDuration(), 0.001);

        testTrainingLog.add(session3);
        assertEquals(65.0, testTrainingLog.getAverageSessionDuration(), 0.001);
    }

    @Test
    void testGetLongestSessionDuration() {
        assertEquals(0, testTrainingLog.getLongestSessionDuration());

        testTrainingLog.add(session1);
        assertEquals(60, testTrainingLog.getLongestSessionDuration());

        testTrainingLog.add(session2); // longer, so the longest changes
        assertEquals(90, testTrainingLog.getLongestSessionDuration());

        testTrainingLog.add(session3); // shorter, so the longest stays put
        assertEquals(90, testTrainingLog.getLongestSessionDuration());
    }

    @Test
    void testToJsonEmptyLog() {
        JSONObject json = testTrainingLog.toJson();
        assertEquals(0, json.getJSONArray("sessions").length());
    }

    @Test
    void testToJsonWithSessions() {
        testTrainingLog.add(session1);
        testTrainingLog.add(session2);

        JSONArray sessions = testTrainingLog.toJson().getJSONArray("sessions");
        assertEquals(2, sessions.length());

        JSONObject firstSession = sessions.getJSONObject(0);
        assertEquals("2026-12-31", firstSession.getString("date"));
        assertEquals(60, firstSession.getInt("duration"));
        assertEquals("Forehand, Backhand", firstSession.getString("skills"));
    }

    // EFFECTS: returns the description of the most recent event in the event log
    private String lastEventDescription() {
        String description = "";
        for (Event e : EventLog.getInstance()) {
            description = e.getDescription();
        }
        return description;
    }
}
