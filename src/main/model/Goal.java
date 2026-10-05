package model;

import java.time.LocalDate;

import org.json.JSONObject;

import persistence.Writable;

// Represents a training goal the player is working towards: a title, a
// description, the date they want to finish it by, and whether it is done yet.
//
// Whether the goal is completed is the only thing about a goal that changes
// over time, so it is the only field that is not final.
public class Goal implements Writable {

    private final String title;
    private final String description;
    private final LocalDate targetDate;
    private boolean completed;

    // REQUIRES: title != null, description != null, targetDate != null
    // EFFECTS: constructs a goal with the given title, description, and target
    //          completion date, which is not completed yet
    public Goal(String title, String description, LocalDate targetDate) {
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
    //          ISO-8601 string such as "2026-12-31"
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
