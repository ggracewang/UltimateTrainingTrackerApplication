package persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.JSONObject;

import model.GoalLog;
import model.TrainingLog;

// Referenced from JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

// Represents a writer that writes JSON representation of training tracker data to file
public class JsonWriter {

    private static final int TAB = 4;

    private final String destination;
    private PrintWriter writer;

    // EFFECTS: constructs a writer to write to the given destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens the writer; throws FileNotFoundException if the destination
    //          file cannot be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes the JSON representation of the given training log and goal
    //          log to file
    public void write(TrainingLog tl, GoalLog gl) {
        JSONObject json = new JSONObject();
        json.put("trainingLog", tl.toJson());
        json.put("goalLog", gl.toJson());
        writer.print(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes the writer
    public void close() {
        writer.close();
    }
}
