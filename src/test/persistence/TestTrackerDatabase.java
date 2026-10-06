package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
