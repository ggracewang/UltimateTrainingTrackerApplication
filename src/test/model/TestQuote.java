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
    void testSaying() {
        Quote quote = Quote.saying("Catch it first.", "Hard-won advice");

        assertEquals("Catch it first.", quote.getText());
        assertEquals("Hard-won advice", quote.getCategory());
        assertFalse(quote.isFact());
    }

    @Test
    void testSayingIsShownInCapitals() {
        assertEquals("CATCH IT FIRST.",
                Quote.saying("Catch it first.", "Hard-won advice").getDisplayText());
    }

    @Test
    void testFact() {
        Quote quote = Quote.fact("A disc weighs 175 grams.", "Did you know");

        assertEquals("A disc weighs 175 grams.", quote.getText());
        assertTrue(quote.isFact());
    }

    @Test
    void testFactIsShownAsWritten() {
        // shouting a measurement reads as odd rather than emphatic
        assertEquals("A disc weighs 175 grams.",
                Quote.fact("A disc weighs 175 grams.", "Did you know").getDisplayText());
    }

    @Test
    void testThereAreSomeOfEach() {
        long facts = Quote.all().stream().filter(Quote::isFact).count();

        assertTrue(facts > 0, "no facts in the list");
        assertTrue(facts < Quote.all().size(), "no sayings in the list");
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
        // sayings are set at 64pt and facts at 46pt, so a fact can run longer
        // before it stops fitting on three lines
        for (Quote quote : Quote.all()) {
            int limit = quote.isFact() ? 90 : 70;
            assertTrue(quote.getText().length() <= limit,
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
                () -> Quote.all().add(Quote.saying("sneaky", "nope")));
    }
}
