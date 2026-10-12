# GUI redesign

**Built.** The palette and layout below are what the app now uses. The mock-ups
are kept as a record of how the look was arrived at.

## Palette — in use

| Role | Hex | Where |
|---|---|---|
| Base | `#021DAE` | banner, chart bars |
| Accent | `#B5F800` | title, primary buttons, selected row, selected tab text |
| Pizzazz | `#FF2D95` | goals-completed counter only — pink is reserved for things that mean something |
| Ink | `#0A1033` | body text, dark buttons, selected tab |
| Paper | `#FFFFFF` | tables |

All of it lives in `UiTheme`. `#012AD6` ("B-" on the ladder) is one step
brighter and still a reasonable alternative — changing `UiTheme.BASE` is the
whole job. Compare them again in `BLUE-ladder.png`.

## Type

- Headings and figures: **Impact**. `UiTheme` checks what is installed and
  falls back Impact → Haettenschweiler → Arial Black → DejaVu Sans Bold →
  SansSerif, so the app still runs where Impact is missing (most Linux).
- Body and labels: Segoe UI.

## What was built

- full-bleed blue banner, oversized Impact title, three live totals on the
  right that refresh whenever either tab changes something
- flat tabs over a `CardLayout`, replacing `JTabbedPane`, which is drawn by the
  operating system and ignored the theme entirely
- buttons in three ranks: lime primary, pale secondary, dark tertiary, each
  lightening on hover
- 36px table rows, no grid lines, a heavy rule under the headings, a lime
  selection bar, and numeric columns set in Impact
- section headings dropped, because the tab above already names the screen

## Known limits

- the native title bar cannot be themed without drawing custom window chrome,
  including drag and resize; not worth it
- no rounded corners or shadows in Swing without hand-painting every component,
  which suits the flat poster look anyway

## The images

| File | What it is |
|---|---|
| `AFTER-sessions.png`, `AFTER-goals.png` | the app as built |
| `REDESIGN-blue.png`, `REDESIGN-navy.png` | the mock-ups this was built from |
| `BLUE-ladder.png` | five blues from navy to electric, for settling the shade |
| `A-night-field.png`, `B-electric.png`, `C-sunlight.png` | early colour-only swaps on the old layout |
