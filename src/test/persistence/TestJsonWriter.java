package persistence;

import model.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class TestJsonWriter {
    @Test
    void testWriterInvalidFile() {
        try {
            TrainingLog tl = new TrainingLog();
            GoalLog gl = new GoalLog();
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyLogs() {
        try {
            TrainingLog tl = new TrainingLog();
            GoalLog gl = new GoalLog();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyLogs.json");
            writer.open();
            writer.write(tl, gl);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyLogs.json");
            tl = reader.readTrainingLog();
            gl = reader.readGoalLog();
            assertEquals(0, tl.getTrainingLog().size());
            assertEquals(0, gl.getAllGoals().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralLogs() {
        try {
            TrainingLog tl = new TrainingLog();
            GoalLog gl = new GoalLog();
            TrainingSession session = new TrainingSession();
            session.setDate(new Date(15, 1, 2025));
            session.setDuration(60);
            session.setSkills("Forehand");
            session.setNotes("Good");
            tl.addSession(session);

            gl.addGoal(new Goal("Test Goal", "Test Description", new Date(31, 12, 2025)));

            JsonWriter writer = new JsonWriter("./data/testWriterGeneralLogs.json");
            writer.open();
            writer.write(tl, gl);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralLogs.json");
            tl = reader.readTrainingLog();
            gl = reader.readGoalLog();
            
            assertEquals(1, tl.getTrainingLog().size());
            assertEquals(1, gl.getAllGoals().size());
            assertEquals("Test Goal", gl.getAllGoals().get(0).getTitle());

        } catch (IOException e) { 
            fail("Exception should not have been thrown");
        }
    }
}
