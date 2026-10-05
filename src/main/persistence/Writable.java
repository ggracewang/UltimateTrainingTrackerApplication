package persistence;

import org.json.JSONObject;

// Referenced from JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

// Represents anything that can describe itself as a JSON object so that it
// can be written to file. Implemented by every class the app saves.
public interface Writable {

    // EFFECTS: returns this object as a JSON object
    JSONObject toJson();
}
