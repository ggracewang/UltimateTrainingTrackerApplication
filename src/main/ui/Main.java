package ui;

import javax.swing.SwingUtilities;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

// Starts the Ultimate Training Tracker.
//
// Swing requires that windows be created on its own thread (the event dispatch
// thread) rather than on the thread that runs main, so the window is built
// inside SwingUtilities.invokeLater.
@ExcludeFromJacocoGeneratedReport
public class Main {

    // EFFECTS: opens the main application window
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrainingTrackerGUI::new);
    }
}
