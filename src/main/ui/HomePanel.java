package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;
import model.Quote;
import persistence.SessionStats;
import persistence.TrackerDatabase;

// Represents the home screen: one fact or line about ultimate, set large
// enough to fill the page, with the running totals tucked into the corner.
//
// The colours and the typeface are picked at random each time the app opens,
// from pairings that are known to stay readable, so the screen looks like a
// different poster every time without ever becoming unreadable.
//
// The totals take the quote's own foreground colour rather than the usual
// lime and pink, so the page stays two colours however it was painted.
@ExcludeFromJacocoGeneratedReport
class HomePanel extends JPanel {

    private static final int QUOTE_SIZE = 64;

    private final TrackerDatabase database;
    private final UiTheme.QuoteStyle style;
    private final JLabel hoursValue = new JLabel("0");
    private final JLabel sessionsValue = new JLabel("0");
    private final JLabel goalsValue = new JLabel("0");

    // REQUIRES: database != null
    // EFFECTS: builds the home screen around a randomly chosen quote and
    //          colour scheme, and fills in the totals from the database
    HomePanel(TrackerDatabase database) {
        super(new BorderLayout());
        this.database = database;
        this.style = UiTheme.randomQuoteStyle();

        Quote quote = Quote.random();

        setBackground(style.getBackground());
        setBorder(BorderFactory.createEmptyBorder(34, 44, 40, 44));
        add(createTop(quote), BorderLayout.NORTH);
        add(createQuote(quote), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        refresh();
    }

    // MODIFIES: this
    // EFFECTS: re-reads the totals from the database; shows dashes if they
    //          cannot be read, because a home screen is not worth an error
    //          dialog the user cannot act on
    void refresh() {
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

    // REQUIRES: quote != null
    // EFFECTS: returns the category on the left and the totals on the right
    private JPanel createTop(Quote quote) {
        JLabel category = new JLabel(quote.getCategory().toUpperCase());
        category.setFont(UiTheme.TINY_BOLD_FONT);
        category.setForeground(UiTheme.fade(style.getForeground(), 200));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(category, BorderLayout.WEST);
        top.add(createTotals(), BorderLayout.EAST);
        return top;
    }

    // EFFECTS: returns the three running totals as a row for the top corner
    private JPanel createTotals() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 22, 0));
        row.setOpaque(false);
        row.add(createTotal(hoursValue, "hours"));
        row.add(createTotal(sessionsValue, "sessions"));
        row.add(createTotal(goalsValue, "goals done"));
        return row;
    }

    // REQUIRES: valueLabel != null
    // EFFECTS: returns one total: the figure above its caption, right-aligned
    //          and painted in whichever colour the quote is using
    private JPanel createTotal(JLabel valueLabel, String caption) {
        valueLabel.setFont(style.font(26));
        valueLabel.setForeground(style.getForeground());
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel captionLabel = new JLabel(caption.toUpperCase());
        captionLabel.setFont(UiTheme.TINY_BOLD_FONT);
        captionLabel.setForeground(UiTheme.fade(style.getForeground(), 150));
        captionLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel tile = new JPanel(new BorderLayout());
        tile.setOpaque(false);
        tile.add(valueLabel, BorderLayout.NORTH);
        tile.add(captionLabel, BorderLayout.SOUTH);
        return tile;
    }

    // REQUIRES: quote != null
    // EFFECTS: returns the quote itself, set large. The HTML wrapper is what
    //          makes a JLabel wrap onto more than one line.
    private JLabel createQuote(Quote quote) {
        JLabel label = new JLabel("<html>" + quote.getText().toUpperCase() + "</html>");
        label.setFont(style.font(QUOTE_SIZE));
        label.setForeground(style.getForeground());
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return label;
    }

    // EFFECTS: returns the quiet line along the bottom of the page
    private JLabel createFooter() {
        JLabel footer = new JLabel("A NEW ONE EVERY TIME YOU OPEN THE APP");
        footer.setFont(UiTheme.TINY_BOLD_FONT);
        footer.setForeground(UiTheme.fade(style.getForeground(), 130));
        return footer;
    }

    // EFFECTS: returns the background this screen was painted with, so the
    //          window can match the rest of its frame to it
    Color getQuoteBackground() {
        return style.getBackground();
    }
}
