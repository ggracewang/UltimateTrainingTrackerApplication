# Ultimate Frisbee Training Tracker

A desktop application in **Java** and **Swing** that helps ultimate frisbee players log their
training, set goals, and see their progress over time. Everything is stored in a **SQLite**
database and written the moment it changes, so nothing is ever lost and there is nothing to save.

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
- As a user, I want my sessions and goals to still be there the next time I open the app, without
  having to remember to save them

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
  -cp "bin;lib/sqlite-jdbc-3.41.2.2.jar;lib/JacocoExcludeFromReportAnnotation.jar"
```

All 74 tests should pass. The commands above use Windows classpath separators (`;`); on macOS or
Linux use `:` instead.

In VS Code, the Java extension picks the project up from `.vscode/settings.json` and `ui.Main` can
be run directly.

The database lives at `data/tracker.db` and is created automatically the first time the app runs,
so a fresh clone needs no setup.

## Instructions for End User

The main window has two tabs. There is no Save button: every change is written to the database as
it is made.

**Sessions tab**

- You can view the panel displaying training sessions by looking at the table in the **Sessions**
  tab, which opens first.
- You can see your **total training hours** on the summary line directly below that table.
- You can add a training session by clicking **Add Session** and filling in the form.
- You can remove a training session by clicking a row to select it, then clicking
  **Remove Selected**.
- You can locate the visual component (a bar chart of how many minutes you trained in each month)
  by clicking **View Stats**, which opens the Training Stats window. That window also reports your
  session count, total hours, average session length, and longest session.

**Goals tab**

- You can view all of your goals by switching to the **Goals** tab. Each goal shows its target date
  and its status: *In progress*, *Overdue*, or *Completed*.
- You can add a goal by clicking **Add Goal** and filling in the form. Goal titles must be unique;
  reusing one is refused with a message rather than an error.
- You can mark a goal as completed by selecting its row and clicking **Mark Completed**.
- You can view only your completed goals by ticking the **Show completed only** checkbox.
- You can remove a goal by selecting its row and clicking **Remove Selected**.

**Closing**

- You can quit by clicking **Exit** or the window's close button. Neither asks whether to save,
  because there is never unsaved work: closing the app cannot lose anything.

## Design Notes

**Everything goes through the database.** `TrackerDatabase` owns the one connection and every
statement the app runs. Each tab writes a change and then reloads its table from the database, so
what is on screen is always what is actually stored rather than a copy that might have drifted.

**The database does the arithmetic.** Totals, averages and the monthly chart are SQL queries, not
Java loops. `getSessionStats()` returns the session count, total, average and longest from a single
query, because the database can keep four running answers while reading the rows once.
`getMinutesByMonth()` groups the sessions with `strftime('%Y-%m', date)` and sums each group, so a
year of sessions comes back as twelve numbers instead of hundreds of rows.

**Every user-typed value is bound to a `?` placeholder**, never glued into a SQL string. A note
containing an apostrophe is then stored as text rather than breaking the statement, and a note
containing SQL cannot be run as SQL. There is a test that stores
`Sam's drill; DROP TABLE training_sessions;--` and checks it comes back intact.

**Model objects carry their row id.** `TrainingSession` and `Goal` hold the `id` the database gave
them, which is how a selected table row is turned back into the right `UPDATE` or `DELETE`. A
brand-new object has `NO_ID` until it is saved. Because `TrainingSession` is immutable, saving one
returns a *new* copy carrying the id rather than changing the original.

**`BarChartPanel` knows nothing about training.** It takes a list of labels and a list of numbers,
so the same chart could show minutes per month, goals per month, or anything else counted up later.

Dates use `java.time.LocalDate` and are stored as ISO-8601 text such as `2026-12-31`. That format
sorts and compares correctly as plain text, which is why `WHERE date >= '2026-08-01'` works without
SQLite needing a dedicated date type.

`TrainingSession` is immutable: a logged session is a record of something that already happened, so
every one of its fields is `final`. `Goal` is immutable too, apart from whether it has been
completed, because that is the only thing about a goal that genuinely changes over time. Nothing in
the UI calls `Goal.markCompleted()` directly — completing a goal always goes through the database
by id, so an on-screen goal cannot be ticked off without the database hearing about it.

## Event log

A sample of the event log printed to the console on exit:

```
Fri Mar 27 14:27:01 PDT 2026
80 min training session on 3/2/2026 added to Training Log.

Fri Mar 27 14:27:19 PDT 2026
20 min training session on 4/2/2024 added to Training Log.

Fri Mar 27 14:27:25 PDT 2026
80 min training session on 3/2/2026 removed from Training Log.
```

Goal changes are logged the same way:

```
Goal "Master huck" added to Goal Log.
Goal "Master huck" marked as completed.
```

Reloading a table from the database logs nothing. Only things the user actually did are recorded,
so re-reading rows to redraw the screen does not fill the log with noise.

## History

This started as a CPSC 210 term project and has since been rebuilt as a personal one. Two things
changed substantially after the course ended.

**The custom `Date` class was reinventing the standard library, badly.** The old `Date` held a day,
a month and a year as plain integers with no validation, so nothing stopped a date like month 23
from being created — and one of my own tests was constructing exactly that without failing. It also
had no way to compare two dates. Replacing it with `java.time.LocalDate` deleted a whole class,
made invalid dates impossible to construct, and made comparison a one-liner. `Goal.isOverdue()` is
now simply `!completed && targetDate.isBefore(LocalDate.now())`.

**JSON files were replaced by a SQLite database.** The app used to hold everything in memory and
write it all out to `trainingtracker.json` when the user clicked Save, which meant forgetting to
click Save lost the work. Moving to SQLite removed the Save and Load buttons entirely, along with
`JsonReader`, `JsonWriter`, `Writable`, and the `Log<T>` / `TrainingLog` / `GoalLog` hierarchy that
existed to hold lists in memory and serialise them. The database holds the list now, so the classes
that used to do that had nothing left to do.

**What I would do next.** The two tables in the UI are built and refreshed in much the same way in
`SessionsPanel` and `GoalsPanel`. The shared styling already lives in `UiTheme`, but the "rebuild
every row, then update a summary line" logic is still written twice. A generic table panel
parameterised by how to turn one item into a row would remove that duplication.

## Attribution

- `Event` and `EventLog` are taken from the course's AlarmSystem demo.
- The Swing window structure follows the AlarmSystem demo.
- SQLite access uses the [Xerial sqlite-jdbc](https://github.com/xerial/sqlite-jdbc) driver.
