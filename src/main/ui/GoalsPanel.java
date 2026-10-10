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
import javax.swing.JCheckBox;
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
import model.Goal;
import persistence.TrackerDatabase;

// Represents the "Goals" tab: a table of the goals the player has set, a
// checkbox for narrowing the table down to the goals they have finished, and
// the buttons for adding a goal, marking one completed, and removing one.
//
// Goals live in the database. Adding, completing, or removing one writes to the
// database straight away and then reloads the table from it, so what is on
// screen is always what is actually stored and there is nothing to save.
//
// Nothing here ever calls goal.markCompleted() directly. That would tick a goal
// off on screen without the database hearing about it, and the two would drift
// apart. Completing a goal always goes through the database by its id.
@ExcludeFromJacocoGeneratedReport
class GoalsPanel extends JPanel {

    private static final String[] COLUMNS = {"#", "Goal", "Description", "Target Date", "Status"};

    private final TrackerDatabase database;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JCheckBox completedOnlyBox;
    private final JLabel summaryLabel;

    // the goals currently shown in the table, which is either every goal or
    // only the completed ones depending on the checkbox; row N of the table is
    // element N of this list
    private List<Goal> visibleGoals;

    // every goal in the database, used for the counts on the summary line
    private List<Goal> allGoals;

    // REQUIRES: database != null
    // EFFECTS: builds the goals tab and fills it from the given database
    GoalsPanel(TrackerDatabase database) {
        super(new BorderLayout());
        this.database = database;
        this.visibleGoals = new ArrayList<>();
        this.allGoals = new ArrayList<>();
        this.tableModel = createTableModel();
        this.table = UiTheme.createTable(tableModel);
        this.completedOnlyBox = createCompletedOnlyBox();
        this.summaryLabel = UiTheme.createSummaryLabel();

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        add(createNorth(), BorderLayout.NORTH);
        add(createScrollPane(), BorderLayout.CENTER);
        add(createSouth(), BorderLayout.SOUTH);
        refresh();
    }

    // MODIFIES: this
    // EFFECTS: reads the goals back out of the database and rebuilds the table
    //          and summary line from them. When the checkbox is ticked the
    //          database itself does the filtering, so the unfinished goals are
    //          never read into memory at all.
    void refresh() {
        try {
            allGoals = database.getAllGoals();
            visibleGoals = completedOnlyBox.isSelected()
                    ? database.getCompletedGoals()
                    : allGoals;
        } catch (SQLException e) {
            showDatabaseError("read your goals from", e);
        }
        rebuildTable();
        updateSummary();
    }

    // MODIFIES: this
    // EFFECTS: replaces every table row with the goals currently on show
    private void rebuildTable() {
        tableModel.setRowCount(0);
        int rowNumber = 1;
        for (Goal g : visibleGoals) {
            tableModel.addRow(new Object[]{
                    rowNumber++,
                    g.getTitle(),
                    g.getDescription(),
                    g.getTargetDateAsString(),
                    g.getStatus()
            });
        }
    }

    // MODIFIES: this
    // EFFECTS: writes how many goals are completed and how many are overdue into
    //          the summary line below the table
    private void updateSummary() {
        if (allGoals.isEmpty()) {
            summaryLabel.setText("No goals set yet.");
            return;
        }
        summaryLabel.setText(String.format("%d of %d goal(s) completed   %d overdue",
                countCompleted(), allGoals.size(), countOverdue()));
    }

    // EFFECTS: returns how many goals have been completed
    private int countCompleted() {
        int completed = 0;
        for (Goal g : allGoals) {
            if (g.isCompleted()) {
                completed++;
            }
        }
        return completed;
    }

    // EFFECTS: returns how many goals are unfinished and past their target date
    private int countOverdue() {
        int overdue = 0;
        for (Goal g : allGoals) {
            if (g.isOverdue()) {
                overdue++;
            }
        }
        return overdue;
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

    // EFFECTS: returns the checkbox that filters the table down to completed
    //          goals, which redraws the table whenever it is ticked or unticked
    private JCheckBox createCompletedOnlyBox() {
        JCheckBox box = new JCheckBox("Show completed only");
        box.setFont(UiTheme.SMALL_FONT);
        box.setForeground(UiTheme.LABEL_FG);
        box.setOpaque(false);
        box.addActionListener(e -> refresh());
        return box;
    }

    // EFFECTS: returns this tab's heading row: the section label on the left and
    //          the completed-only checkbox on the right
    private JPanel createNorth() {
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(UiTheme.createSectionLabel("Goals"), BorderLayout.WEST);
        north.add(completedOnlyBox, BorderLayout.EAST);
        return north;
    }

    // EFFECTS: returns the goals table wrapped in a bordered scroll pane
    private JScrollPane createScrollPane() {
        table.getColumnModel().getColumn(0).setPreferredWidth(28);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
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
        buttons.add(new JButton(new AddGoalAction()));
        buttons.add(new JButton(new MarkCompletedAction()));
        buttons.add(new JButton(new RemoveGoalAction()));

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(summaryLabel, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);
        return south;
    }

    // EFFECTS: returns the goal on the selected table row, or null if no row is
    //          selected; shows a warning dialog in that case
    private Goal getSelectedGoal() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Click a goal in the table to select it first.",
                    "No Goal Selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return visibleGoals.get(selectedRow);
    }

    // EFFECTS: shows the given message to the user as an error dialog
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    // EFFECTS: tells the user the database could not be used for the given
    //          action, and shows the underlying cause
    private void showDatabaseError(String action, SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Could not " + action + " the database.\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Represents the action taken when the user wants to add a new goal to
     * their goal log.
     */
    private class AddGoalAction extends AbstractAction {

        AddGoalAction() {
            super("Add Goal");
        }

        // MODIFIES: the database
        // EFFECTS: shows the new-goal form; if the user clicks OK, saves the goal
        //          they described and redraws the table
        @Override
        public void actionPerformed(ActionEvent evt) {
            JTextField[] fields = createFormFields();
            int result = JOptionPane.showConfirmDialog(GoalsPanel.this, buildFormPanel(fields),
                    "Add Goal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                processGoalInput(fields);
            }
        }

        // EFFECTS: returns the five empty text fields the form is made of
        private JTextField[] createFormFields() {
            JTextField[] fields = new JTextField[5];
            for (int i = 0; i < fields.length; i++) {
                fields[i] = new JTextField();
            }
            return fields;
        }

        // REQUIRES: fields.length >= 5
        // EFFECTS: returns a two-column form panel pairing each label with its field
        private JPanel buildFormPanel(JTextField[] fields) {
            String[] labels = {"Goal title:", "Description:",
                    "Target day (1-31):", "Target month (1-12):", "Target year:"};
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            for (int i = 0; i < labels.length; i++) {
                form.add(new JLabel(labels[i]));
                form.add(fields[i]);
            }
            return form;
        }

        // REQUIRES: fields.length >= 5
        // MODIFIES: the database
        // EFFECTS: builds a goal from what the user typed and saves it; shows an
        //          error dialog instead if the numbers cannot be read, the date
        //          does not exist, the title is blank, a goal with that title is
        //          already stored, or the database cannot be written to
        private void processGoalInput(JTextField[] fields) {
            try {
                saveGoal(readGoal(fields));
            } catch (NumberFormatException e) {
                showError("Please enter whole numbers for the target day, month, and year.");
            } catch (DateTimeException e) {
                showError("That date does not exist. Please check the day, month, and year.");
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            } catch (SQLException e) {
                showDatabaseError("save your goal to", e);
            }
        }

        // REQUIRES: goal != null
        // MODIFIES: the database
        // EFFECTS: saves the given goal and redraws the table; tells the user
        //          instead if they already have a goal with that title, which is
        //          what addGoal returning null means
        private void saveGoal(Goal goal) throws SQLException {
            Goal saved = database.addGoal(goal);
            if (saved == null) {
                showError("You already have a goal called \"" + goal.getTitle() + "\".");
                return;
            }
            EventLog.getInstance().logEvent(new Event(
                    "Goal \"" + saved.getTitle() + "\" added to Goal Log."));
            refresh();
        }

        // REQUIRES: fields.length >= 5
        // EFFECTS: returns the goal described by the form fields; throws
        //          NumberFormatException if a date field is not a whole number,
        //          DateTimeException if that date does not exist, and
        //          IllegalArgumentException if the title is blank
        private Goal readGoal(JTextField[] fields) {
            String title = fields[0].getText().trim();
            if (title.isEmpty()) {
                throw new IllegalArgumentException("Please give the goal a title.");
            }
            int day = Integer.parseInt(fields[2].getText().trim());
            int month = Integer.parseInt(fields[3].getText().trim());
            int year = Integer.parseInt(fields[4].getText().trim());
            return new Goal(title, fields[1].getText().trim(), LocalDate.of(year, month, day));
        }
    }

    /**
     * Represents the action taken when the user wants to mark the currently
     * selected goal as completed.
     */
    private class MarkCompletedAction extends AbstractAction {

        MarkCompletedAction() {
            super("Mark Completed");
        }

        // MODIFIES: the database
        // EFFECTS: marks the selected goal as completed in the database and
        //          redraws the table; tells the user if it was already completed
        @Override
        public void actionPerformed(ActionEvent evt) {
            Goal selected = getSelectedGoal();
            if (selected == null) {
                return;
            }
            if (selected.isCompleted()) {
                JOptionPane.showMessageDialog(GoalsPanel.this,
                        "That goal is already marked as completed.",
                        "Already Completed", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            complete(selected);
        }

        // REQUIRES: goal != null and is stored in the database
        // MODIFIES: the database
        // EFFECTS: ticks the given goal off by its id and redraws the table. The
        //          goal object on screen is never changed directly, so the table
        //          can only ever show what the database actually holds.
        private void complete(Goal goal) {
            try {
                database.markGoalCompleted(goal.getId());
                EventLog.getInstance().logEvent(new Event(
                        "Goal \"" + goal.getTitle() + "\" marked as completed."));
                refresh();
            } catch (SQLException e) {
                showDatabaseError("update your goal in", e);
            }
        }
    }

    /**
     * Represents the action taken when the user wants to remove the currently
     * selected goal from their goal log.
     */
    private class RemoveGoalAction extends AbstractAction {

        RemoveGoalAction() {
            super("Remove Selected");
        }

        // MODIFIES: the database
        // EFFECTS: asks the user to confirm, then deletes the selected goal and
        //          redraws the table
        @Override
        public void actionPerformed(ActionEvent evt) {
            Goal selected = getSelectedGoal();
            if (selected == null) {
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(GoalsPanel.this,
                    "Delete the goal \"" + selected.getTitle() + "\"? This cannot be undone.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                delete(selected);
            }
        }

        // REQUIRES: goal != null and is stored in the database
        // MODIFIES: the database
        // EFFECTS: deletes the given goal by its id and redraws the table
        private void delete(Goal goal) {
            try {
                database.deleteGoal(goal.getId());
                EventLog.getInstance().logEvent(new Event(
                        "Goal \"" + goal.getTitle() + "\" removed from Goal Log."));
                refresh();
            } catch (SQLException e) {
                showDatabaseError("delete your goal from", e);
            }
        }
    }
}
