package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoalLog {
    private List<Goal> goalLog;

    // EFFECTS: constructs new goal log with no goals added yet
    public GoalLog() {
        goalLog = new ArrayList<>();
    }

    // REQUIRES: goal != null
    // MODIFIES: this
    // EFFECTS: adds the goal to the goal log (list) if not already in
    public void addGoal(Goal goal) {
        if (!goalLog.contains(goal)) {
            goalLog.add(goal);
        }
        
    }

    // REQUIRES: goal != null
    // MODIFIES: this
    // EFFECTS: removes the goal from the goal log (list) if the goal is in goal log
    public void removeGoal(String goalTitle) {
        for (int i = 0; i < goalLog.size(); i++) {
            if (goalLog.get(i).getTitle().equals(goalTitle)) {
                goalLog.remove(i);
                break;
            }
        }
    }

    // EFFECTS: return all completed goals
    public List<Goal> getCompletedGoals() {
        List<Goal> completedGoals = new ArrayList<>();
        for (Goal g: goalLog) {
            if (g.getCompletionStatus() == true) {
                completedGoals.add(g);
            }
        }
        return completedGoals; 
    }

    // EFFECTS: return all goals in goal log
    public List<Goal> getAllGoals() {
        return goalLog; //stub
    }

    // EFFECTS: returns this goal log as a JSON object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("goals", goalsToJson());
        return json;
    }

    // EFFECTS: returns goals in this log as a JSON array
    private JSONArray goalsToJson() {
        JSONArray jsonArray = new JSONArray();
    
        for (Goal goal : goalLog) {
            jsonArray.put(goal.toJson());
        }
        
        return jsonArray;
    }
}
