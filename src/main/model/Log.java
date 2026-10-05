package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

// Represents a log that holds a list of items, records an event every time the
// list changes, and can write itself to JSON.
//
// This is the shared behaviour of TrainingLog and GoalLog. Each subclass only
// has to say what its items are called and how to describe one in words;
// adding, removing, counting, and saving are all handled here once.
public abstract class Log<T extends Writable> implements Writable {

    protected final List<T> items;

    // EFFECTS: constructs an empty log
    protected Log() {
        items = new ArrayList<>();
    }

    // REQUIRES: item != null
    // MODIFIES: this
    // EFFECTS: adds the given item to this log and records an event saying so
    public void add(T item) {
        items.add(item);
        logEvent(describe(item) + " added to " + getLogName() + ".");
    }

    // REQUIRES: item != null
    // MODIFIES: this
    // EFFECTS: removes the given item from this log and records an event saying
    //          so, returning true; returns false if the item was not in the log
    public boolean remove(T item) {
        if (!items.remove(item)) {
            return false;
        }
        logEvent(describe(item) + " removed from " + getLogName() + ".");
        return true;
    }

    // REQUIRES: item != null
    // MODIFIES: this
    // EFFECTS: adds the given item to this log WITHOUT recording an event. Used
    //          when rebuilding a saved log from file: those items are not new
    //          user actions, so logging each one would flood the event log.
    public void restore(T item) {
        items.add(item);
    }

    // EFFECTS: returns a read-only view of the items in this log, in the order
    //          they were added; callers cannot change the log through it
    public List<T> getAll() {
        return Collections.unmodifiableList(items);
    }

    // EFFECTS: returns how many items are in this log
    public int size() {
        return items.size();
    }

    // EFFECTS: returns true if this log holds no items
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // EFFECTS: returns this log as a JSON object, under this log's JSON key
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(getJsonKey(), itemsToJson());
        return json;
    }

    // EFFECTS: returns the items in this log as a JSON array
    private JSONArray itemsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (T item : items) {
            jsonArray.put(item.toJson());
        }
        return jsonArray;
    }

    // MODIFIES: EventLog.getInstance()
    // EFFECTS: records an event with the given description in the event log
    protected void logEvent(String description) {
        EventLog.getInstance().logEvent(new Event(description));
    }

    // EFFECTS: returns the name this log's items are stored under in JSON
    protected abstract String getJsonKey();

    // EFFECTS: returns this log's name, as it should appear in event messages
    protected abstract String getLogName();

    // REQUIRES: item != null
    // EFFECTS: returns a short description of the given item for event messages
    protected abstract String describe(T item);
}
