package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import model.Event;
import model.EventLog;
import model.TrainingSession;
import persistence.SessionStats;
import persistence.TrackerDatabase;

// Represents the "Sessions" tab: a table of every logged training session, a
// one-line summary of the totals underneath it, and the buttons for adding a
// session, removing the selected session, and opening the stats window.
//
// Sessions live in the database, not in memory. Adding or removing one writes
// to the database straight away and then reloads the table from it, so what is
// on screen is always what is actually stored, and there is nothing to save.
//
// The totals on the summary line are not counted up from the rows on screen.
// They come back from their own query, so the database does the arithmetic and
// the answer is about everything stored rather than everything displayed.
@ExcludeFromJacocoGeneratedReport
class SessionsPanel extends JPanel {

    private static final String[] COLUMNS = {"#", "Date", "Duration (min)", "Skills", "Notes"};

    private final TrackerDatabase database;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel summaryLabel;

    // the sessions currently shown in the table; row N of the table is element
    // N of this list, which is how a selected row is turned back into the id
    // its session was saved under
    private List<TrainingSession> sessions;

    // REQUIRES: database != null
    // EFFECTS: builds the sessions tab and fills it from the given database
    SessionsPanel(TrackerDatabase database) {
        super(new BorderLayout());
        this.database = database;
        this.sessions = new ArrayList<>();
        this.tableModel = createTableModel();
        this.table = UiTheme.createTable(tableModel);
        this.summaryLabel = UiTheme.createSummaryLabel();

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        add(UiTheme.createSectionLabel("Sessions"), BorderLayout.NORTH);
        add(createScrollPane(), BorderLayout.CENTER);
        add(createSouth(), BorderLayout.SOUTH);
        refresh();
    }

    // MODIFIES: this
    // EFFECTS: reads the sessions and the summary figures back out of the
    //          database, then rebuilds the table and the summary line. The
    //          figures come from their own query rather than being counted up
    //          from the rows, so the database does the arithmetic.
    void refresh() {
        try {
            sessions = database.getAllSessions();
            updateSummary(database.getSessionStats());
        } catch (SQLException e) {
            showDatabaseError("read your sessions from", e);
        }
        rebuildTable();
    }

    // MODIFIES: this
    // EFFECTS: replaces every table row with the sessions last read
    private void rebuildTable() {
        tableModel.setRowCount(0);
        int rowNumber = 1;
        for (TrainingSession s : sessions) {
            tableModel.addRow(new Object[]{
                    rowNumber++,
                    s.getDateAsString(),
                    s.getDuration(),
                    s.getSkills(),
                    s.getNotes()
            });
        }
    }

    // REQUIRES: stats != null
    // MODIFIES: this
    // EFFECTS: writes the session count and total time practised into the
    //          summary line below the table
    private void updateSummary(SessionStats stats) {
        if (stats.isEmpty()) {
            summaryLabel.setText("No sessions logged yet.");
            return;
        }
        summaryLabel.setText(String.format("%d session(s)   %d min total   %.1f hours practised",
                stats.getSessionCount(), stats.getTotalMinutes(), stats.getTotalHours()));
    }

    // EFFECTS: returns a table model with this tab's column headers, no rows,
    //          and no editable cells
    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    // EFFECTS: returns the sessions table wrapped in a bordered scroll pane
    private JScrollPane createScrollPane() {
        table.getColumnModel().getColumn(0).setPreferredWidth(28);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    // EFFECTS: returns the summary line and this tab's row of buttons, stacked
    private JPanel createSouth() {
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        buttons.add(new JButton(new AddSessionAction()));
        buttons.add(new JButton(new RemoveSessionAction()));
        buttons.add(new JButton(new ViewStatsAction()));

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(summaryLabel, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);
        return south;
    }

    // EFFECTS: shows the given message to the user as an error dialog
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    // EFFECTS: tells the user the database could not be used for the given
    //          action, and prints the underlying cause to the console
    private void showDatabaseError(String action, SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Could not " + action + " the database.\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Represents the action taken when the user wants to add a new training
     * session to the log.
     */
    private class AddSessionAction extends AbstractAction {

        AddSessionAction() {
            super("Add Session");
        }

        // MODIFIES: the database
        // EFFECTS: shows the new-session form; if the user clicks OK, saves the
        //          session they described and redraws the table
        @Override
        public void actionPerformed(ActionEvent evt) {
            JTextField[] fields = createFormFields();
            int result = JOptionPane.showConfirmDialog(SessionsPanel.this, buildFormPanel(fields),
                    "Add Training Session", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                processSessionInput(fields);
            }
        }

        // EFFECTS: returns the six empty text fields the form is made of
        private JTextField[] createFormFields() {
            JTextField[] fields = new JTextField[6];
            for (int i = 0; i < fields.length; i++) {
                fields[i] = new JTextField();
            }
            return fields;
        }

        // REQUIRES: fields.length >= 6
        // EFFECTS: returns a two-column form panel pairing each label with its field
        private JPanel buildFormPanel(JTextField[] fields) {
            String[] labels = {"Day (1-31):", "Month (1-12):", "Year:",
                    "Duration (min):", "Skills practiced:", "Notes (optional):"};
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            for (int i = 0; i < labels.length; i++) {
                form.add(new JLabel(labels[i]));
                form.add(fields[i]);
            }
            return form;
        }

        // REQUIRES: fields.length >= 6
        // MODIFIES: the database
        // EFFECTS: builds a session from what the user typed and saves it to the
        //          database; shows an error dialog instead if the numbers cannot
        //          be read, the date does not exist, the duration is not
        //          positive, or the database cannot be written to
        private void processSessionInput(JTextField[] fields) {
            try {
                TrainingSession saved = database.addSession(readSession(fields));
                EventLog.getInstance().logEvent(new Event(saved.getDuration()
                        + " min training session on " + saved.getDateAsString()
                        + " added to Training Log."));
                refresh();
            } catch (NumberFormatException e) {
                showError("Please enter whole numbers for Day, Month, Year, and Duration.");
            } catch (DateTimeException e) {
                showError("That date does not exist. Please check the day, month, and year.");
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            } catch (SQLException e) {
                showDatabaseError("save your session to", e);
            }
        }

        // REQUIRES: fields.length >= 6
        // EFFECTS: returns the training session described by the form fields;
        //          throws NumberFormatException if a number field is not a whole
        //          number, DateTimeException if that date does not exist, and
        //          IllegalArgumentException if the duration is not positive
        private TrainingSession readSession(JTextField[] fields) {
            int day = Integer.parseInt(fields[0].getText().trim());
            int month = Integer.parseInt(fields[1].getText().trim());
            int year = Integer.parseInt(fields[2].getText().trim());
            int duration = Integer.parseInt(fields[3].getText().trim());
            if (duration <= 0) {
                throw new IllegalArgumentException("Duration must be greater than 0 minutes.");
            }
            LocalDate date = LocalDate.of(year, month, day);
            return new TrainingSession(date, duration,
                    fields[4].getText().trim(), fields[5].getText().trim());
        }
    }

    /**
     * Represents the action taken when the user wants to remove the currently
     * selected training session from the log.
     */
    private class RemoveSessionAction extends AbstractAction {

        RemoveSessionAction() {
            super("Remove Selected");
        }

        // MODIFIES: the database
        // EFFECTS: asks the user to confirm, then deletes the selected session
        //          and redraws the table; warns them if no row is selected
        @Override
        public void actionPerformed(ActionEvent evt) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(SessionsPanel.this,
                        "Click a session in the table to select it first.",
                        "No Session Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(SessionsPanel.this,
                    "Delete session #" + (selectedRow + 1) + "? This cannot be undone.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteSelected(selectedRow);
            }
        }

        // REQUIRES: 0 <= row < the number of sessions on screen
        // MODIFIES: the database
        // EFFECTS: deletes the session shown in the given row, looked up by the
        //          id it was given when it was saved, and redraws the table
        private void deleteSelected(int row) {
            TrainingSession session = sessions.get(row);
            try {
                database.deleteSession(session.getId());
                EventLog.getInstance().logEvent(new Event(session.getDuration()
                        + " min training session on " + session.getDateAsString()
                        + " removed from Training Log."));
                refresh();
            } catch (SQLException e) {
                showDatabaseError("delete your session from", e);
            }
        }
    }

    /**
     * Represents the action taken when the user wants to see the bar chart of
     * their session durations in a separate window.
     */
    private class ViewStatsAction extends AbstractAction {

        ViewStatsAction() {
            super("View Stats");
        }

        // EFFECTS: opens the stats window, or tells the user there is nothing to
        //          chart yet if no sessions have been logged
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (sessions.isEmpty()) {
                JOptionPane.showMessageDialog(SessionsPanel.this,
                        "No sessions yet. Add some sessions first.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new StatsWindow(database);
        }
    }
}
