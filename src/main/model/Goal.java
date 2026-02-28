package model;

import org.json.JSONObject;

public class Goal {
    private String title;
    private String description;
    private Date date;
    private Boolean completionStatus;
    
    // EFFECTS: constructs a new Goal with given title, description, completion date, 
    //          and set completion status to false
    public Goal(String title, String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.completionStatus = false;
    }

    // MODIFIES: this
    // EFFECTS: sets the goal's title to new, given title
    public void setTitle(String title) {
        this.title = title;
    }

    // MODIFIES: this
    // EFFECTS: sets the goal's description to new, given description
    public void setDescription(String description) {
        this.description = description;
    }

    // MODIFIES: this
    // EFFECTS: sets the goal's completion date to new, given date
    public void setCompletionDate(Date date) {
        this.date = date;
    }

    // MODIFIES: this
    // EFFECTS: marks a goal as completed by setting completion status to true
    public void markCompleted() {
        this.completionStatus = true;
    }

    // EFFECTS: returns title of the goal
    public String getTitle() {
        return title; 
    }

    // EFFECTS: returns description of the goal
    public String getDescription() {
        return description; 
    }

    // EFFECTS: returns completion date of the goal
    public Date getCompletionDate() {
        return date; 
    }

    // EFFECTS: returns completion status of the goal
    public boolean getCompletionStatus() {
        return completionStatus; 
    }

    // EFFECTS: returns this goal as a JSON object
    public JSONObject toJson() {
        return null;  // stub
    }

}