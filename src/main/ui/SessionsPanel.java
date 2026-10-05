package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.time.DateTimeException;
import java.time.LocalDate;

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
import model.TrainingLog;
import model.TrainingSession;

// Represents the "Sessions" tab: a table of every logged training session, a
// one-line summary of the totals underneath it, and the buttons for adding a
// session, removing the selected session, and opening the stats window.
@ExcludeFromJacocoGeneratedReport
class SessionsPanel extends JPanel {

    private static final String[] COLUMNS = {"#", "Date", "Duration (min)", "Skills", "Notes"};

    private TrainingLog trainingLog;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel summaryLabel;

    // REQUIRES: trainingLog != null
    // EFFECTS: builds the sessions tab showing the given training log
    SessionsPanel(TrainingLog trainingLog) {
        super(new BorderLayout());
        this.trainingLog = trainingLog;
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
    // EFFECTS: points this tab at the given training log and redraws the table;
    //          called after data has been loaded from file
    void setLog(TrainingLog log) {
        this.trainingLog = log;
        refresh();
    }

    // MODIFIES: this
    // EFFECTS: rebuilds every table row from the current training log, then
    //          updates the summary line
    void refresh() {
        tableModel.setRowCount(0);
        int rowNumber = 1;
        for (TrainingSession s : trainingLog.getAll()) {
            tableModel.addRow(new Object[]{
                    rowNumber++,
                    s.getDateAsString(),
                    s.getDuration(),
                    s.getSkills(),
                    s.getNotes()
            });
        }
        updateSummary();
    }

    // MODIFIES: this
    // EFFECTS: writes the session count and total time practised into the
    //          summary line below the table
    private void updateSummary() {
        if (trainingLog.isEmpty()) {
            summaryLabel.setText("No sessions logged yet.");
            return;
        }
        summaryLabel.setText(String.format("%d session(s)   %d min total   %.1f hours practised",
                trainingLog.size(), trainingLog.getTotalDurationPracticed(),
                trainingLog.getTotalHoursPracticed()));
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

    /**
     * Represents the action taken when the user wants to add a new training
     * session to the log.
     */
    private class AddSessionAction extends AbstractAction {

        AddSessionAction() {
            super("Add Session");
        }

        // MODIFIES: trainingLog
        // EFFECTS: shows the new-session form; if the user clicks OK, adds the
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
        // MODIFIES: trainingLog
        // EFFECTS: builds a session from what the user typed and adds it to the
        //          log; shows an error dialog instead if the numbers cannot be
        //          read, the date does not exist, or the duration is not positive
        private void processSessionInput(JTextField[] fields) {
            try {
                trainingLog.add(readSession(fields));
                refresh();
            } catch (NumberFormatException e) {
                showError("Please enter whole numbers for Day, Month, Year, and Duration.");
            } catch (DateTimeException e) {
                showError("That date does not exist. Please check the day, month, and year.");
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
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

        // MODIFIES: trainingLog
        // EFFECTS: asks the user to confirm, then removes the selected session
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
                trainingLog.remove(trainingLog.getAll().get(selectedRow));
                refresh();
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
            if (trainingLog.isEmpty()) {
                JOptionPane.showMessageDialog(SessionsPanel.this,
                        "No sessions yet. Add some sessions first.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new StatsWindow(trainingLog);
        }
    }
}
