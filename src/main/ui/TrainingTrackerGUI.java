package ui;

import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// Referenced from AlarmSystem demo 

public class TrainingTrackerGUI extends JFrame {
    private static final int WIDTH  = 820;
    private static final int HEIGHT = 520;
    private static final String JSON_STORE = "./data/trainingtracker.json";

    // Colour palette:
    private static final Color COLOUR_HEADER  = new Color(44, 62, 80);
    private static final Color COLOUR_ACCENT  = new Color(41, 128, 185);
    private static final Color COLOUR_PANEL   = new Color(248, 249, 252);
    private static final Color COLOUR_BORDER  = new Color(218, 220, 228);
    private static final Color COLOUR_ROW_ALT = new Color(248, 249, 252);

    private TrainingLog trainingLog;
    private GoalLog goalLog;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;

    private DefaultTableModel tableModel;
    private JTable table;


    // EFFECTS: initialises model objects, builds and displays the window,
    //          attaches a window-close listener, then prompts the user to
    //          load saved data
    public TrainingTrackerGUI() {
        super("Ultimate Training Tracker");
 
        trainingLog = new TrainingLog();
        goalLog     = new GoalLog();
        jsonReader  = new JsonReader(JSON_STORE);
        jsonWriter  = new JsonWriter(JSON_STORE);
 
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
 
        addHeader();
        addTablePanel();
        addButtonPanel();
 

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new SaveAction().actionPerformed(null);
            }
        });
 
        centreOnScreen();
        setVisible(true);
        promptLoadOnStart();
    }
 
    // MODIFIES: this
    // EFFECTS: creates and adds the dark header panel to the NORTH region
    private void addHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOUR_HEADER);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
 
        JLabel title = new JLabel("Training Log");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
 
        JLabel subtitle = new JLabel("Ultimate Frisbee Training Tracker");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
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
    // EFFECTS: creates and adds the scrollable sessions table to the CENTER region
    private void addTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        panel.add(createSectionLabel(), BorderLayout.NORTH);
        panel.add(createScrollPane(),   BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
    }
 
    // EFFECTS: returns a styled "Sessions" heading label
    private JLabel createSectionLabel() {
        JLabel label = new JLabel("Sessions");
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(70, 70, 70));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return label;
    }
 
    // MODIFIES: this
    // EFFECTS: calls initTableModel() and initTable() to set up the table fields
    //          then returns a JScrollPane wrapping the table
    private JScrollPane createScrollPane() {
        initTableModel();
        initTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOUR_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
 
    // MODIFIES: this
    // EFFECTS: initialises tableModel with column headers and zero rows;
    //          all cells are set to non-editable
    private void initTableModel() {
        String[] columnNames = {"#", "Date", "Duration (min)", "Skills", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }
 
    // MODIFIES: this
    // EFFECTS: initialises and styles the JTable using tableModel,
    //          sets fonts, row height, selection colours, column widths
    private void initTable() {
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.DARK_GRAY);
        table.getColumnModel().getColumn(0).setPreferredWidth(28);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        styleTableHeader();
        addRowRenderer();
    }
 
    // MODIFIES: this
    // EFFECTS: applies font, background colour, and bottom border to the
    //          column header row of the table
    private void styleTableHeader() {
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(COLOUR_PANEL);
        table.getTableHeader().setForeground(new Color(90, 90, 90));
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOUR_BORDER));
    }
 
    // MODIFIES: this
    // EFFECTS: attaches a custom cell renderer that paints alternating row
    //          background colours (white and COLOUR_ROW_ALT)
    private void addRowRenderer() {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : COLOUR_ROW_ALT);
                }
                return c;
            }
        });
    }
 
    // MODIFIES: this
    // EFFECTS: creates and adds the button bar to SOUTH region
    //          - buttons created using AbstractAction inner classes
    private void addButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOUR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOUR_BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.add(new JButton(new AddSessionAction()));
        panel.add(new JButton(new RemoveSessionAction()));
        panel.add(new JButton(new ViewStatsAction()));
        panel.add(Box.createHorizontalStrut(24));
        panel.add(new JButton(new SaveAction()));
        panel.add(new JButton(new LoadAction()));
        add(panel, BorderLayout.SOUTH);
    }
 
    // MODIFIES: this
    // EFFECTS: positions window at the centre of the screen
    private void centreOnScreen() {
        int width  = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((width - getWidth()) / 2, (height - getHeight()) / 2);
    }
 
    // EFFECTS: shows a Yes/No popup asking whether to load saved data;
    //          if Yes, calls loadFromFile()
    private void promptLoadOnStart() {
        int choice = JOptionPane.showConfirmDialog(null,
                "Would you like to load your previously saved sessions?",
                "Load Data", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            loadFromFile();
        }
    }
 
    // MODIFIES: file at JSON_STORE
    // EFFECTS: writes trainingLog and goalLog to JSON file and shows confirmation message 
    private void saveToFile() {
        try {
            jsonWriter.open();
            jsonWriter.write(trainingLog, goalLog);
            jsonWriter.close();
            JOptionPane.showMessageDialog(null,
                    "Data saved successfully.", "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not save to " + JSON_STORE, "Save Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // MODIFIES: this
    // EFFECTS: reads trainingLog and goalLog from JSON file and refreshes
    //          the table- shows confirmation message for success or failure
    private void loadFromFile() {
        try {
            trainingLog = jsonReader.readTrainingLog();
            goalLog     = jsonReader.readGoalLog();
            refreshTable();
            JOptionPane.showMessageDialog(null,
                    "Data loaded successfully.", "Loaded",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not load from " + JSON_STORE, "Load Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // MODIFIES: tableModel
    // EFFECTS: clears all rows from the table and rebuilds them from
    //          the current state of trainingLog
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<TrainingSession> sessions = trainingLog.getTrainingLog();
        for (int i = 0; i < sessions.size(); i++) {
            TrainingSession s = sessions.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    s.getDate().getFullDateInStringFormat(),
                    s.getDuration(),
                    s.getSkills(),
                    s.getNotes()
            });
        }
    }
 
    /**
     * Represents the action to be taken when the user wants to add a new
     * training session to the log.
     */
    private class AddSessionAction extends AbstractAction {
 
        AddSessionAction() {
            super("Add Session");
        }
 
        // MODIFIES: trainingLog
        // EFFECTS: shows an input form: if OK is clicked and inputs valid,
        //          then creates a new TrainingSession and adds it to trainingLog,
        //          then calls refreshTable(); - shows an error message if any
        //          of the fields cannot be parsed as integers
        @Override
        public void actionPerformed(ActionEvent evt) {
            JTextField[] fields = createFormFields();
            JPanel form = buildFormPanel(fields);
            int result = JOptionPane.showConfirmDialog(null, form,
                    "Add Training Session",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                processSessionInput(fields);
            }
        }
 
        // EFFECTS: returns array of six JTextFields for day, month, year, duration, skills, notes
        private JTextField[] createFormFields() {
            return new JTextField[]{
                    new JTextField(),
                    new JTextField(),
                    new JTextField(),
                    new JTextField(),
                    new JTextField(),
                    new JTextField()
            };
        }
 
        // EFFECTS: returns a two-column form panel containing label-field pairs
        //          for each session attribute, using the given fields array
        private JPanel buildFormPanel(JTextField[] fields) {
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            form.add(new JLabel("Day (1-31):"));        
            form.add(fields[0]);
            form.add(new JLabel("Month (1-12):"));      
            form.add(fields[1]);
            form.add(new JLabel("Year:"));              
            form.add(fields[2]);
            form.add(new JLabel("Duration (min):"));    
            form.add(fields[3]);
            form.add(new JLabel("Skills practiced:"));  
            form.add(fields[4]);
            form.add(new JLabel("Notes (optional):"));  
            form.add(fields[5]);
            return form;
        }
 
        // REQUIRES: fields has length >= 6
        // MODIFIES: trainingLog
        // EFFECTS: parses fields[0..3] as integers and fields[4..5] as strings,
        //          creates a TrainingSession, adds it to trainingLog, and calls
        //          refreshTable(); shows an error message on NumberFormatException
        private void processSessionInput(JTextField[] fields) {
            try {
                int day      = Integer.parseInt(fields[0].getText().trim());
                int month    = Integer.parseInt(fields[1].getText().trim());
                int year     = Integer.parseInt(fields[2].getText().trim());
                int duration = Integer.parseInt(fields[3].getText().trim());
                String skills = fields[4].getText().trim();
                String notes  = fields[5].getText().trim();
                TrainingSession session = new TrainingSession();
                session.setDate(new Date(day, month, year));
                session.setDuration(duration);
                session.setSkills(skills);
                session.setNotes(notes);
                trainingLog.addSession(session);
                refreshTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Please enter whole numbers for Day, Month, Year, and Duration.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    /**
     * Represents the action to be taken when the user wants to remove the
     * currently selected training session from the log.
     */
    private class RemoveSessionAction extends AbstractAction {
 
        RemoveSessionAction() {
            super("Remove Selected");
        }
 
        // MODIFIES: trainingLog
        // EFFECTS: if no row is selected, shows a warning dialog and returns;
        //          otherwise shows a confirmation dialog and, if confirmed,
        //          removes the selected session from trainingLog and calls
        //          refreshTable()
        @Override
        public void actionPerformed(ActionEvent evt) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null,
                        "Click a session in the table to select it first.",
                        "No Session Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Delete session #" + (selectedRow + 1) + "? This cannot be undone.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                trainingLog.removeSession(
                        trainingLog.getTrainingLog().get(selectedRow));
                refreshTable();
            }
        }
    }
 
    /**
     * Represents the action to be taken when the user wants to view the
     * bar chart of session durations in a separate window.
     */
    private class ViewStatsAction extends AbstractAction {
 
        ViewStatsAction() {
            super("View Stats");
        }
 
        // EFFECTS: if trainingLog is empty, shows an information dialog;
        //          otherwise opens a new StatsWindow with the current sessions
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (trainingLog.getTrainingLog().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No sessions yet. Add some sessions first.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new StatsWindow(trainingLog.getTrainingLog());
        }
    }
 
    /**
     * represents the action to be taken when the user wants to save their
     * data and exit. Also triggered by the window-close listener with a
     * null ActionEvent which is safe because evt is never used here.
     */
    private class SaveAction extends AbstractAction {
 
        SaveAction() {
            super("Save");
        }
 
        // EFFECTS: shows a Yes/No/Cancel dialog;
        //          Yes — calls saveToFile() then exits the application;
        //          No  — exits without saving;
        //          Cancel — does nothing and leaves the window open
        @Override
        public void actionPerformed(ActionEvent evt) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Would you like to save your data before exiting?",
                    "Save and Exit", JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                saveToFile();
                System.exit(0);
            } else if (choice == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        }
    }
 
    /**
     * Represents the action to be taken when the user wants to load
     * previously saved data from file.
     */
    private class LoadAction extends AbstractAction {
 
        LoadAction() {
            super("Load");
        }
 
        // MODIFIES: trainingLog, goalLog
        // EFFECTS: calls loadFromFile()
        @Override
        public void actionPerformed(ActionEvent evt) {
            loadFromFile();
        }
    }
}   
