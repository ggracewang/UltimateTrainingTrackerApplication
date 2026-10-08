package sqlplay;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// A scratchpad for learning SQL by running it.
//
// Write SQL into data/scratch.sql, then run this class. Every statement in the
// file is run against data/tracker.db in order, and anything that returns rows
// is printed as a table. A statement that fails prints its error and the rest
// of the file still runs, so one typo does not stop everything.
//
// This is a learning tool, not part of the app. Delete it whenever you like.
public class SqlConsole {

    // the database queried when no other one is named on the command line;
    // this is the real application database, so that you can look at your own
    // sessions and goals. Pass ./data/sandbox.db to query the practice data
    // that SeedDatabase makes instead.
    private static final String DEFAULT_DB = "./data/tracker.db";
    private static final Path SCRIPT = Paths.get("data", "scratch.sql");
    private static final int MAX_ROWS_SHOWN = 40;

    // EFFECTS: runs every statement in data/scratch.sql against the database
    //          named by the first argument, or the application database if no
    //          argument is given, and prints the results
    public static void main(String[] args) throws IOException {
        String dbPath = (args.length > 0) ? args[0] : DEFAULT_DB;
        System.out.println("database: " + dbPath);

        String script = new String(Files.readAllBytes(SCRIPT), StandardCharsets.UTF_8);
        List<String> statements = splitIntoStatements(script);

        if (statements.isEmpty()) {
            System.out.println("No SQL found in " + SCRIPT + ". Write a query in there and run again.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            for (String sql : statements) {
                runOne(conn, sql);
            }
        } catch (SQLException e) {
            System.out.println("Could not open the database: " + e.getMessage());
        }
    }

    // EFFECTS: runs one SQL statement and prints either its rows or how many
    //          rows it changed; prints the error if the statement is not valid
    private static void runOne(Connection conn, String sql) {
        System.out.println();
        System.out.println("> " + sql.replace("\n", "\n  "));

        try (Statement stmt = conn.createStatement()) {
            if (stmt.execute(sql)) {
                try (ResultSet rs = stmt.getResultSet()) {
                    printTable(rs);
                }
            } else {
                System.out.println("  OK (" + stmt.getUpdateCount() + " rows changed)");
            }
        } catch (SQLException e) {
            System.out.println("  SQL ERROR: " + e.getMessage());
        }
    }

    // EFFECTS: prints the rows of the given result set as an aligned table
    private static void printTable(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();

        List<String[]> rows = new ArrayList<>();
        String[] headers = new String[columns];
        for (int i = 0; i < columns; i++) {
            headers[i] = meta.getColumnLabel(i + 1);
        }
        rows.add(headers);

        int total = 0;
        while (rs.next()) {
            total++;
            if (rows.size() <= MAX_ROWS_SHOWN) {
                String[] row = new String[columns];
                for (int i = 0; i < columns; i++) {
                    String value = rs.getString(i + 1);
                    row[i] = (value == null) ? "NULL" : value;
                }
                rows.add(row);
            }
        }

        if (total == 0) {
            System.out.println("  (no rows)");
            return;
        }

        printAligned(rows, columns);
        System.out.println("  " + total + " row(s)"
                + (total > MAX_ROWS_SHOWN ? ", showing first " + MAX_ROWS_SHOWN : ""));
    }

    // EFFECTS: prints the given rows with every column padded to a common width,
    //          with a dashed line under the header row
    private static void printAligned(List<String[]> rows, int columns) {
        int[] widths = new int[columns];
        for (String[] row : rows) {
            for (int i = 0; i < columns; i++) {
                widths[i] = Math.max(widths[i], row[i].length());
            }
        }

        for (int r = 0; r < rows.size(); r++) {
            StringBuilder line = new StringBuilder("  ");
            for (int i = 0; i < columns; i++) {
                // the last column is not padded, so there is no trailing space
                if (i < columns - 1) {
                    line.append(pad(rows.get(r)[i], widths[i])).append(" | ");
                } else {
                    line.append(rows.get(r)[i]);
                }
            }
            System.out.println(line.toString());

            if (r == 0) {
                StringBuilder rule = new StringBuilder("  ");
                for (int i = 0; i < columns; i++) {
                    for (int d = 0; d < widths[i]; d++) {
                        rule.append('-');
                    }
                    if (i < columns - 1) {
                        rule.append("-+-");
                    }
                }
                System.out.println(rule.toString());
            }
        }
    }

    // EFFECTS: returns the given text padded with spaces out to the given width
    private static String pad(String text, int width) {
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }

    // EFFECTS: splits a SQL script into its individual statements, dropping
    //          "--" comments and blank statements. Semicolons inside quoted
    //          text are not treated as separators, so a note containing one
    //          does not split a statement in half.
    private static List<String> splitIntoStatements(String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (String rawLine : script.split("\n")) {
            String line = inQuotes ? rawLine : stripComment(rawLine);

            for (char c = 0, i = 0; i < line.length(); i++) {
                c = line.charAt(i);
                if (c == '\'') {
                    inQuotes = !inQuotes;
                }
                if (c == ';' && !inQuotes) {
                    addIfNotBlank(statements, current);
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
            current.append('\n');
        }

        addIfNotBlank(statements, current);
        return statements;
    }

    // EFFECTS: returns the given line with any trailing "--" comment removed
    private static String stripComment(String line) {
        int marker = line.indexOf("--");
        return (marker >= 0) ? line.substring(0, marker) : line;
    }

    // MODIFIES: statements
    // EFFECTS: adds the trimmed contents of the buffer to the list, unless the
    //          buffer holds only whitespace
    private static void addIfNotBlank(List<String> statements, StringBuilder buffer) {
        String sql = buffer.toString().trim();
        if (!sql.isEmpty()) {
            statements.add(sql);
        }
    }
}
