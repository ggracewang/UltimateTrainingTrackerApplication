package model;

import java.time.LocalDate;

import org.json.JSONObject;

import persistence.Writable;

// Represents one training session that has already happened: the day of the
// session, how many minutes it lasted, the skills practised, and the player's
// notes.
//
// A session is a record of the past, so nothing about it can change after it
// is created. All of its fields are final and it has no setters.
public class TrainingSession implements Writable {

    private final LocalDate date;
    private final int duration;
    private final String skills;
    private final String notes;

    // REQUIRES: date != null, duration >= 0, skills != null, notes != null
    // EFFECTS: constructs a training session on the given date lasting the given
    //          number of minutes, practising the given skills, with the given notes
    public TrainingSession(LocalDate date, int duration, String skills, String notes) {
        this.date = date;
        this.duration = duration;
        this.skills = skills;
        this.notes = notes;
    }

    // EFFECTS: returns the date this session took place on
    public LocalDate getDate() {
        return date;
    }

    // EFFECTS: returns this session's date formatted for display, e.g. 12/31/2026
    public String getDateAsString() {
        return DateUtil.format(date);
    }

    // EFFECTS: returns how many minutes this session lasted
    public int getDuration() {
        return duration;
    }

    // EFFECTS: returns the skills practised in this session
    public String getSkills() {
        return skills;
    }

    // EFFECTS: returns the notes written about this session
    public String getNotes() {
        return notes;
    }

    // EFFECTS: returns this training session as a JSON object, storing the date
    //          as an ISO-8601 string such as "2026-12-31"
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("date", date.toString());
        json.put("duration", duration);
        json.put("skills", skills);
        json.put("notes", notes);
        return json;
    }
}
