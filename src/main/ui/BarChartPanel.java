package ui;

import model.TrainingSession;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

// Referenced from AlarmSystem — AlarmUI.java 

/**
 * Represents a custom-painted bar chart showing training session durations.
 */
@ExcludeFromJacocoGeneratedReport

public class BarChartPanel extends JPanel {

    private List<TrainingSession> sessions;
    private static final int MARGIN_LEFT   = 48;
    private static final int MARGIN_RIGHT  = 16;
    private static final int MARGIN_TOP    = 28;
    private static final int MARGIN_BOTTOM = 42;

    private static final Color COLOUR_BAR      = new Color(41,  128, 185);
    private static final Color COLOUR_BAR_DARK = new Color(28,  96,  145);
    private static final Color COLOUR_AXIS     = new Color(110, 110, 110);
    private static final Color COLOUR_GRID     = new Color(232, 232, 232);
    private static final Color COLOUR_LABEL    = new Color(70,  70,  70);

    // EFFECTS: constructs an empty bar chart panel with a white background
    public BarChartPanel() {
        sessions = new ArrayList<>();
        setBackground(Color.WHITE);
    }

    // MODIFIES: this
    // EFFECTS: sets the session data to visualise-- makes acopy so
    //          external changes to the list do not affect this panel
    public void setData(List<TrainingSession> sessions) {
        this.sessions = new ArrayList<>(sessions);
    }

    // MODIFIES: g
    // EFFECTS: paints the bar chart onto this panel; draws a title, grid lines,
    //          one bar per session scaled to the maximum duration, value labels
    //          above each bar, session index labels below the X axis, and axes.
    //          If sessions is empty, draws a placeholder message instead.
    //          super.paintComponent(g) is called first to clear the background —
    //          without it old drawings stack up on every repaint.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();
        int chartW = panelW - MARGIN_LEFT - MARGIN_RIGHT;
        int chartH = panelH - MARGIN_TOP  - MARGIN_BOTTOM;

        g2.setColor(COLOUR_LABEL);
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.drawString("Duration (min)", MARGIN_LEFT, MARGIN_TOP - 6);

        if (sessions.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            g2.drawString("No sessions yet", MARGIN_LEFT + 10, MARGIN_TOP + chartH / 2);
            drawAxes(g2, chartW, chartH);
            return;
        }

        int maxDuration = findMaxDuration();
        drawGridLines(g2, chartW, chartH, maxDuration);
        drawBars(g2, chartW, chartH, maxDuration);
        drawXAxisLabel(g2, chartW, panelH);
        drawAxes(g2, chartW, chartH);
    }

    // EFFECTS: returns the maximum duration across all sessions;
    //          *returns 1 if all durations are zero to prevent division by zero
    private int findMaxDuration() {
        int max = 1;
        for (TrainingSession s : sessions) {
            if (s.getDuration() > max) {
                max = s.getDuration();
            }
        }
        return max;
    }

    // MODIFIES: g2
    // EFFECTS: draws four evenly-spaced horizontal grid lines across the chart
    //          area, and the corresponding Y-axis value labels to their left
    private void drawGridLines(Graphics2D g2, int chartW,
                                int chartH, int maxDuration) {
        for (int i = 1; i <= 4; i++) {
            int gridY = MARGIN_TOP + chartH - (chartH * i / 4);
            g2.setColor(COLOUR_GRID);
            g2.drawLine(MARGIN_LEFT, gridY, MARGIN_LEFT + chartW, gridY);
            g2.setColor(COLOUR_AXIS);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String val = String.valueOf(maxDuration * i / 4);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(val, MARGIN_LEFT - fm.stringWidth(val) - 4, gridY + 4);
        }
    }

    // MODIFIES: g2
    // EFFECTS: draws one filled bar per session, scaled so the tallest bar
    //          fills chartH pixels; draws a darker border on each bar;
    //          calls drawBarValueLabel and drawBarIndelabel for each bar.
    //          Bar height formula: (double)duration / maxDuration * chartH 
    private void drawBars(Graphics2D g2, int chartW, int chartH, int maxDuration) {
        int n    = sessions.size();
        int gap  = 6;
        int barW = Math.max(2, (chartW - gap * (n + 1)) / n);

        for (int i = 0; i < n; i++) {
            int duration = sessions.get(i).getDuration();
            int barH = (int)((double) duration / maxDuration * chartH);
            int x = MARGIN_LEFT + gap + i * (barW + gap);
            int y = MARGIN_TOP + chartH - barH;
            g2.setColor(COLOUR_BAR);
            g2.fillRect(x, y, barW, barH);
            g2.setColor(COLOUR_BAR_DARK);
            g2.drawRect(x, y, barW, barH);
            drawBarValueLabel(g2, x, y, barW, duration);
            drawBarIndelabel(g2, x, barW, chartH, i);
        }
    }

    // MODIFIES: g2
    // EFFECTS: draws the duration value centred above the bar if barW >= 14--
    //          does nothing otherwise
    private void drawBarValueLabel(Graphics2D g2, int x, int y,
                                    int barW, int duration) {
        if (barW >= 14) {
            g2.setColor(COLOUR_LABEL);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String lbl = String.valueOf(duration);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, x + (barW - fm.stringWidth(lbl)) / 2, y - 3);
        }
    }

    // MODIFIES: g2
    // EFFECTS: draws the 1-based session index centred below the X axis
    //          under the bar if barW >= 8; does nothing otherwise
    private void drawBarIndelabel(Graphics2D g2, int x, int barW, int chartH, int index) {
        if (barW >= 8) {
            g2.setColor(COLOUR_AXIS);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String num = String.valueOf(index + 1);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(num, x + (barW - fm.stringWidth(num)) / 2,
                    MARGIN_TOP + chartH + 14);
        }
    }

    // MODIFIES: g2
    // EFFECTS: draws the "Session #" label centred horizontally below the chart
    private void drawXAxisLabel(Graphics2D g2, int chartW, int panelH) {
        g2.setColor(COLOUR_AXIS);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        String label = "Session #";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, MARGIN_LEFT + (chartW - fm.stringWidth(label)) / 2, panelH - 5);
    }

    // MODIFIES: g2
    // EFFECTS: draws the Y axis (vertical) and X axis (horizontal) lines
    //          using a 1.5px stroke, then resets the stroke to 1px
    private void drawAxes(Graphics2D g2, int chartW, int chartH) {
        g2.setColor(COLOUR_AXIS);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(MARGIN_LEFT, MARGIN_TOP,
                    MARGIN_LEFT, MARGIN_TOP + chartH);
        g2.drawLine(MARGIN_LEFT,          MARGIN_TOP + chartH,
                    MARGIN_LEFT + chartW, MARGIN_TOP + chartH);
        g2.setStroke(new BasicStroke(1f));
    }
}