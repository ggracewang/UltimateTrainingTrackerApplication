package persistence;

import model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

// Referenced from JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

// Represents a reader that reads training tracker data from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads training log from file and returns it;
    // throws IOException if an error occurs reading data from file
    public TrainingLog readTrainingLog() throws IOException {
        return null;  // stub
    }

    // EFFECTS: reads goal log from file and returns it;
    // throws IOException if an error occurs reading data from file
    public GoalLog readGoalLog() throws IOException {
        return null;  // stub
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        return null;  // stub
    }

    // EFFECTS: parses training log from JSON object and returns it
    private TrainingLog parseTrainingLog(JSONObject jsonObject) {
        return null;  // stub
    }

    // MODIFIES: tl
    // EFFECTS: parses training sessions from JSON object and adds them to training log
    private void addSessions(TrainingLog tl, JSONObject jsonObject) {
        // stub
    }

    // MODIFIES: tl
    // EFFECTS: parses training session from JSON object and adds it to training log
    private void addSession(TrainingLog tl, JSONObject jsonObject) {
        // stub
    }

    // EFFECTS: parses goal log from JSON object and returns it
    private GoalLog parseGoalLog(JSONObject jsonObject) {
        return null;  // stub
    }

    // MODIFIES: gl
    // EFFECTS: parses goals from JSON object and adds them to goal log
    private void addGoals(GoalLog gl, JSONObject jsonObject) {
        // stub
    }

    // MODIFIES: gl
    // EFFECTS: parses goal from JSON object and adds it to goal log
    private void addGoal(GoalLog gl, JSONObject jsonObject) {
        // stub
    }
}
