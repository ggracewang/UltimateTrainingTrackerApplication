package model;

import java.util.Collections;
import java.util.List;
import java.util.Random;

// Represents one line shown on the home screen: either a fact about ultimate
// or something worth reading before training, with a short category above it.
//
// The two are set differently. A saying is shouted in capitals, the way the
// posters this design came from do it. A fact is left in ordinary sentence
// case, because shouting a date or a measurement reads as odd rather than
// emphatic.
//
// The collection is held here as a constant rather than in the database.
// These ship with the app the way the colours do; they are not the player's
// data, there is nothing to add or delete, and keeping them in code means
// there is no table to seed on first run and no way to end up with none.
public class Quote {

    private static final Random RANDOM = new Random();

    private static final List<Quote> ALL = List.of(
            saying("The disc has no memory of your last throw.", "Ultimate wisdom"),
            saying("Everyone gets a turn. That is the whole sport.", "On the force"),
            saying("There are no referees. Only people who agree.", "Spirit of the game"),
            saying("A layout you did not attempt is a layout you did not make.", "Hard-won advice"),
            saying("The wind is not against you. It is against everyone.", "Ultimate wisdom"),
            saying("Catch it first. Celebrate second.", "Hard-won advice"),
            saying("A bad throw to open space beats a good throw to a crowd.", "On the force"),
            saying("You cannot out-throw bad footwork.", "Hard-won advice"),
            saying("Nobody remembers the drop. They remember whether you ran back.",
                    "Spirit of the game"),
            saying("Flat is a feeling, not an angle.", "On the forehand"),
            saying("Every handler was once terrified of the forehand.", "On the forehand"),
            saying("You get better at hucking by throwing short ones properly.",
                    "Ultimate wisdom"),
            saying("The best defenders are just the ones still running.", "On the force"),
            saying("Call your own fouls. It is the only rule that matters.",
                    "Spirit of the game"),
            saying("Tired legs throw high. Watch for it.", "Hard-won advice"),
            saying("Consistency beats power. Power beats nothing on its own.",
                    "Ultimate wisdom"),
            saying("Show up when it rains. That is most of it.", "Hard-won advice"),

            fact("Ultimate was invented in 1968, in a high school car park in New Jersey.",
                    "Did you know"),
            fact("A regulation disc weighs 175 grams. Everything else is technique.",
                    "Did you know"),
            fact("The first intercollegiate game was Rutgers against Princeton, in 1972.",
                    "Did you know"),
            fact("A full field is 100 metres long: 64 of play and an 18 metre end zone at each end.",
                    "Did you know"),
            fact("Ultimate is self-officiated at almost every level. Players call their own fouls.",
                    "Did you know"));

    private final String text;
    private final String category;
    private final boolean isFact;

    // REQUIRES: text and category are not blank
    // EFFECTS: constructs a quote with the given text, category, and whether
    //          it is a fact rather than a saying
    private Quote(String text, String category, boolean isFact) {
        this.text = text;
        this.category = category;
        this.isFact = isFact;
    }

    // REQUIRES: text and category are not blank
    // EFFECTS: returns a saying, which is shown in capitals
    public static Quote saying(String text, String category) {
        return new Quote(text, category, false);
    }

    // REQUIRES: text and category are not blank
    // EFFECTS: returns a fact, which is shown in ordinary sentence case
    public static Quote fact(String text, String category) {
        return new Quote(text, category, true);
    }

    // EFFECTS: returns every quote the app knows, in a list that cannot be
    //          changed by the caller
    public static List<Quote> all() {
        return Collections.unmodifiableList(ALL);
    }

    // EFFECTS: returns one quote picked at random. With twenty-odd of them,
    //          seeing the same one twice in a row is rare enough not to look
    //          broken, so no effort is made to remember the last one shown.
    public static Quote random() {
        return ALL.get(RANDOM.nextInt(ALL.size()));
    }

    // EFFECTS: returns the line itself, as written
    public String getText() {
        return text;
    }

    // EFFECTS: returns the line the way it should appear on screen: a saying
    //          in capitals, a fact left as it was written
    public String getDisplayText() {
        return isFact ? text : text.toUpperCase();
    }

    // EFFECTS: returns the short heading shown above the line
    public String getCategory() {
        return category;
    }

    // EFFECTS: returns true if this is a fact rather than a saying
    public boolean isFact() {
        return isFact;
    }
}
