package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Goal;
import model.TrainingSession;

// Represents the SQLite database that the tracker's data is stored in, and the
// operations the app can perform on it.
//
// Opening one of these connects to the database file, creating the file itself
// if it is not there yet, and makes sure the training_sessions and goals tables
// exist. That means the app can be run on a computer that has never run it
// before and it will still work; there is no setup step and no schema file to
// remember to run.
//
// Every statement that carries a value typed by the user is sent as a
// PreparedStatement with ? placeholders, never by gluing strings together. The
// database then treats those values strictly as data, so a note containing an
// apostrophe is stored correctly instead of breaking the statement, and a note
// containing SQL cannot be run as SQL.
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

    // REQUIRES: session != null
    // MODIFIES: the database
    // EFFECTS: saves the given session as a new row and returns a copy of it
    //          carrying the id the database just handed out. The returned copy
    //          is the one to keep: the session passed in still has NO_ID,
    //          because a TrainingSession cannot be changed after it is built.
    //          Throws SQLException if the row cannot be written.
    public TrainingSession addSession(TrainingSession session) throws SQLException {
        String sql = "INSERT INTO training_sessions (date, duration, skills, notes) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, session.getDate().toString());
            stmt.setInt(2, session.getDuration());
            stmt.setString(3, session.getSkills());
            stmt.setString(4, session.getNotes());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                keys.next();
                return new TrainingSession(keys.getInt(1), session.getDate(),
                        session.getDuration(), session.getSkills(), session.getNotes());
            }
        }
    }

    // EFFECTS: returns every session in the database, oldest day first, with
    //          sessions logged on the same day in the order they were added;
    //          returns an empty list if there are none.
    //          Throws SQLException if the rows cannot be read.
    public List<TrainingSession> getAllSessions() throws SQLException {
        String sql = "SELECT id, date, duration, skills, notes FROM training_sessions ORDER BY date, id";
        List<TrainingSession> sessions = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sessions.add(new TrainingSession(
                        rs.getInt("id"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getInt("duration"),
                        rs.getString("skills"),
                        rs.getString("notes")));
            }
        }

        return sessions;
    }

    // MODIFIES: the database
    // EFFECTS: removes the session with the given id and returns true;
    //          returns false if no session has that id, so nothing was removed.
    //          Throws SQLException if the row cannot be deleted.
    public boolean deleteSession(int id) throws SQLException {
        String sql = "DELETE FROM training_sessions WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        }
    }

    // EFFECTS: returns the total number of minutes across every session in the
    //          database, or 0 if there are no sessions. The adding up is done by
    //          the database rather than by a loop in Java.
    //          Throws SQLException if the total cannot be read.
    public int getTotalMinutes() throws SQLException {
        // SUM returns NULL rather than 0 for an empty table, and getInt reads a
        // NULL back as 0, which is the answer we want anyway
        String sql = "SELECT SUM(duration) FROM training_sessions";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    // EFFECTS: returns how many sessions there are, how many minutes they come
    //          to, how long the average one is, and how long the longest one
    //          is. All four come back from a single query, because the database
    //          can work out several answers while looking at the rows once.
    //          Every number is 0 when there are no sessions at all.
    //          Throws SQLException if the figures cannot be read.
    public SessionStats getSessionStats() throws SQLException {
        // COUNT is 0 for an empty table, but SUM, AVG and MAX are all NULL.
        // getInt and getDouble read a NULL back as 0, which is the answer we
        // want in each case, so no special handling is needed.
        String sql = "SELECT COUNT(*), SUM(duration), AVG(duration), MAX(duration) "
                + "FROM training_sessions";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return new SessionStats(rs.getInt(1), rs.getInt(2), rs.getDouble(3), rs.getInt(4));
        }
    }

    // EFFECTS: returns how many minutes were practised in each month that has
    //          at least one session, oldest month first, keyed by the month as
    //          "YYYY-MM". Months with no training are simply absent.
    //          Throws SQLException if the figures cannot be read.
    public Map<String, Integer> getMinutesByMonth() throws SQLException {
        // strftime chops the first seven characters off the date, turning
        // "2026-05-04" into "2026-05". GROUP BY then gathers every row sharing
        // a month into one group, and SUM adds up each group separately, so one
        // row comes back per month instead of one per session.
        String sql = "SELECT strftime('%Y-%m', date) AS month, SUM(duration) AS minutes "
                + "FROM training_sessions GROUP BY month ORDER BY month";

        // LinkedHashMap keeps the months in the order the query returned them
        Map<String, Integer> byMonth = new LinkedHashMap<>();

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                byMonth.put(rs.getString("month"), rs.getInt("minutes"));
            }
        }

        return byMonth;
    }

    // REQUIRES: goal != null
    // MODIFIES: the database
    // EFFECTS: saves the given goal as a new row and returns a copy of it
    //          carrying the id the database handed out. Returns null instead if
    //          a goal with the same title is already stored, because the title
    //          column is declared UNIQUE and two goals may not share one.
    //          Throws SQLException if the row cannot be written.
    public Goal addGoal(Goal goal) throws SQLException {
        // OR IGNORE tells SQLite to skip the row rather than fail when it would
        // break the UNIQUE rule, which is the same thing GoalLog.add does in
        // memory. executeUpdate then reports 0 rows written instead of 1.
        String sql = "INSERT OR IGNORE INTO goals (title, description, target_date, completed) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, goal.getTitle());
            stmt.setString(2, goal.getDescription());
            stmt.setString(3, goal.getTargetDate().toString());
            stmt.setInt(4, goal.isCompleted() ? 1 : 0);

            if (stmt.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                keys.next();
                return rebuildGoal(keys.getInt(1), goal.getTitle(), goal.getDescription(),
                        goal.getTargetDate().toString(), goal.isCompleted());
            }
        }
    }

    // EFFECTS: returns every goal in the database, soonest target date first,
    //          with goals sharing a date in the order they were added;
    //          returns an empty list if there are none.
    //          Throws SQLException if the rows cannot be read.
    public List<Goal> getAllGoals() throws SQLException {
        return readGoals("SELECT id, title, description, target_date, completed FROM goals "
                + "ORDER BY target_date, id");
    }

    // EFFECTS: returns only the goals that have been completed, soonest target
    //          date first; returns an empty list if none are finished.
    //          The database does the filtering, so the unfinished goals are
    //          never read into memory at all.
    //          Throws SQLException if the rows cannot be read.
    public List<Goal> getCompletedGoals() throws SQLException {
        return readGoals("SELECT id, title, description, target_date, completed FROM goals "
                + "WHERE completed = 1 ORDER BY target_date, id");
    }

    // MODIFIES: the database
    // EFFECTS: marks the goal with the given id as completed and returns true;
    //          returns false if no goal has that id or it was already finished,
    //          so nothing changed. Throws SQLException if it cannot be updated.
    public boolean markGoalCompleted(int id) throws SQLException {
        // "AND completed = 0" means re-ticking an already finished goal reports
        // false rather than pretending something changed. The WHERE clause is
        // what keeps this to one row: without it every goal would be marked.
        String sql = "UPDATE goals SET completed = 1 WHERE id = ? AND completed = 0";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        }
    }

    // MODIFIES: the database
    // EFFECTS: removes the goal with the given id and returns true;
    //          returns false if no goal has that id, so nothing was removed.
    //          Throws SQLException if the row cannot be deleted.
    public boolean deleteGoal(int id) throws SQLException {
        String sql = "DELETE FROM goals WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        }
    }

    // REQUIRES: sql selects id, title, description, target_date and completed
    // EFFECTS: runs the given query and returns the goals it produced
    private List<Goal> readGoals(String sql) throws SQLException {
        List<Goal> goals = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                goals.add(rebuildGoal(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("target_date"),
                        rs.getInt("completed") == 1));
            }
        }

        return goals;
    }

    // EFFECTS: returns a Goal built from one row's worth of values. A goal is
    //          always constructed unfinished and then ticked off, because being
    //          completed is something that happens to a goal rather than
    //          something it is created with.
    private Goal rebuildGoal(int id, String title, String description,
            String targetDate, boolean completed) {
        Goal goal = new Goal(id, title, description, LocalDate.parse(targetDate));
        if (completed) {
            goal.markCompleted();
        }
        return goal;
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
