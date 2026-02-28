package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestGoal {
    private Goal testGoal1;
    private Goal testGoal2;
    private Goal testGoal3;
    private Date date1;
    private Date date2;
    private Date date3;

    @BeforeEach
    void runBefore() {
        date1 = new Date(31, 12, 2026);
        date2 = new Date(1, 1, 2027);
        date3 = new Date(11, 23, 2028);

        testGoal1 = new Goal("Master huck", "Throw 50 meters consistently.", date1);
        testGoal2 = new Goal("Improve vertical", "Increase jump by 5cm.", date2);
        testGoal3 = new Goal("", "", date3); //boundary case, empty
    }

    
    @Test
    void testConstructor() {
        //standard case:
        assertEquals("Master huck", testGoal1.getTitle());
        assertEquals("Throw 50 meters consistently.", testGoal1.getDescription());
        assertEquals(date1, testGoal1.getCompletionDate());
        assertFalse(testGoal1.getCompletionStatus()); 

        //empty title and description boundary case:
        assertEquals("", testGoal3.getTitle());
        assertEquals("", testGoal3.getDescription());
        assertEquals(date3, testGoal3.getCompletionDate());
        assertFalse(testGoal3.getCompletionStatus());
    }


    @Test
    void testSetTitle() {
        //only title changes
        testGoal1.setTitle("Improve forehand");
        assertEquals("Improve forehand", testGoal1.getTitle());
        assertEquals("Throw 50 meters consistently.", testGoal1.getDescription());
        assertEquals(date1, testGoal1.getCompletionDate());
        assertFalse(testGoal1.getCompletionStatus()); 

        //title set to empty
        testGoal1.setTitle("");
        assertEquals("", testGoal1.getTitle());
        assertEquals("Throw 50 meters consistently.", testGoal1.getDescription());
        assertEquals(date1, testGoal1.getCompletionDate());
        assertFalse(testGoal1.getCompletionStatus());
    }

    @Test
    void testSetDescription() {
        //*only description changes
        testGoal2.setDescription("test description");
        assertEquals("Improve vertical", testGoal2.getTitle());
        assertEquals("test description", testGoal2.getDescription());
        assertEquals(date2, testGoal2.getCompletionDate());
        assertFalse(testGoal2.getCompletionStatus()); 

        //title description to empty
        testGoal2.setDescription("");
        assertEquals("Improve vertical", testGoal2.getTitle());
        assertEquals("", testGoal2.getDescription());
        assertEquals(date2, testGoal2.getCompletionDate());
        assertFalse(testGoal2.getCompletionStatus());
    }

    @Test
    void testSetCompletionDate() {
        //*only date changes
        testGoal2.setCompletionDate(date3);
        assertEquals("Improve vertical", testGoal2.getTitle());
        assertEquals("Increase jump by 5cm.", testGoal2.getDescription());
        assertEquals(date3, testGoal2.getCompletionDate());
        assertFalse(testGoal2.getCompletionStatus()); 

        Date newDate = new Date(13, 2, 2028);
        testGoal2.setCompletionDate(newDate);
        assertEquals("Improve vertical", testGoal2.getTitle());
        assertEquals("Increase jump by 5cm.", testGoal2.getDescription());
        assertEquals(newDate, testGoal2.getCompletionDate());
        assertFalse(testGoal2.getCompletionStatus()); 

    }


    @Test
    void testMarkCompleted() {
        //standard case:
        testGoal1.markCompleted();
        assertEquals("Master huck", testGoal1.getTitle());
        assertEquals("Throw 50 meters consistently.", testGoal1.getDescription());
        assertEquals(date1, testGoal1.getCompletionDate());
        assertTrue(testGoal1.getCompletionStatus()); 

        //empty title and description boundary case:
        testGoal2.markCompleted();
        assertEquals("", testGoal3.getTitle());
        assertEquals("", testGoal3.getDescription());
        assertEquals(date3, testGoal3.getCompletionDate());
        assertTrue(testGoal2.getCompletionStatus());
    }

    @Test
    void testToJson() {
        JSONObject json = testGoal1.toJson();
        
        assertEquals("Master huck", json.getString("title"));
        assertEquals("Throw 50 meters consistently.", json.getString("description"));
        assertFalse(json.getBoolean("completionStatus"));
        
        JSONObject dateJson = json.getJSONObject("date");
        assertEquals(31, dateJson.getInt("day"));
        assertEquals(12, dateJson.getInt("month"));
        assertEquals(2026, dateJson.getInt("year"));
    }

    @Test
    void testToJsonCompletedGoal() {
        testGoal1.markCompleted();
        JSONObject json = testGoal1.toJson();
        
        assertTrue(json.getBoolean("completionStatus"));
    }

    @Test
    void testToJsonEmptyFields() {
        JSONObject json = testGoal3.toJson();
        
        assertEquals("", json.getString("title"));
        assertEquals("", json.getString("description"));
        assertFalse(json.getBoolean("completionStatus"));
    }
}
