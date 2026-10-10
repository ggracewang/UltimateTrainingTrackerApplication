package ui;

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
 * Represents a custom-painted bar chart: one bar per value, with a label under
 * each bar.
 * It knows nothing about training sessions or goals. It is handed a list of
 * labels and a list of numbers and draws them, so the same chart can show
 * minutes per month, minutes per session, or anything else counted up later.
 */
@ExcludeFromJacocoGeneratedReport
public class BarChartPanel extends JPanel {

    private static final int MARGIN_LEFT   = 48;
    private static final int MARGIN_RIGHT  = 16;
    private static final int MARGIN_TOP    = 28;
    private static final int MARGIN_BOTTOM = 42;

    private static final Color COLOUR_BAR      = new Color(41,  128, 185);
    private static final Color COLOUR_BAR_DARK = new Color(28,  96,  145);
    private static final Color COLOUR_AXIS     = new Color(110, 110, 110);
    private static final Color COLOUR_GRID     = new Color(232, 232, 232);
    private static final Color COLOUR_LABEL    = new Color(70,  70,  70);

    private List<String> labels;
    private List<Integer> values;
    private String valueAxisTitle;
    private String categoryAxisTitle;
    private String emptyMessage;

    // EFFECTS: constructs an empty bar chart panel with a white background
    public BarChartPanel() {
        labels = new ArrayList<>();
        values = new ArrayList<>();
        valueAxisTitle = "Value";
        categoryAxisTitle = "";
        emptyMessage = "Nothing to chart yet";
        setBackground(Color.WHITE);
    }

    // REQUIRES: labels.size() == values.size()
    // MODIFIES: this
    // EFFECTS: sets the bars to draw, one per entry, in the order given. Copies
    //          are taken so that later changes to the caller's lists do not
    //          alter what is on screen.
    public void setData(List<String> labels, List<Integer> values) {
        this.labels = new ArrayList<>(labels);
        this.values = new ArrayList<>(values);
    }

    // MODIFIES: this
    // EFFECTS: sets the wording shown above the chart, below the chart, and in
    //          place of the chart when there is nothing to draw
    public void setTitles(String valueAxisTitle, String categoryAxisTitle, String emptyMessage) {
        this.valueAxisTitle = valueAxisTitle;
        this.categoryAxisTitle = categoryAxisTitle;
        this.emptyMessage = emptyMessage;
    }

    // MODIFIES: g
    // EFFECTS: paints the bar chart onto this panel; draws a title, grid lines,
    //          one bar per value scaled to the largest one, value labels above
    //          each bar, category labels below the X axis, and axes.
    //          If there are no values, draws a placeholder message instead.
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
        g2.drawString(valueAxisTitle, MARGIN_LEFT, MARGIN_TOP - 6);

        if (values.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            g2.drawString(emptyMessage, MARGIN_LEFT + 10, MARGIN_TOP + chartH / 2);
            drawAxes(g2, chartW, chartH);
            return;
        }

        int maxValue = findMaxValue();
        drawGridLines(g2, chartW, chartH, maxValue);
        drawBars(g2, chartW, chartH, maxValue);
        drawXAxisTitle(g2, chartW, panelH);
        drawAxes(g2, chartW, chartH);
    }

    // EFFECTS: returns the largest value being charted;
    //          returns 1 if every value is zero, to prevent division by zero
    private int findMaxValue() {
        int max = 1;
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    // MODIFIES: g2
    // EFFECTS: draws four evenly-spaced horizontal grid lines across the chart
    //          area, and the corresponding Y-axis value labels to their left
    private void drawGridLines(Graphics2D g2, int chartW, int chartH, int maxValue) {
        for (int i = 1; i <= 4; i++) {
            int gridY = MARGIN_TOP + chartH - (chartH * i / 4);
            g2.setColor(COLOUR_GRID);
            g2.drawLine(MARGIN_LEFT, gridY, MARGIN_LEFT + chartW, gridY);
            g2.setColor(COLOUR_AXIS);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String val = String.valueOf(maxValue * i / 4);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(val, MARGIN_LEFT - fm.stringWidth(val) - 4, gridY + 4);
        }
    }

    // MODIFIES: g2
    // EFFECTS: draws one filled bar per value, scaled so the tallest bar fills
    //          chartH pixels; draws a darker border on each bar; calls
    //          drawBarValueLabel and drawBarCategoryLabel for each bar.
    //          Bar height formula: (double) value / maxValue * chartH
    private void drawBars(Graphics2D g2, int chartW, int chartH, int maxValue) {
        int n    = values.size();
        int gap  = 6;
        int barW = Math.max(2, (chartW - gap * (n + 1)) / n);

        for (int i = 0; i < n; i++) {
            int value = values.get(i);
            int barH = (int) ((double) value / maxValue * chartH);
            int x = MARGIN_LEFT + gap + i * (barW + gap);
            int y = MARGIN_TOP + chartH - barH;
            g2.setColor(COLOUR_BAR);
            g2.fillRect(x, y, barW, barH);
            g2.setColor(COLOUR_BAR_DARK);
            g2.drawRect(x, y, barW, barH);
            drawBarValueLabel(g2, x, y, barW, value);
            drawBarCategoryLabel(g2, x, barW, chartH, i);
        }
    }

    // MODIFIES: g2
    // EFFECTS: draws the value centred above the bar if it fits; does nothing
    //          otherwise, so narrow bars are left unlabelled rather than
    //          overlapping each other
    private void drawBarValueLabel(Graphics2D g2, int x, int y, int barW, int value) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        String text = String.valueOf(value);
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(text) <= barW) {
            g2.setColor(COLOUR_LABEL);
            g2.drawString(text, x + (barW - fm.stringWidth(text)) / 2, y - 3);
        }
    }

    // MODIFIES: g2
    // EFFECTS: draws this bar's label centred below the X axis if it fits;
    //          does nothing otherwise
    private void drawBarCategoryLabel(Graphics2D g2, int x, int barW, int chartH, int index) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        String text = labels.get(index);
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(text) <= barW) {
            g2.setColor(COLOUR_AXIS);
            g2.drawString(text, x + (barW - fm.stringWidth(text)) / 2, MARGIN_TOP + chartH + 14);
        }
    }

    // MODIFIES: g2
    // EFFECTS: draws the category axis title centred horizontally below the chart
    private void drawXAxisTitle(Graphics2D g2, int chartW, int panelH) {
        g2.setColor(COLOUR_AXIS);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(categoryAxisTitle,
                MARGIN_LEFT + (chartW - fm.stringWidth(categoryAxisTitle)) / 2, panelH - 5);
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
