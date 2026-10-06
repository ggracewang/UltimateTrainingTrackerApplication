package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestTrainingSession {
    private TrainingSession testSession1;
    private TrainingSession testSession2;
    private TrainingSession testSession3;
    private LocalDate date1;
    private LocalDate date2;
    private LocalDate date3;

    @BeforeEach
    void runBefore() {
        date1 = LocalDate.of(2026, 12, 31);  // boundary: last day of the year
        date2 = LocalDate.of(2027, 1, 1);    // boundary: first day of the year
        date3 = LocalDate.of(2024, 2, 29);   // boundary: leap day

        testSession1 = new TrainingSession(date1, 60, "Forehand, Backhand", "Morning practice");
        testSession2 = new TrainingSession(date2, 1, "", "");
        testSession3 = new TrainingSession(date3, 300, "Endurance", "Cardio day");
    }

    @Test
    void testConstructor() {
        assertEquals(date1, testSession1.getDate());
        assertEquals(60, testSession1.getDuration());
        assertEquals("Forehand, Backhand", testSession1.getSkills());
        assertEquals("Morning practice", testSession1.getNotes());
    }

    @Test
    void testConstructorEmptyFields() {
        assertEquals(date2, testSession2.getDate());
        assertEquals(1, testSession2.getDuration()); // boundary: shortest session
        assertEquals("", testSession2.getSkills());
        assertEquals("", testSession2.getNotes());
    }

    @Test
    void testConstructorLeapDay() {
        assertEquals(date3, testSession3.getDate());
        assertEquals(2024, testSession3.getDate().getYear());
        assertEquals(2, testSession3.getDate().getMonthValue());
        assertEquals(29, testSession3.getDate().getDayOfMonth());
        assertEquals(300, testSession3.getDuration());
    }

    @Test
    void testGetDateAsString() {
        assertEquals("12/31/2026", testSession1.getDateAsString());
        assertEquals("1/1/2027", testSession2.getDateAsString());
        assertEquals("2/29/2024", testSession3.getDateAsString());
    }

    @Test
    void testToJson() {
        JSONObject json = testSession1.toJson();

        assertEquals("2026-12-31", json.getString("date"));
        assertEquals(60, json.getInt("duration"));
        assertEquals("Forehand, Backhand", json.getString("skills"));
        assertEquals("Morning practice", json.getString("notes"));
    }

    @Test
    void testToJsonEmptyFields() {
        JSONObject json = testSession2.toJson();

        assertEquals("2027-01-01", json.getString("date"));
        assertEquals(1, json.getInt("duration"));
        assertEquals("", json.getString("skills"));
        assertEquals("", json.getString("notes"));
    }

    @Test
    void testNewSessionHasNoId() {
        assertEquals(TrainingSession.NO_ID, testSession1.getId());
        assertFalse(testSession1.isSaved());
    }

    @Test
    void testSessionBuiltWithId() {
        TrainingSession fromDatabase = new TrainingSession(7, date1, 60, "Forehand", "note");

        assertEquals(7, fromDatabase.getId());
        assertTrue(fromDatabase.isSaved());
        assertEquals(date1, fromDatabase.getDate());
        assertEquals(60, fromDatabase.getDuration());
        assertEquals("Forehand", fromDatabase.getSkills());
        assertEquals("note", fromDatabase.getNotes());
    }

    @Test
    void testIdIsNotWrittenToJson() {
        JSONObject json = new TrainingSession(7, date1, 60, "Forehand", "note").toJson();

        assertFalse(json.has("id"));
        assertEquals("2026-12-31", json.getString("date"));
    }
}
