package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestGoal {
    private Goal testGoal1;
    private Goal testGoal2;
    private Goal testGoal3;
    private Goal overdueGoal;
    private LocalDate date1;
    private LocalDate date2;
    private LocalDate date3;

    @BeforeEach
    void runBefore() {
        date1 = LocalDate.of(2026, 12, 31);
        date2 = LocalDate.of(2027, 1, 1);
        date3 = LocalDate.of(2028, 11, 23);

        testGoal1 = new Goal("Master huck", "Throw 50 meters consistently.", date1);
        testGoal2 = new Goal("Improve vertical", "Increase jump by 5cm.", date2);
        testGoal3 = new Goal("", "", date3); // boundary case: empty title and description
        overdueGoal = new Goal("Old goal", "Should have finished this.", LocalDate.now().minusDays(1));
    }

    @Test
    void testConstructor() {
        assertEquals("Master huck", testGoal1.getTitle());
        assertEquals("Throw 50 meters consistently.", testGoal1.getDescription());
        assertEquals(date1, testGoal1.getTargetDate());
        assertFalse(testGoal1.isCompleted());
    }

    @Test
    void testConstructorEmptyFields() {
        assertEquals("", testGoal3.getTitle());
        assertEquals("", testGoal3.getDescription());
        assertEquals(date3, testGoal3.getTargetDate());
        assertFalse(testGoal3.isCompleted());
    }

    @Test
    void testMarkCompleted() {
        testGoal1.markCompleted();
        assertTrue(testGoal1.isCompleted());

        // everything else about the goal is unchanged
        assertEquals("Master huck", testGoal1.getTitle());
        assertEquals("Throw 50 meters consistently.", testGoal1.getDescription());
        assertEquals(date1, testGoal1.getTargetDate());

        // marking an already completed goal leaves it completed
        testGoal1.markCompleted();
        assertTrue(testGoal1.isCompleted());

        // marking one goal does not affect another
        assertFalse(testGoal2.isCompleted());
    }

    @Test
    void testGetTargetDateAsString() {
        assertEquals("12/31/2026", testGoal1.getTargetDateAsString());
        assertEquals("1/1/2027", testGoal2.getTargetDateAsString());
        assertEquals("11/23/2028", testGoal3.getTargetDateAsString());
    }

    @Test
    void testIsOverdueWhenTargetDateHasPassed() {
        assertTrue(overdueGoal.isOverdue());
    }

    @Test
    void testIsOverdueWhenTargetDateIsInTheFuture() {
        Goal futureGoal = new Goal("Future goal", "Lots of time left.", LocalDate.now().plusDays(1));
        assertFalse(futureGoal.isOverdue());
    }

    @Test
    void testIsOverdueWhenTargetDateIsToday() {
        // boundary case: a goal due today is not overdue yet
        Goal dueToday = new Goal("Due today", "Still have today.", LocalDate.now());
        assertFalse(dueToday.isOverdue());
    }

    @Test
    void testIsOverdueWhenCompleted() {
        // a finished goal is never overdue, even if its target date has passed
        overdueGoal.markCompleted();
        assertFalse(overdueGoal.isOverdue());
    }

    @Test
    void testGetStatus() {
        assertEquals("In progress", testGoal1.getStatus());
        assertEquals("Overdue", overdueGoal.getStatus());

        testGoal1.markCompleted();
        assertEquals("Completed", testGoal1.getStatus());

        overdueGoal.markCompleted();
        assertEquals("Completed", overdueGoal.getStatus());
    }

    @Test
    void testToJson() {
        JSONObject json = testGoal1.toJson();

        assertEquals("Master huck", json.getString("title"));
        assertEquals("Throw 50 meters consistently.", json.getString("description"));
        assertEquals("2026-12-31", json.getString("targetDate"));
        assertFalse(json.getBoolean("completed"));
    }

    @Test
    void testToJsonCompletedGoal() {
        testGoal1.markCompleted();
        assertTrue(testGoal1.toJson().getBoolean("completed"));
    }

    @Test
    void testToJsonEmptyFields() {
        JSONObject json = testGoal3.toJson();

        assertEquals("", json.getString("title"));
        assertEquals("", json.getString("description"));
        assertEquals("2028-11-23", json.getString("targetDate"));
        assertFalse(json.getBoolean("completed"));
    }
}
