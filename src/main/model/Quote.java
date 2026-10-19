package model;

import java.util.Collections;
import java.util.List;
import java.util.Random;

// Represents one line shown on the home screen: a fact about ultimate, or
// something worth reading before training, with a short category above it.
//
// The collection is held here as a constant rather than in the database.
// These ship with the app the way the colours do; they are not the player's
// data, there is nothing to add or delete, and keeping them in code means
// there is no table to seed on first run and no way to end up with none.
public class Quote {

    private static final Random RANDOM = new Random();

    private static final List<Quote> ALL = List.of(
            new Quote("The disc has no memory of your last throw.", "Ultimate wisdom"),
            new Quote("Everyone gets a turn. That is the whole sport.", "On the force"),
            new Quote("There are no referees. Only people who agree.", "Spirit of the game"),
            new Quote("A layout you did not attempt is a layout you did not make.", "Field fact"),
            new Quote("The wind is not against you. It is against everyone.", "Ultimate wisdom"),
            new Quote("Catch it first. Celebrate second.", "Hard-won advice"),
            new Quote("A bad throw to open space beats a good throw to a crowd.", "Field fact"),
            new Quote("You cannot out-throw bad footwork.", "Hard-won advice"),
            new Quote("Ultimate was invented in a high school car park in 1968.", "Did you know"),
            new Quote("The pull is the only throw you get to practise alone, endlessly.", "Field fact"),
            new Quote("Nobody remembers the drop. They remember whether you ran back.", "Spirit of the game"),
            new Quote("Flat is a feeling, not an angle.", "On the forehand"),
            new Quote("Every handler was once terrified of the forehand.", "Hard-won advice"),
            new Quote("You get better at hucking by throwing short ones properly.", "Ultimate wisdom"),
            new Quote("A disc weighs 175 grams. Everything else is technique.", "Did you know"),
            new Quote("The best defenders are just the ones still running.", "On the force"),
            new Quote("Call your own fouls. It is the only rule that matters.", "Spirit of the game"),
            new Quote("Tired legs throw high. Watch for it.", "Field fact"),
            new Quote("Consistency beats power. Power beats nothing on its own.", "Ultimate wisdom"),
            new Quote("Show up when it rains. That is most of it.", "Hard-won advice"));

    private final String text;
    private final String category;

    // REQUIRES: text != null and not blank, category != null and not blank
    // EFFECTS: constructs a quote with the given text and category
    public Quote(String text, String category) {
        this.text = text;
        this.category = category;
    }

    // EFFECTS: returns every quote the app knows, in a list that cannot be
    //          changed by the caller
    public static List<Quote> all() {
        return Collections.unmodifiableList(ALL);
    }

    // EFFECTS: returns one quote picked at random. With twenty of them, seeing
    //          the same one twice in a row is rare enough not to look broken,
    //          so no effort is made to remember the last one shown.
    public static Quote random() {
        return ALL.get(RANDOM.nextInt(ALL.size()));
    }

    // EFFECTS: returns the line itself
    public String getText() {
        return text;
    }

    // EFFECTS: returns the short heading shown above the line
    public String getCategory() {
        return category;
    }
}
