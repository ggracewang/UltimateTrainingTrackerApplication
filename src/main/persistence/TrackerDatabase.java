package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Represents the connection to the SQLite database file that the tracker's
// data is stored in.
//
// Opening one of these does two things: it connects to the database file,
// creating the file itself if it is not there yet, and it makes sure the
// training_sessions and goals tables exist. That means the app can be run on a
// computer that has never run it before and it will still work; there is no
// setup step and no schema file to remember to run.
//
// This class owns the connection and nothing else. Reading and writing rows is
// the job of the classes that will be built on top of it.
public class TrackerDatabase implements AutoCloseable {

    // the database file the application uses
    public static final String DEFAULT_PATH = "./data/tracker.db";

    // the path meaning "do not use a file at all, keep the database in memory";
    // used by tests, which get a fresh empty database with nothing to clean up
    public static final String IN_MEMORY = ":memory:";

    private final Connection connection;

    // EFFECTS: opens the application's database at DEFAULT_PATH, creating the
    //          file and the tables if they are not there yet;
    //          throws SQLException if the database cannot be opened
    public TrackerDatabase() throws SQLException {
        this(DEFAULT_PATH);
    }

    // REQUIRES: path != null
    // MODIFIES: the database file at the given path
    // EFFECTS: opens the database at the given path, creating the file and the
    //          tables if they are not there yet;
    //          throws SQLException if the database cannot be opened
    public TrackerDatabase(String path) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        createTables();
    }

    // EFFECTS: returns the open connection to this database, for other classes
    //          to run their statements on
    public Connection getConnection() {
        return connection;
    }

    // EFFECTS: returns true if this database is still open
    public boolean isOpen() throws SQLException {
        return !connection.isClosed();
    }

    // MODIFIES: this
    // EFFECTS: closes the connection to the database;
    //          throws SQLException if it cannot be closed
    @Override
    public void close() throws SQLException {
        connection.close();
    }

    // MODIFIES: the database
    // EFFECTS: creates the training_sessions and goals tables, unless they are
    //          already there. "IF NOT EXISTS" is what makes this safe to run on
    //          every startup: the first run builds the tables, and every run
    //          after that leaves the existing data alone.
    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS training_sessions ("
                    + "  id       INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "  date     TEXT    NOT NULL,"
                    + "  duration INTEGER NOT NULL,"
                    + "  skills   TEXT    NOT NULL,"
                    + "  notes    TEXT    NOT NULL)");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS goals ("
                    + "  id          INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "  title       TEXT    NOT NULL UNIQUE,"
                    + "  description TEXT    NOT NULL,"
                    + "  target_date TEXT    NOT NULL,"
                    + "  completed   INTEGER NOT NULL DEFAULT 0)");
        }
    }
}
