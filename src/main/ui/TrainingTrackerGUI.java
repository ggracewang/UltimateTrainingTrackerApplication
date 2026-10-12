package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import model.Event;
import model.EventLog;
import persistence.SessionStats;
import persistence.TrackerDatabase;

// Referenced from AlarmSystem demo

// Represents the main application window: a deep blue banner carrying the
// title and three running totals, a row of flat tabs beneath it, and whichever
// tab is showing.
//
// The tabs are plain labels over a CardLayout rather than a JTabbedPane,
// because a JTabbedPane is drawn by the operating system and ignores the
// theme, which left a grey strip across an otherwise blue and lime window.
//
// Both tabs write to the database the moment anything changes, so there is no
// Save button and no save prompt on the way out: there is never unsaved work.
@ExcludeFromJacocoGeneratedReport
public class TrainingTrackerGUI extends JFrame {

    private static final int WIDTH = 980;
    private static final int HEIGHT = 680;
    private static final String SESSIONS = "Sessions";
    private static final String GOALS = "Goals";

    private final TrackerDatabase database;
    private final SessionsPanel sessionsPanel;
    private final GoalsPanel goalsPanel;

    private final JLabel hoursValue = new JLabel("0");
    private final JLabel sessionsValue = new JLabel("0");
    private final JLabel goalsValue = new JLabel("0");

    private final CardLayout cards = new CardLayout();
    private final JPanel cardHolder = new JPanel(cards);
    private final JLabel sessionsTab = createTab(SESSIONS);
    private final JLabel goalsTab = createTab(GOALS);

    // EFFECTS: opens the database, then builds and shows the window. Quits with
    //          an explanation if the database cannot be opened, because without
    //          it there is nothing to show and nowhere to put new entries.
    public TrainingTrackerGUI() {
        super("Ultimate Training Tracker");

        database = openDatabaseOrQuit();
        sessionsPanel = new SessionsPanel(database, this::refreshTotals);
        goalsPanel = new GoalsPanel(database, this::refreshTotals);

        buildWindow();
        refreshTotals();
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
        getContentPane().setBackground(UiTheme.PAPER);

        cardHolder.setBackground(UiTheme.PAPER);
        cardHolder.add(sessionsPanel, SESSIONS);
        cardHolder.add(goalsPanel, GOALS);

        add(createBanner(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);
        showTab(SESSIONS);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutDown();
            }
        });

        centreOnScreen();
        setVisible(true);
    }

    // EFFECTS: returns the blue banner: the title on the left, the three
    //          running totals on the right
    private JPanel createBanner() {
        JPanel totals = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        totals.setOpaque(false);
        totals.add(UiTheme.createStatTile(hoursValue, "hours", UiTheme.LIME));
        totals.add(UiTheme.createStatTile(sessionsValue, "sessions", UiTheme.LIME));
        totals.add(UiTheme.createStatTile(goalsValue, "goals done", UiTheme.PINK));

        JPanel banner = UiTheme.createBanner("Training Tracker",
                "ultimate  -  train  -  track  -  improve");
        banner.add(totals, BorderLayout.EAST);
        return banner;
    }

    // EFFECTS: returns the tab strip with whichever tab is showing beneath it
    private JPanel createBody() {
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tabs.setBackground(UiTheme.PAPER);
        tabs.setBorder(BorderFactory.createEmptyBorder(18, 26, 12, 26));
        tabs.add(sessionsTab);
        tabs.add(goalsTab);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UiTheme.PAPER);
        body.add(tabs, BorderLayout.NORTH);
        body.add(cardHolder, BorderLayout.CENTER);
        return body;
    }

    // EFFECTS: returns one clickable tab label showing the given name
    private JLabel createTab(String name) {
        JLabel tab = new JLabel(name.toUpperCase());
        tab.setFont(UiTheme.SECTION_FONT);
        tab.setOpaque(true);
        tab.setBorder(BorderFactory.createEmptyBorder(11, 24, 11, 24));
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTab(name);
            }
        });
        return tab;
    }

    // MODIFIES: this
    // EFFECTS: brings the named tab to the front and repaints both tab labels
    //          so the one showing is the dark one
    private void showTab(String name) {
        cards.show(cardHolder, name);
        paintTab(sessionsTab, SESSIONS.equals(name));
        paintTab(goalsTab, GOALS.equals(name));
    }

    // MODIFIES: tab
    // EFFECTS: paints the given tab as the selected one or as a quiet one
    private void paintTab(JLabel tab, boolean selected) {
        tab.setBackground(selected ? UiTheme.INK : UiTheme.PANEL_BG);
        tab.setForeground(selected ? UiTheme.LIME : UiTheme.MUTED_FG);
    }

    // MODIFIES: this
    // EFFECTS: re-reads the three banner totals from the database. Called once
    //          at startup and again by either tab whenever it changes
    //          something, so the figures on the banner cannot go stale.
    private void refreshTotals() {
        try {
            SessionStats stats = database.getSessionStats();
            hoursValue.setText(String.format("%.1f", stats.getTotalHours()));
            sessionsValue.setText(String.valueOf(stats.getSessionCount()));
            goalsValue.setText(String.valueOf(database.getCompletedGoals().size()));
        } catch (SQLException e) {
            hoursValue.setText("--");
            sessionsValue.setText("--");
            goalsValue.setText("--");
        }
    }

    // EFFECTS: returns the bar along the bottom of the window
    private JPanel createBottomBar() {
        JPanel panel = UiTheme.createButtonBar();
        panel.add(UiTheme.createSecondaryButton(new ExitAction()));
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
