package model;

// Represents the player's training log: every training session they have
// recorded, in the order they recorded them.
//
// Adding, removing, counting, and saving to JSON all come from Log. This class
// only supplies the training-specific wording for event messages and the sums
// and averages the stats window reports.
public class TrainingLog extends Log<TrainingSession> {

    private static final int MINUTES_PER_HOUR = 60;

    // EFFECTS: returns the total number of minutes practised across all sessions
    public int getTotalDurationPracticed() {
        int totalDuration = 0;
        for (TrainingSession s : items) {
            totalDuration += s.getDuration();
        }
        return totalDuration;
    }

    // EFFECTS: returns the total number of hours practised across all sessions
    public double getTotalHoursPracticed() {
        return (double) getTotalDurationPracticed() / MINUTES_PER_HOUR;
    }

    // EFFECTS: returns the average length in minutes of a session in this log,
    //          or 0 if no sessions have been logged yet
    public double getAverageSessionDuration() {
        if (isEmpty()) {
            return 0;
        }
        return (double) getTotalDurationPracticed() / size();
    }

    // EFFECTS: returns the length in minutes of the longest session in this log,
    //          or 0 if no sessions have been logged yet
    public int getLongestSessionDuration() {
        int longest = 0;
        for (TrainingSession s : items) {
            if (s.getDuration() > longest) {
                longest = s.getDuration();
            }
        }
        return longest;
    }

    // EFFECTS: returns the name training sessions are stored under in JSON
    @Override
    protected String getJsonKey() {
        return "sessions";
    }

    // EFFECTS: returns this log's name as it appears in event messages
    @Override
    protected String getLogName() {
        return "Training Log";
    }

    // REQUIRES: session != null
    // EFFECTS: returns a description of the given session for event messages,
    //          e.g. "80 min training session on 3/2/2026"
    @Override
    protected String describe(TrainingSession session) {
        return session.getDuration() + " min training session on " + session.getDateAsString();
    }
}
