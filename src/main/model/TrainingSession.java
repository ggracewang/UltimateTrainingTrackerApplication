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
//
// A session also carries an id, which is the number the database uses to tell
// one saved row from another. A session that has not been saved to the database
// yet has the id NO_ID, because the database is what hands out real ids.
public class TrainingSession implements Writable {

    // the id of a session that is not in the database yet; real ids start at 1
    public static final int NO_ID = 0;

    private final int id;
    private final LocalDate date;
    private final int duration;
    private final String skills;
    private final String notes;

    // REQUIRES: date != null, duration >= 0, skills != null, notes != null
    // EFFECTS: constructs a new training session that is not in the database
    //          yet, on the given date, lasting the given number of minutes,
    //          practising the given skills, with the given notes
    public TrainingSession(LocalDate date, int duration, String skills, String notes) {
        this(NO_ID, date, duration, skills, notes);
    }

    // REQUIRES: id >= 0, date != null, duration >= 0, skills != null, notes != null
    // EFFECTS: constructs a training session that already has the given database
    //          id. Used when rebuilding a session that was read back out of the
    //          database, so that it still knows which row it came from.
    public TrainingSession(int id, LocalDate date, int duration, String skills, String notes) {
        this.id = id;
        this.date = date;
        this.duration = duration;
        this.skills = skills;
        this.notes = notes;
    }

    // EFFECTS: returns the database id of this session, or NO_ID if it has not
    //          been saved to the database yet
    public int getId() {
        return id;
    }

    // EFFECTS: returns true if this session has been saved to the database
    public boolean isSaved() {
        return id != NO_ID;
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
    //          as an ISO-8601 string such as "2026-12-31". The id is left out
    //          on purpose: it belongs to the database, not to the save file.
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
