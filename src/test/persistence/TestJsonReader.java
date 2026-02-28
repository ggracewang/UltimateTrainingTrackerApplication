package persistence;

import model.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Referenced from JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

public class TestJsonReader {
    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            TrainingLog tl = reader.readTrainingLog();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyLogs() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyLogs.json");
        try {
            TrainingLog tl = reader.readTrainingLog();
            GoalLog gl = reader.readGoalLog();
            assertEquals(0, tl.getTrainingLog().size());
            assertEquals(0, gl.getAllGoals().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralLogs() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralLogs.json");
        try {
            TrainingLog tl = reader.readTrainingLog();
            GoalLog gl = reader.readGoalLog();
            
            List<TrainingSession> sessions = tl.getTrainingLog();
            assertEquals(2, sessions.size());
            assertEquals(60, sessions.get(0).getDuration());
            assertEquals("Forehand, Backhand", sessions.get(0).getSkills());
            
            List<Goal> goals = gl.getAllGoals();
            assertEquals(2, goals.size());
            assertEquals("Master huck", goals.get(0).getTitle());
            assertFalse(goals.get(0).getCompletionStatus());
            assertTrue(goals.get(1).getCompletionStatus());
            
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}
