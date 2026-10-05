package sqlplay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Random;

// Builds data/tracker.db from scratch: creates the two tables and fills them
// with a few months of made-up training sessions and goals, so that there is
// enough data in there for queries like AVG and GROUP BY to be interesting.
//
// Running this again wipes the database and rebuilds it, so it is always safe
// to re-run after you have been experimenting.
public class SeedDatabase {

    private static final String DB_URL = "jdbc:sqlite:./data/tracker.db";

    // the pool of skills sessions are drawn from, so that grouping by skill
    // has several sessions in each group
    private static final String[] SKILLS = {
        "Forehand", "Backhand", "Hammer", "Defense", "Cutting", "Handling", "Conditioning", "Hucks"
    };

    private static final String[] NOTES = {
        "Felt good, release point is getting more consistent.",
        "Windy - everything drifted. Need to throw lower.",
        "Short session, tired from yesterday.",
        "Best throwing day in a while.",
        "Worked with Sam on it's a breeze drill.",
        "Legs were heavy but finished the set.",
        ""
    };

    // EFFECTS: creates data/tracker.db with the schema and sample data,
    //          replacing any database already there
    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            createSchema(conn);
            insertSessions(conn);
            insertGoals(conn);
            System.out.println("Built data/tracker.db");
            System.out.println("Now put a query in data/scratch.sql and run sqlplay.SqlConsole.");
        }
    }

    // MODIFIES: the database at DB_URL
    // EFFECTS: drops any existing tables and creates the sessions and goals tables
    private static void createSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS sessions");
            stmt.executeUpdate("DROP TABLE IF EXISTS goals");

            stmt.executeUpdate(
                    "CREATE TABLE sessions ("
                    + "  id       INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "  date     TEXT    NOT NULL,"
                    + "  duration INTEGER NOT NULL,"
                    + "  skills   TEXT    NOT NULL,"
                    + "  notes    TEXT    NOT NULL)");

            stmt.executeUpdate(
                    "CREATE TABLE goals ("
                    + "  id          INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "  title       TEXT    NOT NULL UNIQUE,"
                    + "  description TEXT    NOT NULL,"
                    + "  target_date TEXT    NOT NULL,"
                    + "  completed   INTEGER NOT NULL DEFAULT 0)");
        }
    }

    // MODIFIES: the database at DB_URL
    // EFFECTS: inserts about forty training sessions spread over the last five
    //          months. The random generator is given a fixed seed so that the
    //          same data is produced every time this is run.
    private static void insertSessions(Connection conn) throws SQLException {
        Random random = new Random(210);
        LocalDate day = LocalDate.of(2026, 5, 4);
        LocalDate end = LocalDate.of(2026, 10, 1);

        String sql = "INSERT INTO sessions (date, duration, skills, notes) VALUES (?, ?, ?, ?)";
        int count = 0;

        conn.setAutoCommit(false);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            while (day.isBefore(end)) {
                stmt.setString(1, day.toString());
                stmt.setInt(2, 20 + random.nextInt(19) * 5);
                stmt.setString(3, pickSkills(random));
                stmt.setString(4, NOTES[random.nextInt(NOTES.length)]);
                stmt.addBatch();
                count++;
                day = day.plusDays(2 + random.nextInt(4));
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }

        System.out.println("Inserted " + count + " sessions");
    }

    // EFFECTS: returns one or two skill names joined by ", ", the same
    //          comma-separated format the app already stores skills in
    private static String pickSkills(Random random) {
        String first = SKILLS[random.nextInt(SKILLS.length)];
        if (random.nextInt(3) == 0) {
            return first;
        }

        String second = SKILLS[random.nextInt(SKILLS.length)];
        return first.equals(second) ? first : first + ", " + second;
    }

    // MODIFIES: the database at DB_URL
    // EFFECTS: inserts eight goals: some finished, some still to come, and some
    //          whose target date has already passed without being finished
    private static void insertGoals(Connection conn) throws SQLException {
        String[][] goals = {
            {"Master the huck", "Throw a flat 50m forehand on demand", "2026-12-31", "0"},
            {"Hammer in a game", "Complete a hammer in league play", "2026-08-15", "1"},
            {"Run a sub-25 5k", "General conditioning for tournaments", "2026-09-30", "0"},
            {"Left-hand backhand", "Usable 15m backhand with off hand", "2026-11-30", "0"},
            {"Catch 100 in a row", "Both hands, no drops", "2026-07-01", "1"},
            {"Layout D in practice", "Commit to one layout block", "2026-09-01", "0"},
            {"Learn the scoober", "Short-range scoober over a defender", "2027-02-28", "0"},
            {"Pull in bounds 9/10", "Consistent pulls under pressure", "2026-06-30", "1"}
        };

        String sql = "INSERT INTO goals (title, description, target_date, completed) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String[] goal : goals) {
                stmt.setString(1, goal[0]);
                stmt.setString(2, goal[1]);
                stmt.setString(3, goal[2]);
                stmt.setInt(4, Integer.parseInt(goal[3]));
                stmt.executeUpdate();
            }
        }

        System.out.println("Inserted " + goals.length + " goals");
    }
}
