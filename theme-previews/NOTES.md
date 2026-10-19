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

The quote and the two colours are picked fresh each time the app opens. The
typeface and the letter spacing do not vary: every saying is set the same way
and every fact is set the same way, so only the colour tells one opening from
the next. Rotating the face as well made the same screen look like a different
app each launch, because Impact is ultra-condensed and Arial Black is wide.

Six colour pairings, all of them deliberate:

| Ground | Type |
|---|---|
| pink | lime |
| lime | pink |
| blue | lime |
| lime | blue |
| blue | white |
| lime | near-black |

Sayings are shouted in capitals at 64pt and tracked slightly tight, the way
condensed display type is meant to be set. Facts are left in sentence case at
46pt at natural spacing, because shouting a date or a measurement reads as odd
rather than emphatic, and a fact runs longer so it needs the smaller size.

The quotes themselves are a constant list in `model/Quote.java`, built with
`Quote.saying(...)` or `Quote.fact(...)` so which kind it is lives with the
line. They ship with the app like the palette does, so there is no table to
seed and no way to end up with none. Tests cover that none are blank,
duplicated, or too long to set, and that the list holds some of each kind.

## Known limits

- the native title bar cannot be themed without drawing custom window chrome
- no rounded corners or shadows in Swing without hand-painting every component
- the nav is 210px of 1000, about a fifth, permanently; narrow it if the Notes
  column feels tight
- the chart sits in empty space until there are a few months of sessions

## The images

| File | What it is |
|---|---|
| `AFTER-home-*.png` | the home screen in several of its colour pairings, and with a fact rather than a saying |
| `AFTER-sessions.png`, `AFTER-goals.png` | the two log pages as built |
| `MOCKUP-quote-*.png`, `MOCKUP-sessions.png` | the mock-ups this was built from |
| `BLUE-ladder.png` | five blues from navy to electric, for settling the shade |
| `REDESIGN-blue.png`, `REDESIGN-navy.png` | the earlier top-banner layout, before the side nav |
| `A-night-field.png`, `B-electric.png`, `C-sunlight.png` | the first colour-only swaps on the original layout |
