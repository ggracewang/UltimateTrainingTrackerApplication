package ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import model.Event;
import model.EventLog;
import model.GoalLog;
import model.TrainingLog;
import persistence.JsonReader;
import persistence.JsonWriter;
import persistence.TrackerData;
import persistence.TrackerDatabase;

// Referenced from AlarmSystem demo

// Represents the main application window. It hosts the Sessions and Goals tabs
// plus the Save / Load / Exit bar along the bottom.
//
// The two tabs are stored in different places at the moment, part way through
// moving the app from files to a database:
//   - sessions live in the database, and are written the moment they change,
//     so there is nothing for the user to save
//   - goals still live in the JSON file, so Save and Load still apply to them
// Once goals move across too, Save and Load disappear altogether.
//
// Each tab builds and manages its own table and buttons (see SessionsPanel and
// GoalsPanel), so this class only deals with the window itself, with the
// database connection, and with saving and loading goals.
@ExcludeFromJacocoGeneratedReport
public class TrainingTrackerGUI extends JFrame {

    private static final int WIDTH = 880;
    private static final int HEIGHT = 580;
    private static final String JSON_STORE = "./data/trainingtracker.json";

    private GoalLog goalLog;
    private final TrackerDatabase database;
    private final JsonReader jsonReader;
    private final JsonWriter jsonWriter;
    private final SessionsPanel sessionsPanel;
    private final GoalsPanel goalsPanel;

    // EFFECTS: opens the database, builds and shows the window, then asks the
    //          user whether they want to load their saved goals. Quits with an
    //          explanation if the database cannot be opened, because without it
    //          there is nowhere for sessions to live.
    public TrainingTrackerGUI() {
        super("Ultimate Training Tracker");

        database = openDatabaseOrQuit();
        goalLog = new GoalLog();
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
        sessionsPanel = new SessionsPanel(database);
        goalsPanel = new GoalsPanel(goalLog);

        buildWindow();
        promptLoadOnStart();
    }

    // EFFECTS: returns a connection to the application database, or shows an
    //          error and quits if it cannot be opened
    private TrackerDatabase openDatabaseOrQuit() {
        try {
            return new TrackerDatabase();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not open the training database.\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return null; // unreachable; exit() does not return
        }
    }

    // MODIFIES: this
    // EFFECTS: lays out the window, centres it on screen, shows it, and makes
    //          the window's X button go through the same exit prompt as the
    //          Exit button rather than closing immediately
    private void buildWindow() {
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        add(UiTheme.createHeader("Training Tracker", "Ultimate Training Tracker"), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        centreOnScreen();
        setVisible(true);
    }

    // EFFECTS: returns the tabbed pane holding the Sessions and Goals tabs
    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UiTheme.SECTION_FONT);
        tabs.addTab("Sessions", sessionsPanel);
        tabs.addTab("Goals", goalsPanel);
        return tabs;
    }

    // EFFECTS: returns the Save / Load / Exit bar shown along the bottom of the
    //          window, which applies to both tabs
    private JPanel createBottomBar() {
        JPanel panel = UiTheme.createButtonBar();
        panel.add(new JButton(new SaveAction()));
        panel.add(new JButton(new LoadAction()));
        panel.add(Box.createHorizontalStrut(24));
        panel.add(new JButton(new ExitAction()));
        return panel;
    }

    // MODIFIES: this
    // EFFECTS: positions this window at the centre of the screen
    private void centreOnScreen() {
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((screenWidth - getWidth()) / 2, (screenHeight - getHeight()) / 2);
    }

    // MODIFIES: this
    // EFFECTS: asks the user whether to load their saved goals, and loads them
    //          if they say yes. Sessions are not mentioned because they come
    //          out of the database on their own.
    private void promptLoadOnStart() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Would you like to load your previously saved goals?\n"
                + "(Your training sessions are loaded automatically.)",
                "Load Goals", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            loadFromFile();
        }
    }

    // MODIFIES: the file at JSON_STORE
    // EFFECTS: writes the goals to file and returns true; shows an error dialog
    //          and returns false if the file could not be written. An empty
    //          training log is written alongside them, because sessions are
    //          kept in the database now and the save file no longer holds them.
    private boolean saveToFile() {
        try {
            jsonWriter.open();
            jsonWriter.write(new TrainingLog(), goalLog);
            jsonWriter.close();
            EventLog.getInstance().logEvent(new Event("Saved "
                    + goalLog.size() + " goal(s) to file."));
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save to " + JSON_STORE, "Save Failed",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // MODIFIES: this
    // EFFECTS: replaces the goals with the ones stored in the save file and
    //          redraws the Goals tab; shows an error dialog if the file could
    //          not be read. Any sessions still in the file are ignored: the
    //          database is where sessions live now.
    private void loadFromFile() {
        try {
            TrackerData data = jsonReader.read();
            goalLog = data.getGoalLog();
            goalsPanel.setLog(goalLog);
            EventLog.getInstance().logEvent(new Event("Loaded "
                    + goalLog.size() + " goal(s) from file."));
            JOptionPane.showMessageDialog(this,
                    "Goals loaded successfully.", "Loaded",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load from " + JSON_STORE, "Load Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this
    // EFFECTS: closes the database connection, ignoring a failure to close
    //          because the app is quitting anyway
    private void closeDatabase() {
        try {
            database.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database cleanly: " + e.getMessage());
        }
    }

    // MODIFIES: this, the file at JSON_STORE
    // EFFECTS: asks whether to save the goals before quitting. Yes saves and
    //          then quits, No quits without saving, Cancel leaves the window
    //          open. If the save fails the app stays open so the user does not
    //          lose their goals. Sessions are never at risk here: they were
    //          written to the database as they were made.
    //          The database is closed and the event log printed to the console
    //          only when the app really does quit.
    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Would you like to save your goals before exiting?\n"
                + "(Your training sessions are already saved.)",
                "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
        if (choice != JOptionPane.YES_OPTION && choice != JOptionPane.NO_OPTION) {
            return;
        }
        if (choice == JOptionPane.YES_OPTION && !saveToFile()) {
            return;
        }
        closeDatabase();
        printEventLog(EventLog.getInstance());
        System.exit(0);
    }

    // EFFECTS: prints every event that happened this run to the console
    private void printEventLog(EventLog el) {
        for (Event e : el) {
            System.out.println(e.toString());
            System.out.println();
        }
    }

    /**
     * Represents the action taken when the user wants to save their data
     * without closing the app.
     */
    private class SaveAction extends AbstractAction {

        SaveAction() {
            super("Save Goals");
        }

        // MODIFIES: the file at JSON_STORE
        // EFFECTS: saves both logs to file and confirms it to the user
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (saveToFile()) {
                JOptionPane.showMessageDialog(TrainingTrackerGUI.this,
                        "Goals saved successfully.", "Saved",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Represents the action taken when the user wants to load previously saved
     * data from file.
     */
    private class LoadAction extends AbstractAction {

        LoadAction() {
            super("Load Goals");
        }

        // MODIFIES: TrainingTrackerGUI.this
        // EFFECTS: replaces the current data with what is stored in the save file
        @Override
        public void actionPerformed(ActionEvent evt) {
            loadFromFile();
        }
    }

    /**
     * Represents the action taken when the user wants to close the app.
     */
    private class ExitAction extends AbstractAction {

        ExitAction() {
            super("Exit");
        }

        // EFFECTS: runs the same save-then-quit prompt as the window's X button
        @Override
        public void actionPerformed(ActionEvent evt) {
            confirmExit();
        }
    }
}
