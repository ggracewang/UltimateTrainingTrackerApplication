package ui;

import model.TrainingSession;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

// Referenced from AlarmSystem demo 

/**
 * Represents the stats window showing a bar chart of training session durations.
 * Opened from the main window when the user clicks "View Stats".
 * Uses DISPOSE_ON_CLOSE so closing this window does not exit the application.
 */
class StatsWindow extends JFrame {

    private static final int WIDTH  = 500;
    private static final int HEIGHT = 400;

    private static final Color COLOUR_HEADER = new Color(44,  62,  80);
    private static final Color COLOUR_PANEL  = new Color(248, 249, 252);
    private static final Color COLOUR_BORDER = new Color(218, 220, 228);

    // MODIFIES: this
    // EFFECTS: builds and displays the stats window containing a header,
    //          bar chart of the given sessions, and a close button
    public StatsWindow(List<TrainingSession> sessions) {
        super("Training Stats");
        setSize(WIDTH, HEIGHT);
        // DISPOSE_ON_CLOSE closes only this window.
        // EXIT_ON_CLOSE would terminate the entire application.
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        addHeader();
        addChartPanel(sessions);
        addFooter();
        centreOnScreen();
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: creates and adds the dark header panel to NORTH region
    private void addHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOUR_HEADER);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel title = new JLabel("Duration per Session");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Minutes practiced per training session");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(new Color(143, 168, 188));

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setOpaque(false);
        textStack.add(title);
        textStack.add(Box.createVerticalStrut(3));
        textStack.add(subtitle);

        panel.add(textStack, BorderLayout.WEST);
        add(panel, BorderLayout.NORTH);
    }

    // MODIFIES: this
    // EFFECTS: creates a BarChartPanel loaded with sessions and adds it
    //          to the CENTER region
    private void addChartPanel(List<TrainingSession> sessions) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 22));
        BarChartPanel chart = new BarChartPanel();
        chart.setData(sessions);
        panel.add(chart, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
    }

    // MODIFIES: this
    // EFFECTS: creates and adds the footer panel with a close button SOUTH
    private void addFooter() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOUR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOUR_BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        panel.add(new JButton(new CloseAction()));
        add(panel, BorderLayout.SOUTH);
    }

    // MODIFIES: this
    // EFFECTS: positions this window at the centre of the screen
    private void centreOnScreen() {
        int width  = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((width - getWidth()) / 2, (height - getHeight()) / 2);
    }

    /**
     * Represents the action to be taken when the user wants to close
     * the stats window and return to the main window.
     */
    private class CloseAction extends AbstractAction {

        CloseAction() {
            super("Close");
        }

        // MODIFIES: this
        // EFFECTS: disposes of this window; the main TrainingTrackerGUI
        //          window remains open and unaffected
        @Override
        public void actionPerformed(ActionEvent evt) {
            dispose();
        }
    }
}