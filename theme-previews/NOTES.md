# Interface design

**Built.** The palette, layout and type below are what the app uses. The
mock-ups are kept as a record of how it was arrived at.

## Palette

| Role | Hex | Where |
|---|---|---|
| Base | `#021DAE` | nav column, chart bars |
| Accent | `#B5F800` | title, selected nav item, primary buttons, selected row |
| Pizzazz | `#FF2D95` | goals-completed figure, Exit on hover |
| Ink | `#0A1033` | body text, dark buttons, page headings |
| Paper | `#FFFFFF` | pages and tables |

All of it is in `UiTheme`. `#012AD6` is one step brighter and still a
reasonable alternative; changing `UiTheme.BASE` is the whole job. Compare them
in `BLUE-ladder.png`.

## Layout

A blue column down the left holds the title, the three pages, and the way out.
It is labels over a `CardLayout`, not a `JTabbedPane`: a tabbed pane is drawn
by the operating system and ignores the theme, and a side column lets the blue
be a block of colour rather than a stripe.

- **Home** is one quote filling the page, with the running totals in the top
  right corner
- **Sessions** is the table, then the monthly chart and totals, then buttons
- **Goals** is the table, the completed-only filter, then buttons

There is no separate stats window any more; the chart lives on the Sessions
page.

## The home screen

The quote, the two colours and the typeface are all picked fresh each time the
app opens, so it reads as a different poster every launch.

The colour pairings are a chosen list in `UiTheme`, not any two of the palette
at random: lime on white and white on lime disappear, pink on blue vibrates,
blue on ink is mud. Twelve pairings that work give the variety without the
days where the quote cannot be read.

Faces rotate between Impact, Arial Black, Haettenschweiler and Franklin Gothic
Heavy, each falling back to whatever is installed. The totals take the quote's
own foreground colour so the page stays two colours however it was painted.

The quotes themselves are a constant list in `model/Quote.java` — they ship
with the app like the palette does, so there is no table to seed and no way to
end up with none. Tests cover that none are blank, duplicated, or too long to
set at 64pt.

## Known limits

- the native title bar cannot be themed without drawing custom window chrome
- no rounded corners or shadows in Swing without hand-painting every component
- the nav is 210px of 1000, about a fifth, permanently; narrow it if the Notes
  column feels tight
- the chart sits in empty space until there are a few months of sessions

## The images

| File | What it is |
|---|---|
| `AFTER-home-blue.png`, `AFTER-home-white.png` | the home screen on two consecutive launches |
| `AFTER-sessions.png`, `AFTER-goals.png` | the two log pages as built |
| `MOCKUP-quote-*.png`, `MOCKUP-sessions.png` | the mock-ups this was built from |
| `BLUE-ladder.png` | five blues from navy to electric, for settling the shade |
| `REDESIGN-blue.png`, `REDESIGN-navy.png` | the earlier top-banner layout, before the side nav |
| `A-night-field.png`, `B-electric.png`, `C-sunlight.png` | the first colour-only swaps on the original layout |
