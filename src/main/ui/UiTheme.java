package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

// Holds the colours, fonts, and shared furniture used by every window.
//
// The look is built from three colours: a deep blue for the big surfaces, a
// bright lime for anything the eye should land on, and a pink kept for things
// that actually mean something (goals completed, goals overdue) rather than
// for decoration. Lime is never used as text on a light background, where it
// is unreadable; it is always a fill with dark type on top of it.
//
// Keeping all of this in one class is what made the look changeable at all:
// the colours used to be typed out separately in every window.
@ExcludeFromJacocoGeneratedReport
public final class UiTheme {

    // --- palette -----------------------------------------------------------

    /** The deep blue the banner and headings are painted in. */
    public static final Color BASE = new Color(2, 29, 174);

    /** The bright lime used for the title, primary buttons, and selection. */
    public static final Color LIME = new Color(181, 248, 0);

    /** Kept for things that carry meaning: completed goals, overdue goals. */
    public static final Color PINK = new Color(255, 45, 149);

    /** Near-black blue used for body text and dark buttons. */
    public static final Color INK = new Color(10, 16, 51);

    public static final Color PAPER = Color.WHITE;
    public static final Color PANEL_BG = new Color(246, 247, 242);
    public static final Color BORDER = new Color(224, 226, 218);
    public static final Color LABEL_FG = new Color(55, 60, 75);
    public static final Color MUTED_FG = new Color(138, 143, 133);
    public static final Color SELECTION_BG = LIME;
    public static final Color ON_BASE_MUTED = new Color(255, 255, 255, 175);

    /** A darker blue edging the nav column, so it stays distinct whatever is beside it. */
    public static final Color NAV_EDGE = new Color(1, 18, 120);

    // --- type --------------------------------------------------------------

    // Impact is the condensed heavy face the design is built around. It ships
    // with Windows and macOS; anywhere it is missing the next available name
    // is used, so the app still runs rather than silently falling back to
    // something unreadable.
    private static final String HEAVY = firstAvailable(
            "Impact", "Haettenschweiler", "Arial Black", "DejaVu Sans Bold", "SansSerif");
    private static final String BODY = firstAvailable(
            "Segoe UI", "Helvetica Neue", "SansSerif");

    public static final Font DISPLAY_FONT = new Font(HEAVY, Font.PLAIN, 40);
    public static final Font STAT_FONT = new Font(HEAVY, Font.PLAIN, 30);
    public static final Font NUMBER_FONT = new Font(HEAVY, Font.PLAIN, 17);
    public static final Font SECTION_FONT = new Font(BODY, Font.BOLD, 13);
    public static final Font BODY_FONT = new Font(BODY, Font.PLAIN, 13);
    public static final Font SMALL_FONT = new Font(BODY, Font.PLAIN, 12);
    public static final Font TINY_BOLD_FONT = new Font(BODY, Font.BOLD, 10);
    public static final Font BUTTON_FONT = new Font(BODY, Font.BOLD, 12);

    private static final int ROW_HEIGHT = 36;

    // --- the home screen's rotating poster ---------------------------------

    // Background and foreground pairings for the quote on the home screen,
    // one picked at random each time the app opens. Only the colour changes:
    // the typeface and the letter spacing are fixed, so every quote is set
    // the same way and only the colour tells one opening from the next.
    private static final Color[][] QUOTE_PAIRS = {
        {PINK, LIME},   // pink ground, lime type
        {LIME, PINK},   // and the other way round
        {BASE, LIME},   // blue ground, lime type
        {LIME, BASE},   // and the other way round
        {BASE, PAPER},  // blue ground, white type
        {LIME, INK},    // lime ground, near-black type
    };

    private static final Random RANDOM = new Random();

    // Sayings are set tight, the way condensed display type is meant to be.
    // Facts are left at their natural spacing, because they are read as a
    // sentence rather than taken in as a shape.
    private static final float SAYING_TRACKING = -0.02f;
    private static final float FACT_TRACKING = 0f;

    /**
     * Represents one way of painting the home screen's quote. Only the two
     * colours vary; the type is the same every time.
     */
    public static final class QuoteStyle {
        private final Color background;
        private final Color foreground;

        private QuoteStyle(Color background, Color foreground) {
            this.background = background;
            this.foreground = foreground;
        }

        public Color getBackground() {
            return background;
        }

        public Color getForeground() {
            return foreground;
        }
    }

    // EFFECTS: returns a random colour pairing for the quote
    public static QuoteStyle randomQuoteStyle() {
        Color[] pair = QUOTE_PAIRS[RANDOM.nextInt(QUOTE_PAIRS.length)];
        return new QuoteStyle(pair[0], pair[1]);
    }

    // EFFECTS: returns the one face every saying is set in, at the given size
    public static Font sayingFont(int size) {
        return tracked(new Font(HEAVY, Font.PLAIN, size), SAYING_TRACKING);
    }

    // EFFECTS: returns the one face every fact is set in, at the given size
    public static Font factFont(int size) {
        return tracked(new Font(HEAVY, Font.PLAIN, size), FACT_TRACKING);
    }

    // EFFECTS: returns the given font with the given spacing between letters,
    //          measured as a fraction of the type size, so the spacing stays
    //          in proportion however large the text is set
    private static Font tracked(Font font, float tracking) {
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, tracking);
        return font.deriveFont(attributes);
    }

    // REQUIRES: 0 <= alpha <= 255
    // EFFECTS: returns the given colour at the given opacity, for text that
    //          should sit quietly against whichever background was picked
    public static Color fade(Color colour, int alpha) {
        return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
    }

    // EFFECTS: prevents this utility class from being instantiated
    private UiTheme() {
    }

    // EFFECTS: returns the first of the given font names that is installed on
    //          this computer; returns the last one if none of the others are
    private static String firstAvailable(String... names) {
        Set<String> installed = new HashSet<>();
        for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames()) {
            installed.add(name);
        }
        for (String name : names) {
            if (installed.contains(name)) {
                return name;
            }
        }
        return names[names.length - 1];
    }

    // --- banner ------------------------------------------------------------

    // EFFECTS: returns the deep blue banner across the top of a window, with
    //          the title set large in lime above a quieter subtitle
    public static JPanel createBanner(String title, String subtitle) {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(BASE);
        banner.setBorder(BorderFactory.createEmptyBorder(18, 26, 16, 26));
        banner.add(createBannerText(title, subtitle), BorderLayout.WEST);
        return banner;
    }

    // EFFECTS: returns the big lime title stacked above the subtitle
    public static JPanel createBannerText(String title, String subtitle) {
        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(DISPLAY_FONT);
        titleLabel.setForeground(LIME);

        JLabel subtitleLabel = new JLabel(subtitle.toUpperCase());
        subtitleLabel.setFont(TINY_BOLD_FONT);
        subtitleLabel.setForeground(ON_BASE_MUTED);

        JPanel stack = new JPanel(new BorderLayout());
        stack.setOpaque(false);
        stack.add(titleLabel, BorderLayout.NORTH);
        stack.add(subtitleLabel, BorderLayout.SOUTH);
        return stack;
    }

    // EFFECTS: returns a big number above a small caption, for the strip of
    //          figures that sits on the right-hand side of the banner
    public static JPanel createStatTile(JLabel valueLabel, String caption, Color valueColour) {
        valueLabel.setFont(STAT_FONT);
        valueLabel.setForeground(valueColour);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel captionLabel = new JLabel(caption.toUpperCase());
        captionLabel.setFont(TINY_BOLD_FONT);
        captionLabel.setForeground(ON_BASE_MUTED);

        JPanel tile = new JPanel(new BorderLayout());
        tile.setOpaque(false);
        tile.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 20));
        tile.add(valueLabel, BorderLayout.NORTH);
        tile.add(captionLabel, BorderLayout.SOUTH);
        return tile;
    }

    // --- buttons -----------------------------------------------------------

    /** The main action on a screen: lime, with dark type. */
    public static JButton createPrimaryButton(Action action) {
        return createButton(action, LIME, INK, new Color(199, 255, 60));
    }

    /** A quieter action: pale, with dark type. */
    public static JButton createSecondaryButton(Action action) {
        return createButton(action, PANEL_BG, INK, new Color(234, 236, 228));
    }

    /** A third-rank action: dark, with lime type. */
    public static JButton createTertiaryButton(Action action) {
        return createButton(action, INK, LIME, new Color(24, 32, 78));
    }

    // EFFECTS: returns a flat square button running the given action, painted
    //          in the given colours and lightening when the pointer is over it
    private static JButton createButton(Action action, Color background,
            Color foreground, Color hover) {
        JButton button = new JButton(action);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(11, 20, 11, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
        return button;
    }

    // --- tables ------------------------------------------------------------

    // EFFECTS: returns a styled, single-selection table showing the given
    //          model: roomy rows, no grid lines, a heavy rule under the column
    //          headings, and a full-width lime bar on the selected row
    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(ROW_HEIGHT);
        table.setFont(BODY_FONT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(INK);
        table.setBackground(PAPER);
        styleTableHeader(table);
        addRowRenderer(table);
        return table;
    }

    // MODIFIES: table
    // EFFECTS: styles the column heading row: small bold grey capitals over a
    //          white background, with a dark rule beneath
    private static void styleTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(TINY_BOLD_FONT);
        header.setBackground(PAPER);
        header.setForeground(MUTED_FG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, INK));
        header.setPreferredSize(new Dimension(0, 30));
        header.setReorderingAllowed(false);
    }

    // MODIFIES: table
    // EFFECTS: attaches a renderer that pads every cell, keeps unselected rows
    //          plain white, and sets any numeric column in the heavy face so
    //          the figures read as data rather than as words
    private static void addRowRenderer(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setFont(value instanceof Integer ? NUMBER_FONT : BODY_FONT);
                if (!isSelected) {
                    c.setBackground(PAPER);
                    c.setForeground(value instanceof Integer ? INK : LABEL_FG);
                }
                return c;
            }
        });
    }

    // --- odds and ends -----------------------------------------------------

    // EFFECTS: returns the large heading naming a page
    public static JLabel createPageHeading(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(DISPLAY_FONT.deriveFont(30f));
        label.setForeground(INK);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return label;
    }

    // EFFECTS: returns the small bold heading shown above a table
    public static JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(SECTION_FONT);
        label.setForeground(INK);
        return label;
    }

    // EFFECTS: returns the empty grey line that sits under a table and gets
    //          filled in with counts and totals
    public static JLabel createSummaryLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(SMALL_FONT);
        label.setForeground(MUTED_FG);
        label.setBorder(BorderFactory.createEmptyBorder(10, 2, 8, 0));
        return label;
    }

    // EFFECTS: returns an empty panel styled as a bar for holding buttons,
    //          with a divider line along its top edge
    public static JPanel createButtonBar() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return panel;
    }
}
