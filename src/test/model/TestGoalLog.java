package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestGoalLog {
    private GoalLog testGoalLog;
    private Goal goal1;
    private Goal goal2;
    private Goal goal3;

    @BeforeEach
    void runBefore() {
        testGoalLog = new GoalLog();

        goal1 = new Goal("Master huck", "Throw 50 meters consistently.", LocalDate.of(2026, 12, 31));
        goal2 = new Goal("Improve vertical", "Increase jump by 5cm.", LocalDate.of(2027, 1, 1));
        goal3 = new Goal("", "", LocalDate.of(2028, 11, 23)); // boundary case: empty title
    }

    @Test
    void testConstructor() {
        assertTrue(testGoalLog.isEmpty());
        assertEquals(0, testGoalLog.size());
        assertTrue(testGoalLog.getAll().isEmpty());
        assertTrue(testGoalLog.getCompletedGoals().isEmpty());
    }

    @Test
    void testAddGoals() {
        testGoalLog.add(goal1);
        assertFalse(testGoalLog.isEmpty());
        assertEquals(1, testGoalLog.size());
        assertEquals(goal1, testGoalLog.getAll().get(0));

        testGoalLog.add(goal2);
        testGoalLog.add(goal3);
        assertEquals(3, testGoalLog.size());
        assertEquals(goal1, testGoalLog.getAll().get(0));
        assertEquals(goal2, testGoalLog.getAll().get(1));
        assertEquals(goal3, testGoalLog.getAll().get(2));
    }

    @Test
    void testAddGoalAlreadyInLog() {
        testGoalLog.add(goal1);
        testGoalLog.add(goal2);

        testGoalLog.add(goal1); // already in the log, so it is ignored

        assertEquals(2, testGoalLog.size());
        assertEquals(goal1, testGoalLog.getAll().get(0));
        assertEquals(goal2, testGoalLog.getAll().get(1));
    }

    @Test
    void testAddGoalRecordsEvent() {
        EventLog.getInstance().clear();
        testGoalLog.add(goal1);

        assertEquals("Goal \"Master huck\" added to Goal Log.", lastEventDescription());
    }

    @Test
    void testRemoveGoal() {
        testGoalLog.add(goal1);
        testGoalLog.add(goal2);

        assertTrue(testGoalLog.remove(goal1));

        assertEquals(1, testGoalLog.size());
        assertEquals(goal2, testGoalLog.getAll().get(0));
    }

    @Test
    void testRemoveGoalNotInLog() {
        testGoalLog.add(goal1);

        assertFalse(testGoalLog.remove(goal2));
        assertEquals(1, testGoalLog.size());
    }

    @Test
    void testRemoveByTitle() {
        testGoalLog.add(goal1);
        testGoalLog.add(goal2);
        testGoalLog.add(goal3);

        assertTrue(testGoalLog.removeByTitle("Improve vertical"));

        assertEquals(2, testGoalLog.size());
        assertEquals(goal1, testGoalLog.getAll().get(0));
        assertEquals(goal3, testGoalLog.getAll().get(1));

        assertTrue(testGoalLog.removeByTitle("Master huck"));
        assertEquals(1, testGoalLog.size());
        assertEquals(goal3, testGoalLog.getAll().get(0));

        // boundary case: a goal with an empty title can still be found by title
        assertTrue(testGoalLog.removeByTitle(""));
        assertTrue(testGoalLog.isEmpty());
    }

    @Test
    void testRemoveByTitleNotInLog() {
        testGoalLog.add(goal1);
        testGoalLog.add(goal2);

        assertFalse(testGoalLog.removeByTitle("No such goal"));

        assertEquals(2, testGoalLog.size());
        assertEquals(goal1, testGoalLog.getAll().get(0));
        assertEquals(goal2, testGoalLog.getAll().get(1));
    }

    @Test
    void testRemoveByTitleFromEmptyLog() {
        assertFalse(testGoalLog.removeByTitle("Master huck"));
        assertTrue(testGoalLog.isEmpty());
    }

    @Test
    void testMarkCompleted() {
        testGoalLog.add(goal1);
        testGoalLog.add(goal2);

        testGoalLog.markCompleted(goal1);

        assertTrue(goal1.isCompleted());
        assertFalse(goal2.isCompleted());
        assertEquals(1, testGoalLog.getCompletedGoals().size());
        assertEquals(goal1, testGoalLog.getCompletedGoals().get(0));
    }

    @Test
    void testMarkCompletedRecordsEvent() {
        testGoalLog.add(goal1);
        EventLog.getInstance().clear();
        testGoalLog.markCompleted(goal1);

        assertEquals("Goal \"Master huck\" marked as completed.", lastEventDescription());
    }

    @Test
    void testMarkCompletedGoalNotInLog() {
        testGoalLog.add(goal1);
        EventLog.getInstance().clear();

        testGoalLog.markCompleted(goal2); // goal2 was never added

        assertFalse(goal2.isCompleted());
        assertEquals("Event log cleared.", lastEventDescription());
    }

    @Test
    void testMarkCompletedGoalAlreadyCompleted() {
        testGoalLog.add(goal1);
        testGoalLog.markCompleted(goal1);
        EventLog.getInstance().clear();

        testGoalLog.markCompleted(goal1); // already completed, so nothing happens

        assertTrue(goal1.isCompleted());
        assertEquals(1, testGoalLog.getCompletedGoals().size());
        assertEquals("Event log cleared.", lastEventDescription());
    }

    @Test
    void testGetCompletedGoals() {
        testGoalLog.add(goal1);
        testGoalLog.add(goal2);
        testGoalLog.add(goal3);

        assertTrue(testGoalLog.getCompletedGoals().isEmpty());

        testGoalLog.markCompleted(goal2);
        assertEquals(1, testGoalLog.getCompletedGoals().size());
        assertEquals(goal2, testGoalLog.getCompletedGoals().get(0));

        testGoalLog.markCompleted(goal1);
        assertEquals(2, testGoalLog.getCompletedGoals().size());
        // completed goals come back in the order they were added, not completed
        assertEquals(goal1, testGoalLog.getCompletedGoals().get(0));
        assertEquals(goal2, testGoalLog.getCompletedGoals().get(1));

        testGoalLog.markCompleted(goal3);
        assertEquals(3, testGoalLog.getCompletedGoals().size());
    }

    @Test
    void testToJsonEmptyLog() {
        JSONObject json = testGoalLog.toJson();
        assertEquals(0, json.getJSONArray("goals").length());
    }

    @Test
    void testToJsonWithGoals() {
        testGoalLog.add(goal1);
        testGoalLog.add(goal2);

        JSONArray goals = testGoalLog.toJson().getJSONArray("goals");
        assertEquals(2, goals.length());

        JSONObject firstGoal = goals.getJSONObject(0);
        assertEquals("Master huck", firstGoal.getString("title"));
        assertEquals("2026-12-31", firstGoal.getString("targetDate"));
        assertFalse(firstGoal.getBoolean("completed"));
    }

    @Test
    void testToJsonWithCompletedGoals() {
        testGoalLog.add(goal1);
        testGoalLog.markCompleted(goal1);

        JSONArray goals = testGoalLog.toJson().getJSONArray("goals");
        assertTrue(goals.getJSONObject(0).getBoolean("completed"));
    }

    // EFFECTS: returns the description of the most recent event in the event log
    private String lastEventDescription() {
        String description = "";
        for (Event e : EventLog.getInstance()) {
            description = e.getDescription();
        }
        return description;
    }
}
