package model;

import java.time.LocalDate;

import org.json.JSONObject;

import persistence.Writable;

// Represents a training goal the player is working towards: a title, a
// description, the date they want to finish it by, and whether it is done yet.
//
// Whether the goal is completed is the only thing about a goal that changes
// over time, so it is the only field that is not final.
//
// A goal also carries an id, which is the number the database uses to tell one
// saved row from another. A goal that has not been saved to the database yet
// has the id NO_ID, because the database is what hands out real ids.
public class Goal implements Writable {

    // the id of a goal that is not in the database yet; real ids start at 1
    public static final int NO_ID = 0;

    private final int id;
    private final String title;
    private final String description;
    private final LocalDate targetDate;
    private boolean completed;

    // REQUIRES: title != null, description != null, targetDate != null
    // EFFECTS: constructs a new goal that is not in the database yet, with the
    //          given title, description, and target completion date, and which
    //          is not completed
    public Goal(String title, String description, LocalDate targetDate) {
        this(NO_ID, title, description, targetDate);
    }

    // REQUIRES: id >= 0, title != null, description != null, targetDate != null
    // EFFECTS: constructs a goal that already has the given database id. Used
    //          when rebuilding a goal that was read back out of the database,
    //          so that it still knows which row it came from.
    public Goal(int id, String title, String description, LocalDate targetDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetDate = targetDate;
        this.completed = false;
    }

    // MODIFIES: this
    // EFFECTS: marks this goal as completed
    public void markCompleted() {
        this.completed = true;
    }

    // EFFECTS: returns the database id of this goal, or NO_ID if it has not
    //          been saved to the database yet
    public int getId() {
        return id;
    }

    // EFFECTS: returns true if this goal has been saved to the database
    public boolean isSaved() {
        return id != NO_ID;
    }

    // EFFECTS: returns the title of this goal
    public String getTitle() {
        return title;
    }

    // EFFECTS: returns the description of this goal
    public String getDescription() {
        return description;
    }

    // EFFECTS: returns the date this goal is meant to be completed by
    public LocalDate getTargetDate() {
        return targetDate;
    }

    // EFFECTS: returns this goal's target date formatted for display
    public String getTargetDateAsString() {
        return DateUtil.format(targetDate);
    }

    // EFFECTS: returns true if this goal has been completed
    public boolean isCompleted() {
        return completed;
    }

    // EFFECTS: returns true if this goal is unfinished and its target date has
    //          already passed
    public boolean isOverdue() {
        return !completed && targetDate.isBefore(LocalDate.now());
    }

    // EFFECTS: returns "Completed", "Overdue", or "In progress" for this goal
    public String getStatus() {
        if (completed) {
            return "Completed";
        } else if (isOverdue()) {
            return "Overdue";
        }
        return "In progress";
    }

    // EFFECTS: returns this goal as a JSON object, storing the target date as an
    //          ISO-8601 string such as "2026-12-31". The id is left out on
    //          purpose: it belongs to the database, not to the save file.
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("description", description);
        json.put("targetDate", targetDate.toString());
        json.put("completed", completed);
        return json;
    }
}
