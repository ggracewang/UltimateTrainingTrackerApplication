package model;

import org.json.JSONObject;

public class TrainingSession {
    private Date date;
    private Integer duration;
    private String skills;
    private String notes;

    // EFFECTS: constructs a training session with no date, duration (in mins), skills
    //          practiced, and notes
    public TrainingSession() {
        this.date = null;
        this.duration = 0;
        this.skills = "";
        this.notes = "";
    } 

    // REQUIRES: newDate != null
    // MODIFIES: this
    // EFFECTS: sets the training session's date to new, given date
    public void setDate(Date newDate) {
        this.date = newDate;
    }

    // REQUIRES: duration > 0
    // MODIFIES: this
     // EFFECTS: sets the training session's duration to new, given duration
    public void setDuration(int newDuration) {
        this.duration = newDuration;
    }

    // MODIFIES: this
    // EFFECTS: sets the training session's notes to new, given notes
    public void setNotes(String newNotes) {
        this.notes = newNotes;
    }

    // MODIFIES: this
    // EFFECTS: sets the training session's skills to new, given skills
    public void setSkills(String newSkills) {
        this.skills = newSkills;
    }


    // EFFECTS: returns the training session's date
    public Date getDate() {
        return date; 
    }

    // EFFECTS: returns the training session's duration
    public int getDuration() {
        return duration; //stub
    }

    // EFFECTS: returns the training session's notes
    public String getNotes() {
        return notes;
    }

    // EFFECTS: returns the training session's skills
    public String getSkills() {
        return skills; 
    }

    // EFFECTS: returns this training session as a JSON object
    public JSONObject toJson() {
        return null;  // stub
    }

}
