package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

// Holds the colours and fonts shared by every window, plus the few pieces of
// furniture that more than one window needs to build (the dark header bar, a
// styled table, a row of buttons).
//
// Before this class existed the same colour values were typed out separately in
// each window class, so changing the look of the app meant editing it in
// several places and hoping they stayed in step.
@ExcludeFromJacocoGeneratedReport
public final class UiTheme {

    public static final Color HEADER_BG = new Color(44, 62, 80);
    public static final Color PANEL_BG = new Color(248, 249, 252);
    public static final Color BORDER = new Color(218, 220, 228);
    public static final Color ROW_ALT = new Color(248, 249, 252);
    public static final Color SUBTITLE_FG = new Color(143, 168, 188);
    public static final Color LABEL_FG = new Color(70, 70, 70);
    public static final Color MUTED_FG = new Color(120, 120, 120);
    public static final Color SELECTION_BG = new Color(219, 234, 254);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font SECTION_FONT = new Font("SansSerif", Font.BOLD, 13);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    private static final int ROW_HEIGHT = 26;

    // EFFECTS: prevents this utility class from being instantiated
    private UiTheme() {
    }

    // EFFECTS: returns the dark header bar shown at the top of a window, with
    //          the given title stacked above the given subtitle
    public static JPanel createHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(SUBTITLE_FG);

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setOpaque(false);
        textStack.add(titleLabel);
        textStack.add(Box.createVerticalStrut(3));
        textStack.add(subtitleLabel);

        panel.add(textStack, BorderLayout.WEST);
        return panel;
    }

    // EFFECTS: returns a styled, single-selection table showing the given model,
    //          with alternating row colours and a light header row
    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(ROW_HEIGHT);
        table.setFont(BODY_FONT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(Color.DARK_GRAY);
        styleTableHeader(table);
        addRowRenderer(table);
        return table;
    }

    // MODIFIES: table
    // EFFECTS: applies the shared font, background, and bottom border to the
    //          column header row of the given table
    private static void styleTableHeader(JTable table) {
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(PANEL_BG);
        table.getTableHeader().setForeground(new Color(90, 90, 90));
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
    }

    // MODIFIES: table
    // EFFECTS: attaches a cell renderer that paints alternating row backgrounds
    private static void addRowRenderer(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                }
                return c;
            }
        });
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

    // EFFECTS: returns a bold heading label for naming a section of a window
    public static JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SECTION_FONT);
        label.setForeground(LABEL_FG);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return label;
    }

    // EFFECTS: returns a small grey label used for summary lines under a table
    public static JLabel createSummaryLabel() {
        JLabel label = new JLabel();
        label.setFont(SMALL_FONT);
        label.setForeground(MUTED_FG);
        label.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        return label;
    }
}
