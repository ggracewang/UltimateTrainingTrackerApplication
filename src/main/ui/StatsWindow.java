package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import persistence.SessionStats;
import persistence.TrackerDatabase;

// Referenced from AlarmSystem demo

/**
 * Represents the stats window: a bar chart of how many minutes were practised
 * in each month, with a line of totals above it.
 * Opened from the Sessions tab when the user clicks "View Stats".
 * Every number shown here is worked out by the database rather than by looping
 * in Java, so none of the sessions are loaded to produce it.
 * Uses DISPOSE_ON_CLOSE so closing this window does not exit the application.
 */
@ExcludeFromJacocoGeneratedReport
class StatsWindow extends JFrame {

    private static final int WIDTH = 560;
    private static final int HEIGHT = 430;

    // REQUIRES: database != null
    // EFFECTS: builds and shows the stats window for the given database
    StatsWindow(TrackerDatabase database) {
        super("Training Stats");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        SessionStats stats = readStats(database);
        Map<String, Integer> byMonth = readMinutesByMonth(database);

        add(createNorth(stats), BorderLayout.NORTH);
        add(createChartPanel(byMonth), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        centreOnScreen();
        setVisible(true);
    }

    // EFFECTS: returns the session statistics from the database, or a set of
    //          zeroes if they cannot be read
    private SessionStats readStats(TrackerDatabase database) {
        try {
            return database.getSessionStats();
        } catch (SQLException e) {
            showDatabaseError(e);
            return new SessionStats(0, 0, 0, 0);
        }
    }

    // EFFECTS: returns the minutes practised per month from the database, or an
    //          empty result if they cannot be read
    private Map<String, Integer> readMinutesByMonth(TrackerDatabase database) {
        try {
            return database.getMinutesByMonth();
        } catch (SQLException e) {
            showDatabaseError(e);
            return new LinkedHashMap<>();
        }
    }

    // EFFECTS: tells the user the figures could not be read from the database
    private void showDatabaseError(SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Could not read your training figures from the database.\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    // REQUIRES: stats != null
    // EFFECTS: returns the dark header bar with the totals line stacked below it
    private JPanel createNorth(SessionStats stats) {
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(UiTheme.createHeader("Minutes per Month",
                "How much training each month added up to"));
        north.add(createTotalsBar(stats));
        return north;
    }

    // REQUIRES: stats != null
    // EFFECTS: returns a strip showing the session count, total hours, average
    //          session length, and longest session
    private JPanel createTotalsBar(SessionStats stats) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UiTheme.PANEL_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 22, 10, 22)));

        JLabel totals = new JLabel(String.format(
                "%d sessions      %.1f hours total      %.0f min average      %d min longest",
                stats.getSessionCount(), stats.getTotalHours(),
                stats.getAverageMinutes(), stats.getLongestMinutes()));
        totals.setFont(UiTheme.SMALL_FONT);
        totals.setForeground(UiTheme.LABEL_FG);

        bar.add(totals, BorderLayout.WEST);
        return bar;
    }

    // REQUIRES: byMonth != null
    // EFFECTS: returns a white panel holding the bar chart of monthly minutes
    private JPanel createChartPanel(Map<String, Integer> byMonth) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 22));

        BarChartPanel chart = new BarChartPanel();
        chart.setTitles("Minutes practised", "Month", "No sessions yet");
        chart.setData(monthLabels(byMonth), new ArrayList<>(byMonth.values()));

        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    // REQUIRES: byMonth is keyed by month in "YYYY-MM" form
    // EFFECTS: returns the months turned into short labels for under the bars,
    //          e.g. "2026-05" becomes "May"
    private List<String> monthLabels(Map<String, Integer> byMonth) {
        List<String> labels = new ArrayList<>();
        for (String key : byMonth.keySet()) {
            Month month = YearMonth.parse(key).getMonth();
            String name = month.toString(); // e.g. "MAY"
            labels.add(name.charAt(0) + name.substring(1, 3).toLowerCase());
        }
        return labels;
    }

    // EFFECTS: returns the footer panel holding the close button
    private JPanel createFooter() {
        JPanel panel = UiTheme.createButtonBar();
        panel.add(new JButton(new CloseAction()));
        return panel;
    }

    // MODIFIES: this
    // EFFECTS: positions this window at the centre of the screen
    private void centreOnScreen() {
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((screenWidth - getWidth()) / 2, (screenHeight - getHeight()) / 2);
    }

    /**
     * Represents the action taken when the user wants to close the stats window
     * and return to the main window.
     */
    private class CloseAction extends AbstractAction {

        CloseAction() {
            super("Close");
        }

        // MODIFIES: this
        // EFFECTS: disposes of this window; the main window stays open
        @Override
        public void actionPerformed(ActionEvent evt) {
            dispose();
        }
    }
}
