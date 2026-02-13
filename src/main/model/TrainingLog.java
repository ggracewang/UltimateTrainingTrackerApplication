package model;

import java.util.ArrayList;
import java.util.List;

public class TrainingLog {


    // EFFECTS: constructs new training log with no training sessions, and zero total duration
    public TrainingLog() {
        //TODO
    }

    // REQUIRES: training session != null
    // MODIFIES: this
    // EFFECTS: adds the given training session to the trainingLog list
    public void addSession(TrainingSession session) {
        //TODO
    }

    // REQUIRES: training session != null
    // MODIFIES: this
    // EFFECTS: removes the given training session from the trainingLog list if there is one
    public void removeSession(TrainingSession session) {
        //TODO
    }

    // EFFECTS: returns list of of training sessions logged
    public List<TrainingSession> getTrainingLog() {
        return null; //stub
    }

    // EFFECTS: returns the total duration practiced of all sessions in training log
    public int getTotalDurationPracticed() {
        return 0; //stub
    }

    

}
