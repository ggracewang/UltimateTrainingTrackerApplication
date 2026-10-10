# GUI redesign — decisions so far

Parked until the SQLite migration (plan steps 9-12) is finished.

## Palette — decided for now

| Role | Hex | Notes |
|---|---|---|
| Base / banner | `#021DAE` | "MID" on the ladder, between the navy and the electric blue |
| Accent | `#B5F800` | lime: buttons, title text, selected row |
| Pizzazz | `#FF2D95` | pink: used sparingly and only where it *means* something (goals completed, overdue status) |
| Ink | `#0A1033` | body text, secondary buttons |
| Paper | `#FFFFFF` | table background |

Still open: `#012AD6` ("B-") is one step brighter and also fine. Compare them
again in `BLUE-ladder.png` before committing to the final look.

## Type

- Headings / big numbers: **Impact** (installed on Windows and macOS). Closest
  free match for other platforms is Anton (SIL Open Font Licence) — bundle the
  `.ttf` and load it with `Font.createFont` if portability matters.
- Body and labels: Segoe UI Bold / Plain.

## Layout direction

See `REDESIGN-blue.png` and `REDESIGN-navy.png`. Both are real Swing windows,
so everything in them is buildable:

- full-bleed colour banner with an oversized Impact title
- live stat tiles in the banner (hours, sessions, goals completed)
- flat blocky tabs replacing the native JTabbedPane strip
- 42px table rows, no grid lines, durations set in Impact
- square buttons: lime primary, grey secondary, ink tertiary

## Known Swing limits

- no rounded corners or shadows without hand-painting every component
- the native title bar cannot be themed without going undecorated
- Impact is not present on Linux; bundle a font file if that matters

## Why this is parked

The stat tiles need `getCompletedGoals()` from the database, which is step 9 of
the SQL plan and does not exist yet. Step 12 also deletes the Save/Load bar,
which is why the mock-ups have no bottom bar. Doing the redesign first would
mean doing parts of it twice.

## The images

| File | What it is |
|---|---|
| `BLUE-ladder.png` | five blues from navy to electric, with the title and stats on each |
| `REDESIGN-blue.png` | full redesign mock-up, electric blue base |
| `REDESIGN-navy.png` | same redesign, deep navy base |
| `A-night-field.png` | palette-only swap on the current layout, navy |
| `B-electric.png` | palette-only swap, electric blue |
| `C-sunlight.png` | palette-only swap, lime header |
