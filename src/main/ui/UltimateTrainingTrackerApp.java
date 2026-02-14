package ui;
import model.*;
import java.util.List;
import java.util.Scanner;

public class UltimateTrainingTrackerApp {
    private GoalLog goalLog;
    private TrainingLog trainingLog;
    private Scanner scanner;
    private boolean isProgramRunning;

    public UltimateTrainingTrackerApp() {
        init();

        printDivider();

        System.out.println("Welcome to the Ultimate Training Tracker!");
        printDivider();

        while (this.isProgramRunning) {
            handleMenu();
        }

    }

    // MODIFIES: this
    // EFFECTS: initializes the application with the starting values
    public void init() {
        this.goalLog = new GoalLog();
        this.trainingLog = new TrainingLog();
        this.scanner = new Scanner(System.in);
        this.isProgramRunning = true;
    }

    // MODIFIES: this
    // EFFECTS: displays and process inputs for the main menu
    public void handleMenu() {
        displayMenu();
        String input = this.scanner.nextLine();
        processMenuCommands(input);
    }

    // EFFECTS: displays list of commands that can be used in main menu
    public void displayMenu() {
        System.out.println("\nPlease select and option\n");
        System.out.println("Manage Training Log:");
        System.out.println("a: Add a new goal");
        System.out.println("v: View all goals");
        System.out.println("c: View all completed goals");
        System.out.println("m: Mark a goal as completed");
        System.out.println("d: Delete a goal");

        System.out.println("\nManage Goal Log:\n");
        System.out.println("s: Add a training session");
        System.out.println("l: View all training sessions");
        System.out.println("r: Remove a training session");
        System.out.println("t: View total training time");

        System.out.println("x: Exit the application");
        printDivider();
    }

    // EFFECTS: processes the user's input in the main menu
    public void processMenuCommands(String input) {
        printDivider();
        switch(input) {
            case "a":
                addNewGoal();
                break;
            case "v":
                viewAllGoals();
                break;
            case "c":
                viewCompletedGoals();
                break;
            case "m":
                markGoalAsCompleted();
                break;
            case "d":
                deleteGoal();
                break;
            case "s":
                addTrainingSession();
                break;
            case "l":
                viewAllTrainingSessions();
                break;
            case "r":
                removeTrainingSession();
                break;
            case "t":
                viewTotalTrainingTime();
                break;
            case "x":
                quitApplication();
                break;
            default: 
                System.out.println("Invalid option inputted. Please try again.");
            
        }
        printDivider();
    }

    //MODIFIES: this
    // EFFECTS: adds a new goal to the goal log
    public void addNewGoal() {
        System.out.println("ENTER GOAL TITLE: ");
        String title = this.scanner.nextLine();

        System.out.println("ENTER GOAL DESCRIPTION: ");
        String description = this.scanner.nextLine();

        System.out.println("ENTER GOAL TARGET COMPLETION DAY (1-31): ");
        int day = Integer.parseInt(this.scanner.nextLine());

        System.out.println("ENTER GOAL TARGET COMPLETION MONTH (1-12): ");
        int month = Integer.parseInt(this.scanner.nextLine());

        System.out.println("ENTER GOAL TARGET COMPLETION YEAR: ");
        int year = Integer.parseInt(this.scanner.nextLine());


        Date completionDate = new Date(day, month, year);
        Goal newGoal = new Goal(title, description, completionDate);
        this.goalLog.addGoal(newGoal);

        System.out.println("\nNew goal successfully added. ");


    }

    // EFFECTS: displays all goals in goal log
    public void viewAllGoals() {
        if (goalLog.getAllGoals().isEmpty()) {
            System.out.println("No goals to display. Try adding a goal first!");
        } else {
            System.out.println("ALL GOALS: ");
            System.out.println();
            for (int i = 0; i < goalLog.getAllGoals().size(); i++) {
                displayGoalDetails(goalLog.getAllGoals().get(i), i+1);
            }
        }
    }

    // EFFECTS: displays all completed goals
    public void viewCompletedGoals() {
        List<Goal> completedGoals = goalLog.getCompletedGoals();

        if (completedGoals.isEmpty()) {
            System.out.println("No completed goals yet. Keep going!");
        } else {
            System.out.println("COMPLETED GOALS: ");
            System.out.println();
            for (int i = 0; i < completedGoals.size(); i++) {
                displayGoalDetails(completedGoals.get(i), i+1);
            }
        }
    }

    // displays the details of a given goal
    public void displayGoalDetails(Goal goal, int num) {
        System.out.println("Goal #" + num);
        System.out.println("   Title: " + goal.getTitle());
        System.out.println("   Description: " + goal.getDescription());
        System.out.println("   Target Date: " + goal.getCompletionDate().getFullDateInStringFormat());
        System.out.println("   Status: " + goal.getCompletionStatus());
        System.out.println();
    }

    //MODIFIES: this
    //EFFECTS: marks a goal as complete
    public void markGoalAsCompleted() {
        List<Goal> goals = goalLog.getAllGoals();

        if (goals.isEmpty()) {
            System.out.println("No goals available. Try adding a goal first!");
        } else {
            System.out.println("Available goals:");
            displayGoalTitles();
            System.out.println("\nPlease enter the title of the goal you completed: ");
            String title = this.scanner.nextLine();

            boolean found = false;
            for (Goal g: goals) {
                if (g.getTitle().equals(title)) {
                    g.markCompleted();
                    System.out.println("CONGRATS! This goal is completed!");
                    found = true;
                    break;
                }
            }
            if (found == false) {
                System.out.println("Error: Goal not found. Please check the title and try again.");
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: deletes a goal from the goal log
    public void deleteGoal() {
        List<Goal> goals = goalLog.getAllGoals();

        if (goals.isEmpty()) {
            System.out.println("No goals to delete. Your goal log is empty.");
        } else {
            System.out.println("Current goals:");
            displayGoalTitles();
            System.out.println("\nPlease enter the title of the goal you want to delete: ");
            String title = this.scanner.nextLine();
            goalLog.removeGoal(title);
            System.out.println("\nThis goal has been deleted");
            
        }
    }

    // EFFECTS: displays the goal titles
    public void displayGoalTitles() {
        List<Goal> goals = goalLog.getAllGoals();
        for (int i = 0; i < goals.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + goals.get(i).getTitle());
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a new training session to the training log
    public void addTrainingSession() {
        TrainingSession session = new TrainingSession();

        System.out.println("ENTER THE TRAINING SESSION DAY (1-31): ");
        int day = Integer.parseInt(this.scanner.nextLine());

        System.out.println("ENTER THE TRAINING SESSION MONTH (1-12): ");
        int month = Integer.parseInt(this.scanner.nextLine());

        System.out.println("ENTER THE TRAINING SESSION YEAR: ");
        int year = Integer.parseInt(this.scanner.nextLine());

        Date sessionDate = new Date(day, month, year);
        session.setDate(sessionDate);

        System.out.println("ENTER TRAINING SESSION DURATION: ");
        int duration = Integer.parseInt(this.scanner.nextLine());
        session.setDuration(duration);

        System.out.println("ENTER SKILLS PRACTICED DURING THIS TRAINING SESSION: ");
        String skills = this.scanner.nextLine();
        session.setSkills(skills);

        System.out.println("ENTER ANY NOTES FOR THIS TRAINING SESSION (OR PRESS ENTER TO SKIP): ");
        String notes = this.scanner.nextLine();
        session.setNotes(notes);

        trainingLog.addSession(session);
        System.out.println("New training session successfully added. Nice work!");
        
    }


    // EFFECTS: displays all training sessions
    public void viewAllTrainingSessions() {
        List<TrainingSession> sessions = trainingLog.getTrainingLog();

        if (sessions.isEmpty()) {
            System.out.println("No training sessions to display. Try adding a session first!");
        } else {
            System.out.println("ALL TRAINING SESSIONS: ");
            System.out.println();
            for (int i = 0; i < sessions.size(); i++) {
                displaySessionDetails(sessions.get(i), i+1);
            }
        }
    }


    // EFFECTS: displays the details of a given training session
    public void displaySessionDetails(TrainingSession session, int num) {
        System.out.println("Session#" + num);
        System.out.println("   Date: " + session.getDate().getFullDateInStringFormat());
        System.out.println("   Duration: " + session.getDuration() + " minutes");
        System.out.println("   Skills Practiced: " + session.getSkills());
        System.out.println("   Notes: " + session.getNotes());
        System.out.println();
    }

    // MODIFIES: this
    // EFFECTS: removes a training session from the training log
    public void removeTrainingSession() {
        List<TrainingSession> sessions = trainingLog.getTrainingLog();

        if (sessions.isEmpty()) {
            System.out.println("Error: No training sessions to remove. Your log is empty.");
        } else {
            System.out.println("Your training sessions:");
            displayListOfSessions();
            System.out.println("\nPlease enter the number of the session you would like to delete");
            int index = Integer.parseInt(this.scanner.nextLine());

            if (index > 0 && index <= sessions.size()) {
                TrainingSession session = sessions.get(index - 1);
                trainingLog.removeSession(session);
                System.out.println("\nTraining session #" + index + " has been deleted");
            } else {
                System.out.println("\nError: Invalid session number. Please try again.");
            }
        }

    }


    // EFFECTS: displays a list of trianing sessions with numbers for selecting
    public void displayListOfSessions() {
        List<TrainingSession> sessions = trainingLog.getTrainingLog();
        for (int i = 0; i < sessions.size(); i++) {
            TrainingSession s = sessions.get(i);
            System.out.println(" " + (i + 1) + ". " + s.getDate().getFullDateInStringFormat() 
                                + " - " + s.getDuration() + " mins - " + s.getSkills());
        }
    }

    //EFFECTS: displays total training time statistics
    public void viewTotalTrainingTime() {
        int totalMinutes = trainingLog.getTotalDurationPracticed();
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        System.out.println("TOTAL TRAINING DURATION: ");
        System.out.println("   Total time: " + totalMinutes + " minutes");
        System.out.println("   That's " + hours + " hrs and " + minutes + " minutes!");

    }
    

    // MODIFIES: this
    // EFFECTS: prints closing message and marks program as not running
    public void quitApplication() {
        System.out.println("Thanks for using the Ultimate Training Tracker!");
        this.isProgramRunning = false;
    }

    // EFFECTS: prints a line of dashes to act like divider
    private void printDivider() {
        System.out.println("-----------------------------------------------");
    }

}
