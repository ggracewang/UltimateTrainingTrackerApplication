package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import model.Goal;
import model.GoalLog;
import model.TrainingLog;
import model.TrainingSession;

// Referenced from JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

public class TestJsonWriter {

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass: that file name cannot be opened for writing
        }
    }

    @Test
    void testWriterEmptyLogs() {
        try {
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyLogs.json");
            writer.open();
            writer.write(new TrainingLog(), new GoalLog());
            writer.close();

            TrackerData data = new JsonReader("./data/testWriterEmptyLogs.json").read();
            assertTrue(data.getTrainingLog().isEmpty());
            assertTrue(data.getGoalLog().isEmpty());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralLogs() {
        try {
            writeGeneralLogs();

            TrackerData data = new JsonReader("./data/testWriterGeneralLogs.json").read();

            assertEquals(1, data.getTrainingLog().size());
            TrainingSession session = data.getTrainingLog().getAll().get(0);
            assertEquals(LocalDate.of(2025, 1, 15), session.getDate());
            assertEquals(60, session.getDuration());
            assertEquals("Forehand", session.getSkills());
            assertEquals("Good", session.getNotes());

            assertEquals(1, data.getGoalLog().size());
            Goal goal = data.getGoalLog().getAll().get(0);
            assertEquals("Test Goal", goal.getTitle());
            assertEquals("Test Description", goal.getDescription());
            assertEquals(LocalDate.of(2025, 12, 31), goal.getTargetDate());
            assertTrue(goal.isCompleted());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    // MODIFIES: ./data/testWriterGeneralLogs.json
    // EFFECTS: writes one training session and one completed goal to the test
    //          file; throws IOException if the file cannot be opened
    private void writeGeneralLogs() throws IOException {
        TrainingLog tl = new TrainingLog();
        tl.add(new TrainingSession(LocalDate.of(2025, 1, 15), 60, "Forehand", "Good"));

        GoalLog gl = new GoalLog();
        Goal goal = new Goal("Test Goal", "Test Description", LocalDate.of(2025, 12, 31));
        gl.add(goal);
        gl.markCompleted(goal);

        JsonWriter writer = new JsonWriter("./data/testWriterGeneralLogs.json");
        writer.open();
        writer.write(tl, gl);
        writer.close();
    }
}
