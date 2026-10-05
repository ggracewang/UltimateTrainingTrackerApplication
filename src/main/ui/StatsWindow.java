package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import model.TrainingLog;

// Referenced from AlarmSystem demo

/**
 * Represents the stats window: a bar chart of how long each training session
 * lasted, with a line of totals above it.
 * Opened from the Sessions tab when the user clicks "View Stats".
 * Uses DISPOSE_ON_CLOSE so closing this window does not exit the application.
 */
@ExcludeFromJacocoGeneratedReport
class StatsWindow extends JFrame {

    private static final int WIDTH = 560;
    private static final int HEIGHT = 430;

    // REQUIRES: trainingLog != null
    // EFFECTS: builds and shows the stats window for the given training log
    public StatsWindow(TrainingLog trainingLog) {
        super("Training Stats");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        add(createNorth(trainingLog), BorderLayout.NORTH);
        add(createChartPanel(trainingLog), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        centreOnScreen();
        setVisible(true);
    }

    // REQUIRES: trainingLog != null
    // EFFECTS: returns the dark header bar with the totals line stacked below it
    private JPanel createNorth(TrainingLog trainingLog) {
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(UiTheme.createHeader("Duration per Session",
                "Minutes practiced per training session"));
        north.add(createTotalsBar(trainingLog));
        return north;
    }

    // REQUIRES: trainingLog != null
    // EFFECTS: returns a strip showing the session count, total hours, average
    //          session length, and longest session
    private JPanel createTotalsBar(TrainingLog trainingLog) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UiTheme.PANEL_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 22, 10, 22)));

        JLabel totals = new JLabel(String.format(
                "%d sessions      %.1f hours total      %.0f min average      %d min longest",
                trainingLog.size(), trainingLog.getTotalHoursPracticed(),
                trainingLog.getAverageSessionDuration(), trainingLog.getLongestSessionDuration()));
        totals.setFont(UiTheme.SMALL_FONT);
        totals.setForeground(UiTheme.LABEL_FG);

        bar.add(totals, BorderLayout.WEST);
        return bar;
    }

    // REQUIRES: trainingLog != null
    // EFFECTS: returns a white panel holding the bar chart of the given sessions
    private JPanel createChartPanel(TrainingLog trainingLog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 22));
        BarChartPanel chart = new BarChartPanel();
        chart.setData(trainingLog.getAll());
        panel.add(chart, BorderLayout.CENTER);
        return panel;
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
