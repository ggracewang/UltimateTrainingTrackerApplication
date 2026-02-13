package model;

public class TrainingSession {

    // EFFECTS: constructs a training session with no date, duration, skills
    //          practiced, and notes
    public TrainingSession() {
        //TODO
    } 

    // REQUIRES: Date is ahead of current date
    // MODIFIES: this
    // EFFECTS: sets the training session's date to new, given date
    public void setDate(Date newDate) {
        // TODO
    }

    // REQUIRES: duration > 0
    // MODIFIES: this
     // EFFECTS: sets the training session's duration to new, given duration
    public void setDuration(int newDuration) {
        //TODO
    }

    // MODIFIES: this
    // EFFECTS: sets the training session's notes to new, given notes
    public void setNotes(String newNotes) {
        //TODO
    }

    // MODIFIES: this
    // EFFECTS: sets the training session's skills to new, given skills
    public void setSkills(String newSkills) {
        //TODO
    }


    // EFFECTS: returns the training session's date
    public Date getDate() {
        return null; //stub
    }

    // EFFECTS: returns the training session's duration
    public int getDuration() {
        return 0; //stub
    }

    // EFFECTS: returns the training session's notes
    public String getNotes() {
        return ""; //stub
    }

    // EFFECTS: returns the training session's skills
    public String getSkills() {
        return ""; //stub
    }


}
