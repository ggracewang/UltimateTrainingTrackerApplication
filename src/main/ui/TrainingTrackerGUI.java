package ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

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

// Referenced from AlarmSystem demo

// Represents the main application window. It owns the two logs and the two
// files-on-disk helpers, and it hosts the Sessions and Goals tabs plus the
// Save / Load / Exit bar along the bottom.
//
// Each tab builds and manages its own table and buttons (see SessionsPanel and
// GoalsPanel), so this class only deals with the window itself and with saving
// and loading.
@ExcludeFromJacocoGeneratedReport
public class TrainingTrackerGUI extends JFrame {

    private static final int WIDTH = 880;
    private static final int HEIGHT = 580;
    private static final String JSON_STORE = "./data/trainingtracker.json";

    private TrainingLog trainingLog;
    private GoalLog goalLog;
    private final JsonReader jsonReader;
    private final JsonWriter jsonWriter;
    private final SessionsPanel sessionsPanel;
    private final GoalsPanel goalsPanel;

    // EFFECTS: creates empty logs, builds and shows the window, then asks the
    //          user whether they want to load their saved data
    public TrainingTrackerGUI() {
        super("Ultimate Training Tracker");

        trainingLog = new TrainingLog();
        goalLog = new GoalLog();
        jsonReader = new JsonReader(JSON_STORE);
        jsonWriter = new JsonWriter(JSON_STORE);
        sessionsPanel = new SessionsPanel(trainingLog);
        goalsPanel = new GoalsPanel(goalLog);

        buildWindow();
        promptLoadOnStart();
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
    // EFFECTS: asks the user whether to load their saved data, and loads it if
    //          they say yes
    private void promptLoadOnStart() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Would you like to load your previously saved sessions and goals?",
                "Load Data", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            loadFromFile();
        }
    }

    // MODIFIES: the file at JSON_STORE
    // EFFECTS: writes both logs to file and returns true; shows an error dialog
    //          and returns false if the file could not be written
    private boolean saveToFile() {
        try {
            jsonWriter.open();
            jsonWriter.write(trainingLog, goalLog);
            jsonWriter.close();
            EventLog.getInstance().logEvent(new Event("Saved "
                    + trainingLog.size() + " training session(s) and "
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
    // EFFECTS: replaces both logs with the ones stored in the save file and
    //          redraws both tabs; shows an error dialog if the file could not
    //          be read
    private void loadFromFile() {
        try {
            TrackerData data = jsonReader.read();
            trainingLog = data.getTrainingLog();
            goalLog = data.getGoalLog();
            sessionsPanel.setLog(trainingLog);
            goalsPanel.setLog(goalLog);
            EventLog.getInstance().logEvent(new Event("Loaded "
                    + trainingLog.size() + " training session(s) and "
                    + goalLog.size() + " goal(s) from file."));
            JOptionPane.showMessageDialog(this,
                    "Data loaded successfully.", "Loaded",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load from " + JSON_STORE, "Load Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this, the file at JSON_STORE
    // EFFECTS: asks whether to save before quitting. Yes saves and then quits,
    //          No quits without saving, Cancel leaves the window open. If the
    //          save fails the app stays open so the user does not lose data.
    //          The event log is printed to the console only when the app really
    //          does quit.
    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Would you like to save your data before exiting?",
                "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
        if (choice != JOptionPane.YES_OPTION && choice != JOptionPane.NO_OPTION) {
            return;
        }
        if (choice == JOptionPane.YES_OPTION && !saveToFile()) {
            return;
        }
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
            super("Save");
        }

        // MODIFIES: the file at JSON_STORE
        // EFFECTS: saves both logs to file and confirms it to the user
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (saveToFile()) {
                JOptionPane.showMessageDialog(TrainingTrackerGUI.this,
                        "Data saved successfully.", "Saved",
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
            super("Load");
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
