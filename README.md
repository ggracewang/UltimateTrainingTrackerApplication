# My Personal Project

## A subtitle

A *bulleted* list:
- item 1
- item 2
- item 3

An example of text with **bold** and *italic* fonts.  

# **Ultimate Frisbee Training Tracker**

## Project Description

The **Ultimate Frisbee Training Tracker** is a desktop application designed to help players track their training progress, set and monitor goals, and log game performance. It will allow users to:
- Record training sessions with details like date, duration, drills practiced, and personal notes
- Set fitness/skill goals with target completion dates
- Log reflection/journal entries and/or notes for improvement
- View their training history and monitor progress over time


## Who will use it?
The program is designed for *ultimate frisbee players* who want to take their training more seriously and visualize/look back on their improvement over time. Whether they're a competitive or recreation player, the application will help them stay organized and motivated. 


## Why this project?
As an active ultimate player myself who started not long ago, I've struggled to practice consistently and remember advice or what I learned from practices. This project interests me because it helps solve a real, personal problem, and it combines my passion for ultimate with my interest in software development.



## **USER STORIES**
- As a user, I want to be able to add a training session to my training log with date, duration, drills & skills practiced, and notes
- As a user, I want to be able to remove a training session from a training log
- As a user, I want to be able to view all my training sessions in order they were added to the training log
- As a user, I want to be able to view my total training hours
- As a user, I want to be able to add a goal to my goal tracker with a description and target completion date 
- As a user, I want to be able to mark a goal as completed
- As a user, I want to be able to view all my goals in my goal log
- As a user, I want to be able to view all my completed goals 
- As a user, I want to be able to save my training log and goal log to file (if I choose to)
- As a user, I want to be able to load my training log and goal log from file (if I choose to)


# Instructions for End User

- You can view the panel displaying training sessions by looking at the 
  table labelled "Sessions" in the main window.
- You can generate the first required action (add a session) by clicking the "Add Session" button at the bottom of the main window.
- You can generate the second required action (remove a session) by clicking a row to select it, then clicking "Remove Selected".
- You can locate the visual component (bar chart) by clicking "View Stats", which opens the Training Stats window.
- You can save the state of the application by clicking "Save", or by 
  clicking "Yes" when the save prompt appears on close.
- You can reload the state of the application by clicking "Load", or by 
  clicking "Yes" when the load prompt appears on startup.


# Phase 4: Task 2

Fri Mar 27 14:27:01 PDT 2026
80 min session on 3/2/2026 added to Training Log.

Fri Mar 27 14:27:19 PDT 2026
20 min session on 4/2/2024 added to Training Log.

Fri Mar 27 14:27:25 PDT 2026
80 min session on 3/2/2026 removed from Training Log.

# Phase 4: Task 3

Right now, my TrainingLog and GoalLog classes are almost identical in how they work. They both store a list of items, allow you to add or remove them, and convert that data into a JSON format. I would refactor these to use a single parent class (like an abstract Log class). This would remove a lot of repeated code and make the project much easier to maintain, as any changes to how logs are handled would only need to be made in one place.

I would also use Java's built in Date library instead. Currently, I have a custom Date class that manually handles the day, month, and year values, which can be prone to errors. If I were to continue this project, I would replace it with Java's standard LocalDate library. This would make the application more reliable because the standard library already handles complex logic like leap years and date comparisons. It would also make my code cleaner by allowing me to delete a custom class that essentially recreates a tool that already exists in Java.