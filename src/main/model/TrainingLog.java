package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TrainingLog {
    private List<TrainingSession> trainingLog;

    // EFFECTS: constructs new training log with no training sessions
    public TrainingLog() {
        trainingLog = new ArrayList<>();
    }

    // REQUIRES: training session != null
    // MODIFIES: this
    // EFFECTS: adds the given training session to the trainingLog list
    public void addSession(TrainingSession session) {
        trainingLog.add(session);
    }

    // REQUIRES: training session != null
    // MODIFIES: this
    // EFFECTS: removes the given training session from the trainingLog list if there is one
    public void removeSession(TrainingSession session) {
        for (int i = 0; i < trainingLog.size(); i++) {
            if (trainingLog.get(i) == session) {
                trainingLog.remove(i);
            }
        }
    }

    // EFFECTS: returns list of of training sessions logged
    public List<TrainingSession> getTrainingLog() {
        return trainingLog; 
    }

    // EFFECTS: returns the total duration practiced of all sessions in training log
    public int getTotalDurationPracticed() {
        int totalDuration = 0;
        for (TrainingSession s: trainingLog) {
            totalDuration += s.getDuration();
        }
        return totalDuration;
    }

    // EFFECTS: returns this training log as a JSON object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("sessions", sessionsToJson());
        return json;
    }

    // EFFECTS: returns training sessions in this log as a JSON array
    private JSONArray sessionsToJson() {
        JSONArray jsonArray = new JSONArray();
    
        for (TrainingSession session : trainingLog) {
            jsonArray.put(session.toJson());
        }
        
        return jsonArray;

    }

}
