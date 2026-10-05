-- Your SQL scratchpad. Edit this file, run sqlplay.SqlConsole, see the results.
-- Everything here runs top to bottom. Comment lines out with -- as you go.

-- 1. Look at the data. "*" means every column.
SELECT * FROM sessions;

-- 2. Pick just the columns you care about.
SELECT date, duration FROM sessions;

-- 3. WHERE keeps only the rows matching a condition.
SELECT date, duration FROM sessions WHERE duration > 90;

-- 4. ORDER BY sorts. DESC = biggest first. LIMIT stops after n rows.
SELECT date, duration, skills FROM sessions ORDER BY duration DESC LIMIT 5;
