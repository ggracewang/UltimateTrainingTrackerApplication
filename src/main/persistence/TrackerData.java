package persistence;

import model.GoalLog;
import model.TrainingLog;

// Holds the two logs that together make up one saved file.
//
// JsonReader returns one of these so that it only has to open and parse the
// save file once, and so that both logs are guaranteed to come from the same
// snapshot of the file.
public class TrackerData {

    private final TrainingLog trainingLog;
    private final GoalLog goalLog;

    // REQUIRES: trainingLog != null, goalLog != null
    // EFFECTS: constructs a bundle holding the given training log and goal log
    public TrackerData(TrainingLog trainingLog, GoalLog goalLog) {
        this.trainingLog = trainingLog;
        this.goalLog = goalLog;
    }

    // EFFECTS: returns the training log that was read from file
    public TrainingLog getTrainingLog() {
        return trainingLog;
    }

    // EFFECTS: returns the goal log that was read from file
    public GoalLog getGoalLog() {
        return goalLog;
    }
}
