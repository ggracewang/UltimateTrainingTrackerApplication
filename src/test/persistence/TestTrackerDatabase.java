package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.TrainingSession;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestTrackerDatabase {

    // a real file on disk, used only by the test that has to close the database
    // and open it again; an in-memory database disappears when it is closed
    private static final String TEST_FILE = "./data/testTrackerDatabase.db";

    private TrackerDatabase db;

    @BeforeEach
    void runBefore() throws SQLException {
        new File(TEST_FILE).delete();
        db = new TrackerDatabase(TrackerDatabase.IN_MEMORY);
    }

    @AfterEach
    void runAfter() throws SQLException {
        if (db != null && db.isOpen()) {
            db.close();
        }
        new File(TEST_FILE).delete();
    }

    @Test
    void testConstructorOpensConnection() throws SQLException {
        assertTrue(db.isOpen());
        assertFalse(db.getConnection().isClosed());
    }

    @Test
    void testBothTablesAreCreated() throws SQLException {
        List<String> tables = tableNames(db);

        assertTrue(tables.contains("training_sessions"));
        assertTrue(tables.contains("goals"));
    }

    @Test
    void testTrainingSessionsHasExpectedColumns() throws SQLException {
        assertEquals(List.of("id", "date", "duration", "skills", "notes"),
                columnNames(db, "training_sessions"));
    }

    @Test
    void testGoalsHasExpectedColumns() throws SQLException {
        assertEquals(List.of("id", "title", "description", "target_date", "completed"),
                columnNames(db, "goals"));
    }

    @Test
    void testClose() throws SQLException {
        db.close();

        assertFalse(db.isOpen());
        assertTrue(db.getConnection().isClosed());
    }

    @Test
    void testReopeningKeepsExistingData() throws SQLException {
        // first run: build the database and put a row in it
        TrackerDatabase first = new TrackerDatabase(TEST_FILE);
        try (Statement stmt = first.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO training_sessions (date, duration, skills, notes) "
                    + "VALUES ('2026-10-05', 45, 'Forehand', 'first run')");
        }
        first.close();

        // second run: the tables already exist, so CREATE TABLE IF NOT EXISTS
        // must leave them, and the row, alone
        TrackerDatabase second = new TrackerDatabase(TEST_FILE);
        try (Statement stmt = second.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT date, duration FROM training_sessions")) {
            assertTrue(rs.next());
            assertEquals("2026-10-05", rs.getString("date"));
            assertEquals(45, rs.getInt("duration"));
            assertFalse(rs.next());
        }
        second.close();
    }

    @Test
    void testIdsAreHandedOutAutomatically() throws SQLException {
        try (Statement stmt = db.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO goals (title, description, target_date) "
                    + "VALUES ('First', 'a', '2026-12-31')");
            stmt.executeUpdate("INSERT INTO goals (title, description, target_date) "
                    + "VALUES ('Second', 'b', '2026-12-31')");
        }

        try (Statement stmt = db.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, completed FROM goals ORDER BY id")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("id"));     // ids start at 1, not 0
            assertEquals(0, rs.getInt("completed")); // DEFAULT 0 means not completed
            assertTrue(rs.next());
            assertEquals(2, rs.getInt("id"));
        }
    }

    @Test
    void testOpeningInAMissingFolderThrows() {
        assertThrows(SQLException.class,
                () -> new TrackerDatabase("./no-such-folder/tracker.db"));
    }

    @Test
    void testAddSessionReturnsCopyCarryingTheNewId() throws SQLException {
        TrainingSession before = new TrainingSession(LocalDate.of(2026, 5, 4), 90, "Hucks", "windy");
        assertEquals(TrainingSession.NO_ID, before.getId());

        TrainingSession after = db.addSession(before);

        assertEquals(1, after.getId());          // first row in an empty table
        assertTrue(after.isSaved());
        assertEquals(LocalDate.of(2026, 5, 4), after.getDate());
        assertEquals(90, after.getDuration());
        assertEquals("Hucks", after.getSkills());
        assertEquals("windy", after.getNotes());

        // the original is untouched, because a session cannot be changed
        assertEquals(TrainingSession.NO_ID, before.getId());
    }

    @Test
    void testAddSessionGivesEachRowItsOwnId() throws SQLException {
        TrainingSession first = db.addSession(new TrainingSession(LocalDate.of(2026, 5, 4), 60, "", ""));
        TrainingSession second = db.addSession(new TrainingSession(LocalDate.of(2026, 5, 6), 30, "", ""));

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void testGetAllSessionsOnEmptyDatabase() throws SQLException {
        assertTrue(db.getAllSessions().isEmpty());
    }

    @Test
    void testGetAllSessionsRoundTripsEveryField() throws SQLException {
        db.addSession(new TrainingSession(LocalDate.of(2026, 5, 4), 90, "Hucks, Defense", "felt good"));

        List<TrainingSession> all = db.getAllSessions();

        assertEquals(1, all.size());
        assertEquals(LocalDate.of(2026, 5, 4), all.get(0).getDate());
        assertEquals(90, all.get(0).getDuration());
        assertEquals("Hucks, Defense", all.get(0).getSkills());
        assertEquals("felt good", all.get(0).getNotes());
        assertTrue(all.get(0).isSaved());
    }

    @Test
    void testGetAllSessionsIsOrderedOldestFirst() throws SQLException {
        db.addSession(new TrainingSession(LocalDate.of(2026, 7, 1), 10, "", ""));
        db.addSession(new TrainingSession(LocalDate.of(2026, 5, 4), 20, "", ""));
        db.addSession(new TrainingSession(LocalDate.of(2026, 6, 2), 30, "", ""));

        List<TrainingSession> all = db.getAllSessions();

        assertEquals(LocalDate.of(2026, 5, 4), all.get(0).getDate());
        assertEquals(LocalDate.of(2026, 6, 2), all.get(1).getDate());
        assertEquals(LocalDate.of(2026, 7, 1), all.get(2).getDate());
    }

    @Test
    void testNotesContainingAnApostropheAreStoredCorrectly() throws SQLException {
        // this is the value that would break a statement built by joining
        // strings together, and is why every value goes through a placeholder
        String awkward = "Sam's drill; DROP TABLE training_sessions;--";
        db.addSession(new TrainingSession(LocalDate.of(2026, 5, 4), 60, "Handling", awkward));

        List<TrainingSession> all = db.getAllSessions();

        assertEquals(1, all.size());
        assertEquals(awkward, all.get(0).getNotes());
        assertTrue(tableNames(db).contains("training_sessions")); // table still there
    }

    @Test
    void testDeleteSession() throws SQLException {
        TrainingSession saved = db.addSession(new TrainingSession(LocalDate.of(2026, 5, 4), 60, "", ""));

        assertTrue(db.deleteSession(saved.getId()));
        assertTrue(db.getAllSessions().isEmpty());
    }

    @Test
    void testDeleteSessionThatIsNotThere() throws SQLException {
        db.addSession(new TrainingSession(LocalDate.of(2026, 5, 4), 60, "", ""));

        assertFalse(db.deleteSession(999));
        assertEquals(1, db.getAllSessions().size()); // nothing else was removed
    }

    @Test
    void testGetTotalMinutesOnEmptyDatabase() throws SQLException {
        assertEquals(0, db.getTotalMinutes());
    }

    @Test
    void testGetTotalMinutes() throws SQLException {
        db.addSession(new TrainingSession(LocalDate.of(2026, 5, 4), 90, "", ""));
        db.addSession(new TrainingSession(LocalDate.of(2026, 5, 6), 45, "", ""));
        db.addSession(new TrainingSession(LocalDate.of(2026, 5, 8), 20, "", ""));

        assertEquals(155, db.getTotalMinutes());
    }

    @Test
    void testGetTotalMinutesFollowsDeletes() throws SQLException {
        TrainingSession saved = db.addSession(new TrainingSession(LocalDate.of(2026, 5, 4), 90, "", ""));
        db.addSession(new TrainingSession(LocalDate.of(2026, 5, 6), 45, "", ""));

        db.deleteSession(saved.getId());

        assertEquals(45, db.getTotalMinutes());
    }

    // EFFECTS: returns the names of every table in the given database
    private List<String> tableNames(TrackerDatabase database) throws SQLException {
        List<String> names = new ArrayList<>();
        try (Statement stmt = database.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        }
        return names;
    }

    // EFFECTS: returns the column names of the given table, in order
    private List<String> columnNames(TrackerDatabase database, String table) throws SQLException {
        List<String> names = new ArrayList<>();
        try (Statement stmt = database.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        }
        return names;
    }
}
