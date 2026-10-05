package model;

import java.util.ArrayList;
import java.util.List;

// Represents the player's goal tracker: every goal they have set, in the order
// they set them.
//
// Adding, removing, counting, and saving to JSON all come from Log. This class
// only supplies the goal-specific wording plus the goal-specific operations:
// refusing duplicates, marking a goal done, and filtering to completed goals.
public class GoalLog extends Log<Goal> {

    // REQUIRES: goal != null
    // MODIFIES: this
    // EFFECTS: adds the given goal to this log unless it is already in it
    @Override
    public void add(Goal goal) {
        if (!items.contains(goal)) {
            super.add(goal);
        }
    }

    // MODIFIES: this
    // EFFECTS: removes the goal with the given title from this log and returns
    //          true; returns false if no goal in this log has that title.
    //          The matching goal is found first and removed afterwards, so the
    //          list is never modified while it is still being looped over.
    public boolean removeByTitle(String title) {
        Goal match = null;
        for (Goal goal : items) {
            if (goal.getTitle().equals(title)) {
                match = goal;
                break;
            }
        }
        return match != null && remove(match);
    }

    // REQUIRES: goal != null
    // MODIFIES: this, goal
    // EFFECTS: marks the given goal as completed and records an event saying so,
    //          if the goal is in this log and not already completed
    public void markCompleted(Goal goal) {
        if (!items.contains(goal) || goal.isCompleted()) {
            return;
        }
        goal.markCompleted();
        logEvent("Goal \"" + goal.getTitle() + "\" marked as completed.");
    }

    // EFFECTS: returns every goal in this log that has been completed
    public List<Goal> getCompletedGoals() {
        List<Goal> completedGoals = new ArrayList<>();
        for (Goal g : items) {
            if (g.isCompleted()) {
                completedGoals.add(g);
            }
        }
        return completedGoals;
    }

    // EFFECTS: returns the name goals are stored under in JSON
    @Override
    protected String getJsonKey() {
        return "goals";
    }

    // EFFECTS: returns this log's name as it appears in event messages
    @Override
    protected String getLogName() {
        return "Goal Log";
    }

    // REQUIRES: goal != null
    // EFFECTS: returns a description of the given goal for event messages,
    //          e.g. Goal "Master huck"
    @Override
    protected String describe(Goal goal) {
        return "Goal \"" + goal.getTitle() + "\"";
    }
}
