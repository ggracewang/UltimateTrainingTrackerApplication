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
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseTrainingLog(jsonObject);
    }

    // EFFECTS: reads goal log from file and returns it;
    // throws IOException if an error occurs reading data from file
    public GoalLog readGoalLog() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseGoalLog(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
    
        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }
        
        return contentBuilder.toString();
    }

    // EFFECTS: parses training log from JSON object and returns it
    private TrainingLog parseTrainingLog(JSONObject jsonObject) {
        TrainingLog tl = new TrainingLog();
        JSONObject trainingLogJson = jsonObject.getJSONObject("trainingLog");
        addSessions(tl, trainingLogJson);
        return tl;
    }

    // MODIFIES: tl
    // EFFECTS: parses training sessions from JSON object and adds them to training log
    private void addSessions(TrainingLog tl, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("sessions");
        for (Object json : jsonArray) {
            JSONObject nextSession = (JSONObject) json;
            addSession(tl, nextSession);
        }
    }

    // MODIFIES: tl
    // EFFECTS: parses training session from JSON object and adds it to training log
    private void addSession(TrainingLog tl, JSONObject jsonObject) {
        JSONObject dateJson = jsonObject.getJSONObject("date");
        int day = dateJson.getInt("day");
        int month = dateJson.getInt("month");
        int year = dateJson.getInt("year");
        Date date = new Date(day, month, year);

        int duration = jsonObject.getInt("duration");
        String skills = jsonObject.getString("skills");
        String notes = jsonObject.getString("notes");

        TrainingSession session = new TrainingSession();
        session.setDate(date);
        session.setDuration(duration);
        session.setSkills(skills);
        session.setNotes(notes);

        tl.addSession(session);
    }

    // EFFECTS: parses goal log from JSON object and returns it
    private GoalLog parseGoalLog(JSONObject jsonObject) {
        GoalLog gl = new GoalLog();
        JSONObject goalLogJson = jsonObject.getJSONObject("goalLog");
        addGoals(gl, goalLogJson);
        return gl;
    }

    // MODIFIES: gl
    // EFFECTS: parses goals from JSON object and adds them to goal log
    private void addGoals(GoalLog gl, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("goals");
        for (Object json : jsonArray) {
            JSONObject nextGoal = (JSONObject) json;
            addGoal(gl, nextGoal);
        }
    }

    // MODIFIES: gl
    // EFFECTS: parses goal from JSON object and adds it to goal log
    private void addGoal(GoalLog gl, JSONObject jsonObject) {
        String title = jsonObject.getString("title");
        String description = jsonObject.getString("description");
        
        JSONObject dateJson = jsonObject.getJSONObject("date");
        int day = dateJson.getInt("day");
        int month = dateJson.getInt("month");
        int year = dateJson.getInt("year");
        Date date = new Date(day, month, year);

        boolean completionStatus = jsonObject.getBoolean("completionStatus");

        Goal goal = new Goal(title, description, date);
        if (completionStatus) {
            goal.markCompleted();
        }

        gl.addGoal(goal);
        }
}
