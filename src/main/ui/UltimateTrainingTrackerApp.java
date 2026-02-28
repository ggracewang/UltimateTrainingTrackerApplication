package ui;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import persistence.JsonReader;
import persistence.JsonWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

// Referenced from JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

// Referenced from Lab 4: Flashcard Reviewer Application

public class UltimateTrainingTrackerApp {
    private GoalLog goalLog;
    private TrainingLog trainingLog;
    private Scanner scanner;
    private boolean isProgramRunning;
    private static final String JSON_STORE = "./data/trainingtracker.json";
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;

    public UltimateTrainingTrackerApp() {
        init();

        printDivider();
        System.out.println("Welcome to the Ultimate Training Tracker!");
        printDivider();

        while (this.isProgramRunning) {
            handleHomeMenu();
        }

    }

    // MODIFIES: this
    // EFFECTS: initializes the application with the starting values
    public void init() {
        this.goalLog = new GoalLog();
        this.trainingLog = new TrainingLog();
        this.scanner = new Scanner(System.in);
        this.isProgramRunning = true;
        this.jsonReader = new JsonReader(JSON_STORE);
        this.jsonWriter = new JsonWriter(JSON_STORE);
    }

    // MODIFIES: this
    // EFFECTS: displays and process inputs for the main/home menu
    public void handleHomeMenu() {
        displayHomeMenu();
        String input = this.scanner.nextLine();
        processHomeMenuCommands(input);
    }

    // MODIFIES: this
    // EFFECTS: displays and process inputs for the goal log menu
    public void handleGoalLogMenu() {
        displayGoalLogMenu();
        String input = this.scanner.nextLine();
        processGoalLogMenuCommands(input);
    }

    // MODIFIES: this
    // EFFECTS: displays and process inputs for the training log menu
    public void handleTrainingLogMenu() {
        displayTrainingLogMenu();
        String input = this.scanner.nextLine();
        processTrainingLogMenuCommands(input);
    }

    // EFFECTS: displays list of commands that can be used in main menu
    public void displayHomeMenu() {
        System.out.println("\nHOME MENU");
        System.out.println("\nPlease select an option below:\n");
        System.out.println("T: Manage Training Log");
        System.out.println("G: Manage Goal Log");
        System.out.println("S: Save data to file"); 
        System.out.println("L: Load data from file"); 
        System.out.println("X: Exit the application");
        printDivider();

    }


    // EFFECTS: displays list of commands that can be used in main menu
    public void displayGoalLogMenu() {
        System.out.println("Manage Training Log:");
        System.out.println("\nPlease select an option below.\n");
        System.out.println("a: Add a new goal");
        System.out.println("v: View all goals");
        System.out.println("c: View all completed goals");
        System.out.println("m: Mark a goal as completed");
        System.out.println("d: Delete a goal");

        System.out.println("b: Go back to Home menu");

        System.out.println("x: Exit the application");
        printDivider();
    }

    // EFFECTS: displays list of commands that can be used in main menu
    public void displayTrainingLogMenu() {
        System.out.println("\nManage Training Log:\n");
        System.out.println("\nPlease select an option below.\n");
        System.out.println("a: Add a training session");
        System.out.println("v: View all training sessions");
        System.out.println("r: Remove a training session");
        System.out.println("t: View total training time");

        System.out.println("b: Go back to Home menu");

        System.out.println("x: Exit the application");
        printDivider();
    }

    // EFFECTS: processes the user's input in the main menu
    public void processHomeMenuCommands(String input) { 
        printDivider();
        switch (input) {
            case "T":
                handleTrainingLogMenu();
                break;
            case "G":
                handleGoalLogMenu();
                break;
            case "S":
                saveData();
                break;
            case "L":
                loadData();
                break;
            case "X":
                quitApplication();
                break;
            default: 
                System.out.println("Invalid option inputted. Please try again.");
            
        }
        printDivider();
    }

    // EFFECTS: processes the user's input in the training log menu
    public void processTrainingLogMenuCommands(String input) { 
        printDivider();
        switch (input) {
            case "a": addTrainingSession();
                break;
            case "v": viewAllTrainingSessions();
                break;
            case "r": removeTrainingSession();
                break;
            case "t": viewTotalTrainingTime();
                break;
            case "b": handleHomeMenu();
                break;
            case "x": quitApplication();
                break;
            default: 
                System.out.println("Invalid option inputted. Please try again.");    
        }  
    }

    // EFFECTS: processes the user's input in the goal log menu
    public void processGoalLogMenuCommands(String input) { 
        printDivider();
        switch (input) {
            case "a": addNewGoal();
                break;
            case "v": viewAllGoals();
                break;
            case "c": viewCompletedGoals();
                break;
            case "m": markGoalAsCompleted();
                break;
            case "d": deleteGoal();
                break;
            case "b": handleHomeMenu();
                break;
            case "x": quitApplication();
                break;
            default: 
                System.out.println("Invalid option inputted. Please try again.");            
        }
        
    }

    // REQUIRES: the characters inputted for the day, month, year must be numbers(specifically integers)
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
                displayGoalDetails(goalLog.getAllGoals().get(i), i + 1);
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
                displayCompletedGoalDetails(completedGoals.get(i), i + 1);
            }
        }
    }

    // displays the details of a given goal
    public void displayGoalDetails(Goal goal, int num) {
        System.out.println("Goal #" + num);
        System.out.println("   Title: " + goal.getTitle());
        System.out.println("   Description: " + goal.getDescription());
        System.out.println("   Target Date: " + goal.getCompletionDate().getFullDateInStringFormat());
        System.out.println("   Completion Status: " + printCompletionStatus(goal));
        System.out.println();
    }


    // displays the details of a given goal
    public void displayCompletedGoalDetails(Goal goal, int num) {
        System.out.println("Goal #" + num);
        System.out.println("   Title: " + goal.getTitle());
        System.out.println("   Description: " + goal.getDescription());
        System.out.println("   Target Date: " + goal.getCompletionDate().getFullDateInStringFormat());
        System.out.println("   Completion Status: " + printCompletionStatus(goal));
        System.out.println();
    }

    // EFFECTS: prints incomplete/complete if completion status is true/false
    public String printCompletionStatus(Goal goal) {
        String status = "Incomplete";
        if (goal.getCompletionStatus() == true) {
            status = "Complete";
        }
        return status;
    }

    //MODIFIES: this
    //EFFECTS: marks a goal as complete
    public void markGoalAsCompleted() {
        List<Goal> goals = goalLog.getAllGoals();

        if (goals.isEmpty() || getUncompletedGoals().isEmpty()) {
            System.out.println("No goals to mark complete. Try adding a goal first!");
        } else {
            System.out.println("Available goals:");
            displayUncompletedGoalTitles();
            System.out.println("\nPlease enter the title of the goal you completed: ");
            String title = this.scanner.nextLine();

            boolean found = false;
            for (Goal g: goals) {
                if (g.getTitle().equals(title)) {
                    g.markCompleted();
                    System.out.println("\nCONGRATS! This goal was completed on " 
                            + getDateCompleted().getFullDateInStringFormat() + ".");
                    found = true;
                    break;
                }
            }
            if (found == false) {
                printDivider();
                System.out.println("Error: Goal not found. Please check the title and try again.");
            }
        }
    }
    
    // REQUIRES: the characters inputted for the day, month, year must be numbers
    // EFFECTS: gets the day, month, year user completed a goal, returns the date
    public Date getDateCompleted() {
        System.out.println("ENTER THE DAY YOU COMPLETED THIS GOAL (1-31): ");
        int day = Integer.parseInt(this.scanner.nextLine());

        System.out.println("ENTER THE MONTH YOU COMPLETED THIS GOAL (1-12): ");
        int month = Integer.parseInt(this.scanner.nextLine());

        System.out.println("ENTER THE YEAR YOUR COMPLETED THIS GOAL: ");
        int year = Integer.parseInt(this.scanner.nextLine());

        Date dateCompleted = new Date(day, month, year);
        return dateCompleted;

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

    // EFFECTS: displays the titles of uncomplete goals
    public void displayUncompletedGoalTitles() {
        List<Goal> goals = goalLog.getAllGoals();
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getCompletionStatus() == false) {
                System.out.println("   " + (i + 1) + ". " + goals.get(i).getTitle());
            }
            
        }

    }

    // EFFECTS: returns list of uncompleted goals
    public List<Goal> getUncompletedGoals() {
        List<Goal> goals = goalLog.getAllGoals();
        List<Goal> completedGoals = new ArrayList<>();
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getCompletionStatus() == false) {
                completedGoals.add(goals.get(i));
            }
            
        }
        return completedGoals;
    }

    // REQUIRES: the characters inputted for the day, month, year must be numbers
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
                displaySessionDetails(sessions.get(i), i + 1);
            }
        }
    }


    // EFFECTS: displays the details of a given training session
    public void displaySessionDetails(TrainingSession session, int num) {
        System.out.println("Session #" + num);
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
        System.out.println("------------------------------------------------------------------");
    }

    // EFFECTS: saves the training log and goal log to file
    private void saveData() {
        try {
            jsonWriter.open();
            jsonWriter.write(trainingLog, goalLog);
            jsonWriter.close();
            System.out.println("Saved training log and goal log to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads training log and goal log from file
    private void loadData() {
        try {
            trainingLog = jsonReader.readTrainingLog();
            goalLog = jsonReader.readGoalLog();
            System.out.println("Loaded training log and goal log from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

}
