package sqlplay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Random;

import persistence.TrackerDatabase;

// Fills data/tracker.db with a few months of made-up training sessions and
// goals, so that there is enough data in there for queries like AVG and
// GROUP BY to be interesting.
//
// The tables themselves are created by TrackerDatabase, so there is only one
// description of the schema in the project and this sandbox can never drift
// out of step with the real application.
//
// Running this again empties both tables and refills them, so it is always safe
// to re-run after you have been experimenting.
public class SeedDatabase {

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

    // EFFECTS: fills data/tracker.db with sample data, replacing anything
    //          already in its two tables
    public static void main(String[] args) throws SQLException {
        try (TrackerDatabase db = new TrackerDatabase()) {
            Connection conn = db.getConnection();
            emptyTables(conn);
            insertSessions(conn);
            insertGoals(conn);
            System.out.println("Filled data/tracker.db");
            System.out.println("Now put a query in data/scratch.sql and run sqlplay.SqlConsole.");
        }
    }

    // MODIFIES: the database
    // EFFECTS: removes every row from both tables, and resets the counters that
    //          hand out ids so that the first row is id 1 again
    private static void emptyTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM training_sessions");
            stmt.executeUpdate("DELETE FROM goals");
            stmt.executeUpdate("DELETE FROM sqlite_sequence "
                    + "WHERE name IN ('training_sessions', 'goals')");
        }
    }

    // MODIFIES: the database
    // EFFECTS: inserts about forty training sessions spread over the last five
    //          months. The random generator is given a fixed seed so that the
    //          same data is produced every time this is run.
    private static void insertSessions(Connection conn) throws SQLException {
        Random random = new Random(210);
        LocalDate day = LocalDate.of(2026, 5, 4);
        LocalDate end = LocalDate.of(2026, 10, 1);

        String sql = "INSERT INTO training_sessions (date, duration, skills, notes) VALUES (?, ?, ?, ?)";
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

        System.out.println("Inserted " + count + " training sessions");
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

    // MODIFIES: the database
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
