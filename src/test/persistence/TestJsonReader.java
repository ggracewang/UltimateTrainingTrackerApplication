package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.Event;
import model.EventLog;
import model.Goal;
import model.TrainingSession;

// Referenced from JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

public class TestJsonReader {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass: there is no such file to read
        }
    }

    @Test
    void testReaderEmptyLogs() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyLogs.json");
        try {
            TrackerData data = reader.read();

            assertTrue(data.getTrainingLog().isEmpty());
            assertTrue(data.getGoalLog().isEmpty());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralLogsSessions() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralLogs.json");
        try {
            List<TrainingSession> sessions = reader.read().getTrainingLog().getAll();

            assertEquals(2, sessions.size());

            assertEquals(LocalDate.of(2025, 1, 15), sessions.get(0).getDate());
            assertEquals(67, sessions.get(0).getDuration());
            assertEquals("Forehand, Backhand", sessions.get(0).getSkills());
            assertEquals("", sessions.get(0).getNotes());

            assertEquals(LocalDate.of(2025, 1, 16), sessions.get(1).getDate());
            assertEquals(41, sessions.get(1).getDuration());
            assertEquals("Defense", sessions.get(1).getSkills());
            assertEquals("Improved", sessions.get(1).getNotes());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralLogsGoals() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralLogs.json");
        try {
            List<Goal> goals = reader.read().getGoalLog().getAll();

            assertEquals(2, goals.size());

            assertEquals("Master huck", goals.get(0).getTitle());
            assertEquals("Throw 50 meters", goals.get(0).getDescription());
            assertEquals(LocalDate.of(2025, 12, 31), goals.get(0).getTargetDate());
            assertFalse(goals.get(0).isCompleted());

            assertEquals("Improve vertical", goals.get(1).getTitle());
            assertEquals("Jump higher", goals.get(1).getDescription());
            assertEquals(LocalDate.of(2025, 6, 30), goals.get(1).getTargetDate());
            assertTrue(goals.get(1).isCompleted());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderDoesNotFloodTheEventLog() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralLogs.json");
        try {
            EventLog.getInstance().clear();
            reader.read();

            // loading is not a user action, so restoring the saved sessions and
            // goals must not add an event for each one
            List<String> descriptions = new ArrayList<>();
            for (Event e : EventLog.getInstance()) {
                descriptions.add(e.getDescription());
            }
            assertEquals(List.of("Event log cleared."), descriptions);
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
