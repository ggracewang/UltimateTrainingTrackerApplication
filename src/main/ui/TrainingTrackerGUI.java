package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import model.Event;
import model.EventLog;
import persistence.TrackerDatabase;

// Referenced from AlarmSystem demo

// Represents the main application window: a blue column down the left holding
// the title and the three pages, and whichever page is showing filling the
// rest.
//
// The nav is a column of labels over a CardLayout rather than a JTabbedPane.
// A JTabbedPane is drawn by the operating system and ignores the theme, and a
// side column gives the blue somewhere to be a block of colour rather than a
// stripe, which is the look the app is after.
//
// Every page writes to the database the moment anything changes, so there is
// no Save button and no save prompt on the way out.
@ExcludeFromJacocoGeneratedReport
public class TrainingTrackerGUI extends JFrame {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 680;
    private static final int NAV_WIDTH = 210;

    private static final String HOME = "Home";
    private static final String SESSIONS = "Sessions";
    private static final String GOALS = "Goals";
    private static final String[] PAGES = {HOME, SESSIONS, GOALS};

    private final TrackerDatabase database;
    private final HomePanel homePanel;
    private final SessionsPanel sessionsPanel;
    private final GoalsPanel goalsPanel;

    private final CardLayout cards = new CardLayout();
    private final JPanel cardHolder = new JPanel(cards);
    private final JLabel[] navItems = new JLabel[PAGES.length];

    // EFFECTS: opens the database, then builds and shows the window. Quits with
    //          an explanation if the database cannot be opened, because without
    //          it there is nothing to show and nowhere to put new entries.
    public TrainingTrackerGUI() {
        super("Ultimate Training Tracker");

        database = openDatabaseOrQuit();
        homePanel = new HomePanel(database);
        sessionsPanel = new SessionsPanel(database, homePanel::refresh);
        goalsPanel = new GoalsPanel(database, homePanel::refresh);

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
    //          the window's X button shut the app down tidily
    private void buildWindow() {
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        cardHolder.add(homePanel, HOME);
        cardHolder.add(sessionsPanel, SESSIONS);
        cardHolder.add(goalsPanel, GOALS);

        add(createNav(), BorderLayout.WEST);
        add(cardHolder, BorderLayout.CENTER);
        showPage(HOME);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutDown();
            }
        });

        centreOnScreen();
        setVisible(true);
    }

    // EFFECTS: returns the blue column: the title at the top, the three pages
    //          below it, and the way out at the bottom
    private JPanel createNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UiTheme.BASE);
        nav.setPreferredSize(new Dimension(NAV_WIDTH, 0));
        nav.setBorder(BorderFactory.createCompoundBorder(
                // keeps the column distinct on the days the quote behind it
                // happens to be painted the same blue
                BorderFactory.createMatteBorder(0, 0, 0, 2, UiTheme.NAV_EDGE),
                BorderFactory.createEmptyBorder(26, 0, 22, 0)));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(createTitle(), BorderLayout.NORTH);
        top.add(createNavItems(), BorderLayout.CENTER);

        nav.add(top, BorderLayout.NORTH);
        nav.add(createExit(), BorderLayout.SOUTH);
        return nav;
    }

    // EFFECTS: returns the app name, stacked onto two lines so it can be set
    //          large in the narrow column
    private JPanel createTitle() {
        JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.setOpaque(false);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        for (String line : new String[]{"TRAINING", "TRACKER"}) {
            JLabel label = new JLabel(line);
            label.setFont(UiTheme.DISPLAY_FONT.deriveFont(34f));
            label.setForeground(UiTheme.LIME);
            label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            title.add(label);
        }
        return title;
    }

    // EFFECTS: returns the stack of clickable page names
    private JPanel createNavItems() {
        JPanel items = new JPanel();
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        items.setOpaque(false);
        for (int i = 0; i < PAGES.length; i++) {
            navItems[i] = createNavItem(PAGES[i]);
            items.add(navItems[i]);
        }
        return items;
    }

    // EFFECTS: returns one clickable entry in the nav column
    private JLabel createNavItem(String page) {
        JLabel item = new JLabel(page.toUpperCase());
        item.setFont(UiTheme.SECTION_FONT);
        item.setOpaque(true);
        item.setBorder(BorderFactory.createEmptyBorder(13, 20, 13, 20));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPage(page);
            }
        });
        return item;
    }

    // EFFECTS: returns the way out, sitting apart from the pages at the bottom
    private JLabel createExit() {
        JLabel exit = new JLabel("EXIT");
        exit.setFont(UiTheme.SECTION_FONT);
        exit.setForeground(UiTheme.ON_BASE_MUTED);
        exit.setBorder(BorderFactory.createEmptyBorder(13, 20, 0, 20));
        exit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                shutDown();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                exit.setForeground(UiTheme.PINK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exit.setForeground(UiTheme.ON_BASE_MUTED);
            }
        });
        return exit;
    }

    // MODIFIES: this
    // EFFECTS: brings the named page to the front and repaints the nav so the
    //          page being shown is the highlighted one
    private void showPage(String page) {
        cards.show(cardHolder, page);
        for (int i = 0; i < PAGES.length; i++) {
            boolean selected = PAGES[i].equals(page);
            navItems[i].setBackground(selected ? UiTheme.LIME : UiTheme.BASE);
            navItems[i].setForeground(selected ? UiTheme.INK : UiTheme.ON_BASE_MUTED);
        }
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
}
