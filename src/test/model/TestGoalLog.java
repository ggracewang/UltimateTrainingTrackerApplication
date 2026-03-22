package model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestGoalLog {
    private GoalLog testGoalLog;
    private Goal goal1;
    private Goal goal2;
    private Goal goal3;
    private Date date1;
    private Date date2;
    private Date date3;
    

    @BeforeEach
    void runBefore() {
        testGoalLog = new GoalLog();

        date1 = new Date(31, 12, 2026);
        date2 = new Date(1, 1, 2027);
        date3 = new Date(11, 23, 2028);

        goal1 = new Goal("Master huck", "Throw 50 meters consistently.", date1);
        goal2 = new Goal("Improve vertical", "Increase jump by 5cm.", date2);
        goal3 = new Goal("", "", date3); //boundary case, empty
    }

    @Test
    void testConstructor() {
        assertTrue(testGoalLog.getAllGoals().isEmpty());
        assertTrue(testGoalLog.getCompletedGoals().isEmpty());
    }

    @Test
    void testAddGoals() {
        //add 1 goal to goal log
        testGoalLog.addGoal(goal1);

        assertFalse(testGoalLog.getAllGoals().isEmpty());
        assertEquals(1, testGoalLog.getAllGoals().size());
        assertEquals(goal1, testGoalLog.getAllGoals().get(0));

        //add more goals to goal log
        testGoalLog.addGoal(goal2);
        testGoalLog.addGoal(goal3);

        assertEquals(3, testGoalLog.getAllGoals().size());
        assertEquals(goal1, testGoalLog.getAllGoals().get(0));
        assertEquals(goal2, testGoalLog.getAllGoals().get(1));
        assertEquals(goal3, testGoalLog.getAllGoals().get(2));

        testGoalLog.addGoal(goal1); //add goal that's already in goal log
        assertEquals(3, testGoalLog.getAllGoals().size());
        assertEquals(goal1, testGoalLog.getAllGoals().get(0));
        assertEquals(goal2, testGoalLog.getAllGoals().get(1));
        assertEquals(goal3, testGoalLog.getAllGoals().get(2));
    }

    @Test
    void testRemoveGoal() {
        testGoalLog.addGoal(goal1);
        testGoalLog.addGoal(goal2);
        testGoalLog.addGoal(goal3);

        testGoalLog.removeGoal("Improve vertical");

        assertEquals(2, testGoalLog.getAllGoals().size());
        assertEquals(goal1, testGoalLog.getAllGoals().get(0));
        assertEquals(goal3, testGoalLog.getAllGoals().get(1));

        testGoalLog.removeGoal("67"); // remove goal that isn't in goal log
        assertEquals(2, testGoalLog.getAllGoals().size());
        assertEquals(goal1, testGoalLog.getAllGoals().get(0));
        assertEquals(goal3, testGoalLog.getAllGoals().get(1));

        testGoalLog.removeGoal("Master huck");

        assertEquals(1, testGoalLog.getAllGoals().size());
        assertEquals(goal3, testGoalLog.getAllGoals().get(0));

    }

    @Test
    void testGetCompletedGoals() {
        testGoalLog.addGoal(goal1);
        testGoalLog.addGoal(goal2);
        testGoalLog.addGoal(goal3);

        goal2.markCompleted();

        assertEquals(1, testGoalLog.getCompletedGoals().size());
        assertEquals(goal2, testGoalLog.getCompletedGoals().get(0));

        goal1.markCompleted();
        assertEquals(2, testGoalLog.getCompletedGoals().size());
        assertEquals(goal1, testGoalLog.getCompletedGoals().get(0));
        assertEquals(goal2, testGoalLog.getCompletedGoals().get(1));

        goal3.markCompleted();
        assertEquals(3, testGoalLog.getCompletedGoals().size());
        assertEquals(goal1, testGoalLog.getCompletedGoals().get(0));
        assertEquals(goal2, testGoalLog.getCompletedGoals().get(1));
        assertEquals(goal3, testGoalLog.getCompletedGoals().get(2));


    }


    @Test
    void testToJsonEmptyLog() {
        JSONObject json = testGoalLog.toJson();
        JSONArray goals = json.getJSONArray("goals");
        assertEquals(0, goals.length());
    }

    @Test
    void testToJsonWithGoals() {
        testGoalLog.addGoal(goal1);
        testGoalLog.addGoal(goal2);
        
        JSONObject json = testGoalLog.toJson();
        JSONArray goals = json.getJSONArray("goals");
        
        assertEquals(2, goals.length());
        
        JSONObject firstGoal = goals.getJSONObject(0);
        assertEquals("Master huck", firstGoal.getString("title"));
        assertFalse(firstGoal.getBoolean("completionStatus"));
    }

    @Test
    void testToJsonWithCompletedGoals() {
        goal1.markCompleted();
        testGoalLog.addGoal(goal1);
        
        JSONObject json = testGoalLog.toJson();
        JSONArray goals = json.getJSONArray("goals");
        
        JSONObject firstGoal = goals.getJSONObject(0);
        assertTrue(firstGoal.getBoolean("completionStatus"));

    }
    
}
