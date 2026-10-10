package ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import model.Event;
import model.EventLog;
import persistence.TrackerDatabase;

// Referenced from AlarmSystem demo

// Represents the main application window. It hosts the Sessions and Goals tabs
// and owns the one connection to the database that both of them use.
//
// Both tabs now write to the database the moment anything changes, so there is
// no Save button and no save prompt on the way out: there is never unsaved work
// to lose. Each tab builds and manages its own table and buttons, so this class
// only deals with the window itself and with the database connection.
@ExcludeFromJacocoGeneratedReport
public class TrainingTrackerGUI extends JFrame {

    private static final int WIDTH = 880;
    private static final int HEIGHT = 580;

    private final TrackerDatabase database;
    private final SessionsPanel sessionsPanel;
    private final GoalsPanel goalsPanel;

    // EFFECTS: opens the database, then builds and shows the window. Quits with
    //          an explanation if the database cannot be opened, because without
    //          it there is nothing to show and nowhere to put new entries.
    public TrainingTrackerGUI() {
        super("Ultimate Training Tracker");

        database = openDatabaseOrQuit();
        sessionsPanel = new SessionsPanel(database);
        goalsPanel = new GoalsPanel(database);

        buildWindow();
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
    //          the window's X button shut down the same way the Exit button does
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
                shutDown();
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

    // EFFECTS: returns the bar along the bottom of the window
    private JPanel createBottomBar() {
        JPanel panel = UiTheme.createButtonBar();
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
    // EFFECTS: closes the database, prints everything that happened this run to
    //          the console, and quits. There is no "save before leaving?"
    //          question because every change was written as it was made.
    private void shutDown() {
        closeDatabase();
        printEventLog(EventLog.getInstance());
        System.exit(0);
    }

    // MODIFIES: this
    // EFFECTS: closes the database connection, reporting but not acting on a
    //          failure to close, because the app is quitting anyway
    private void closeDatabase() {
        try {
            database.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database cleanly: " + e.getMessage());
        }
    }

    // EFFECTS: prints every event that happened this run to the console
    private void printEventLog(EventLog el) {
        for (Event e : el) {
            System.out.println(e.toString());
            System.out.println();
        }
    }

    /**
     * Represents the action taken when the user wants to close the app.
     */
    private class ExitAction extends AbstractAction {

        ExitAction() {
            super("Exit");
        }

        // EFFECTS: shuts the app down the same way the window's X button does
        @Override
        public void actionPerformed(ActionEvent evt) {
            shutDown();
        }
    }
}
