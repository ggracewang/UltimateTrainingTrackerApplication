package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Goal;
import model.GoalLog;
import model.TrainingLog;
import model.TrainingSession;

// Referenced from JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

// Represents a reader that reads training tracker data from JSON data stored in file
public class JsonReader {

    private final String source;

    // EFFECTS: constructs reader to read from the given source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads the training log and the goal log from file and returns them
    //          together; throws IOException if the file cannot be read.
    //          The file is opened and parsed once, so both logs come from the
    //          same snapshot of it.
    public TrackerData read() throws IOException {
        JSONObject jsonObject = new JSONObject(readFile());
        return new TrackerData(parseTrainingLog(jsonObject), parseGoalLog(jsonObject));
    }

    // EFFECTS: reads the source file as a string and returns it
    private String readFile() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses the training log out of the given JSON object and returns it
    private TrainingLog parseTrainingLog(JSONObject jsonObject) {
        TrainingLog tl = new TrainingLog();
        JSONArray jsonArray = jsonObject.getJSONObject("trainingLog").getJSONArray("sessions");

        for (Object json : jsonArray) {
            tl.restore(parseSession((JSONObject) json));
        }

        return tl;
    }

    // EFFECTS: parses one training session out of the given JSON object
    private TrainingSession parseSession(JSONObject jsonObject) {
        return new TrainingSession(
                LocalDate.parse(jsonObject.getString("date")),
                jsonObject.getInt("duration"),
                jsonObject.getString("skills"),
                jsonObject.getString("notes"));
    }

    // EFFECTS: parses the goal log out of the given JSON object and returns it
    private GoalLog parseGoalLog(JSONObject jsonObject) {
        GoalLog gl = new GoalLog();
        JSONArray jsonArray = jsonObject.getJSONObject("goalLog").getJSONArray("goals");

        for (Object json : jsonArray) {
            gl.restore(parseGoal((JSONObject) json));
        }

        return gl;
    }

    // EFFECTS: parses one goal out of the given JSON object
    private Goal parseGoal(JSONObject jsonObject) {
        Goal goal = new Goal(
                jsonObject.getString("title"),
                jsonObject.getString("description"),
                LocalDate.parse(jsonObject.getString("targetDate")));

        if (jsonObject.getBoolean("completed")) {
            goal.markCompleted();
        }

        return goal;
    }
}
