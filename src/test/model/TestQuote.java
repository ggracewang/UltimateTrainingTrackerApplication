package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class TestQuote {

    @Test
    void testConstructor() {
        Quote quote = new Quote("Catch it first.", "Hard-won advice");

        assertEquals("Catch it first.", quote.getText());
        assertEquals("Hard-won advice", quote.getCategory());
    }

    @Test
    void testThereAreEnoughQuotesToNotRepeatConstantly() {
        // the home screen picks at random with no memory of the last one, so
        // the list has to be long enough that repeats are rare
        assertTrue(Quote.all().size() >= 15);
    }

    @Test
    void testNoQuoteIsBlank() {
        for (Quote quote : Quote.all()) {
            assertFalse(quote.getText().trim().isEmpty());
            assertFalse(quote.getCategory().trim().isEmpty());
        }
    }

    @Test
    void testNoQuoteIsSoLongItWillNotFitOnScreen() {
        // set at 64pt across roughly three lines, much past this stops fitting
        for (Quote quote : Quote.all()) {
            assertTrue(quote.getText().length() <= 70,
                    "too long to set large: " + quote.getText());
        }
    }

    @Test
    void testNoTwoQuotesAreTheSame() {
        Set<String> seen = new HashSet<>();
        for (Quote quote : Quote.all()) {
            assertTrue(seen.add(quote.getText()), "duplicated: " + quote.getText());
        }
    }

    @Test
    void testRandomAlwaysReturnsOneFromTheList() {
        List<Quote> all = Quote.all();
        for (int i = 0; i < 200; i++) {
            Quote picked = Quote.random();
            assertNotNull(picked);
            assertTrue(all.contains(picked));
        }
    }

    @Test
    void testRandomDoesNotAlwaysReturnTheSameOne() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            seen.add(Quote.random().getText());
        }
        assertTrue(seen.size() > 1, "random() kept returning the same quote");
    }

    @Test
    void testTheListCannotBeChangedByCallers() {
        assertThrows(UnsupportedOperationException.class,
                () -> Quote.all().add(new Quote("sneaky", "nope")));
    }
}
