# Ultimate Frisbee Training Tracker

A desktop application in **Java** and **Swing** that helps ultimate frisbee players log their
training, set goals, and see their progress over time. Training sessions and goals are saved to
and loaded from a JSON file, so the data survives between runs.

## Project Description

The **Ultimate Frisbee Training Tracker** is a desktop application designed to help players track
their training progress, set and monitor goals, and log game performance. It allows users to:

- Record training sessions with details like date, duration, drills practiced, and personal notes
- Set fitness/skill goals with target completion dates
- Log reflection/journal entries and/or notes for improvement
- View their training history and monitor progress over time

## Who will use it?

The program is designed for *ultimate frisbee players* who want to take their training more
seriously and visualize/look back on their improvement over time. Whether they're a competitive or
recreational player, the application will help them stay organized and motivated.

## Why this project?

As an active ultimate player myself who started not long ago, I've struggled to practice
consistently and remember advice or what I learned from practices. This project interests me
because it helps solve a real, personal problem, and it combines my passion for ultimate with my
interest in software development.

## User Stories

- As a user, I want to be able to add a training session to my training log with date, duration,
  drills & skills practiced, and notes
- As a user, I want to be able to remove a training session from a training log
- As a user, I want to be able to view all my training sessions in order they were added to the
  training log
- As a user, I want to be able to view my total training hours
- As a user, I want to be able to add a goal to my goal tracker with a description and target
  completion date
- As a user, I want to be able to mark a goal as completed
- As a user, I want to be able to view all my goals in my goal log
- As a user, I want to be able to view all my completed goals
- As a user, I want to be able to save my training log and goal log to file (if I choose to)
- As a user, I want to be able to load my training log and goal log from file (if I choose to)

## How to Run

The project has no build tool; it compiles with plain `javac` against the jars in `lib/`.

```
javac -d bin -cp "lib/*" $(find src -name "*.java")
java -cp "bin;lib/*" ui.Main
```

To run the tests (JUnit 5, via the console launcher in `lib/`). Note that this launcher does not
accept a `lib/*` wildcard the way `javac` does, so the jars are listed out:

```
java -jar lib/junit-platform-console-standalone-1.10.2.jar execute --scan-classpath \
  -cp "bin;lib/json-20250517.jar;lib/JacocoExcludeFromReportAnnotation.jar"
```

All 76 tests should pass. The commands above use Windows classpath separators (`;`); on macOS or
Linux use `:` instead.

In VS Code, the Java extension picks the project up from `.vscode/settings.json` and `ui.Main` can
be run directly.

## Instructions for End User

The main window has two tabs, and a **Save / Load / Exit** bar along the bottom that applies to
both of them.

**Sessions tab**

- You can view the panel displaying training sessions by looking at the table in the **Sessions**
  tab, which opens first.
- You can see your **total training hours** on the summary line directly below that table.
- You can add a training session by clicking **Add Session** and filling in the form.
- You can remove a training session by clicking a row to select it, then clicking
  **Remove Selected**.
- You can locate the visual component (a bar chart of how long each session lasted) by clicking
  **View Stats**, which opens the Training Stats window. That window also reports your session
  count, total hours, average session length, and longest session.

**Goals tab**

- You can view all of your goals by switching to the **Goals** tab. Each goal shows its target date
  and its status: *In progress*, *Overdue*, or *Completed*.
- You can add a goal by clicking **Add Goal** and filling in the form.
- You can mark a goal as completed by selecting its row and clicking **Mark Completed**.
- You can view only your completed goals by ticking the **Show completed only** checkbox.
- You can remove a goal by selecting its row and clicking **Remove Selected**.

**Saving and loading**

- You can save the state of the application at any time by clicking **Save**.
- You can reload the state of the application by clicking **Load**, or by clicking **Yes** when the
  load prompt appears on startup.
- You can quit by clicking **Exit** or the window's close button; either one offers to save your
  data first. If the save fails the application stays open, so your data is not lost.

## Design Notes

The two logs share a parent class. `TrainingLog` and `GoalLog` do almost exactly the same job: hold
a list, add to it, remove from it, record an event describing the change, and write the list out as
JSON. All of that lives once in the abstract class `Log<T extends Writable>`. Each subclass only
supplies the parts that genuinely differ: the key its items are stored under in JSON, its name in
event messages, and how to describe one of its items in words. This is the **template method**
pattern — the parent owns the steps, the subclasses fill in the blanks.

`Log.getAll()` hands back an unmodifiable view of the list rather than the list itself, so callers
cannot sneak items in or out without going through `add` and `remove`, which is what keeps the
event log honest.

Dates use `java.time.LocalDate` from the standard library. Sessions and goals store their dates as
ISO-8601 strings such as `"2026-12-31"` in the save file.

`TrainingSession` is immutable: a logged session is a record of something that already happened, so
every one of its fields is `final`. `Goal` is immutable too, apart from whether it has been
completed, because that is the only thing about a goal that genuinely changes over time.

## Phase 4: Task 2

A sample of the event log printed to the console on exit:

```
Fri Mar 27 14:27:01 PDT 2026
80 min training session on 3/2/2026 added to Training Log.

Fri Mar 27 14:27:19 PDT 2026
20 min training session on 4/2/2024 added to Training Log.

Fri Mar 27 14:27:25 PDT 2026
80 min training session on 3/2/2026 removed from Training Log.
```

The application also logs goal changes and file access, so a longer session produces events like:

```
Loaded 1 training session(s) and 0 goal(s) from file.
Goal "Master huck" added to Goal Log.
Goal "Master huck" marked as completed.
Saved 2 training session(s) and 1 goal(s) to file.
```

Loading from file deliberately logs a single summary event rather than one event per restored
session, because reloading saved data is not the same kind of user action as logging a new session.

## Phase 4: Task 3

Two refactorings I identified, and then carried out.

**1. The two log classes were nearly identical.** `TrainingLog` and `GoalLog` both stored a list,
allowed items to be added and removed, and converted that data to JSON, with the same code written
out twice. I pulled the shared behaviour up into an abstract `Log<T extends Writable>` parent class.
Each subclass is now only the handful of lines that are actually specific to it, and a change to
how logs work — for example making `remove` report whether it removed anything — gets made in one
place instead of two.

**2. The custom `Date` class was reinventing the standard library, badly.** The old `Date` class
held a day, a month, and a year as plain integers with no validation, so nothing stopped a date
like month 23 from being created — and one of my own tests was constructing exactly that without
failing. It also had no way to compare two dates. Replacing it with `java.time.LocalDate` deleted
a whole class, made invalid dates impossible to construct (the app now shows "That date does not
exist" instead of silently accepting them), and made date comparison a one-liner. That immediately
paid for itself: `Goal.isOverdue()` is now simply
`!completed && targetDate.isBefore(LocalDate.now())`, which is what drives the *Overdue* status in
the Goals tab. Writing that against the old class would have meant hand-comparing three integers
and getting leap years right myself.

**What I would do next.** The two log tables in the UI are built and refreshed in much the same way
in `SessionsPanel` and `GoalsPanel`. The shared styling already lives in `UiTheme`, but the
"rebuild every row from the model, then update a summary line" logic is still written twice. A
generic table panel parameterised by how to turn one item into a row would remove that last piece
of duplication.

## Attribution

- `Event` and `EventLog` are taken from the course's AlarmSystem demo.
- `JsonReader`, `JsonWriter`, and `Writable` follow the course's
  [JsonSerializationDemo](https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo).
- The Swing window structure follows the AlarmSystem demo.
