package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TestDateUtil {

    @Test
    void testFormatRegularDate() {
        assertEquals("6/15/2024", DateUtil.format(LocalDate.of(2024, 6, 15)));
    }

    @Test
    void testFormatBoundaryDates() {
        // single-digit day and month are not padded with zeroes
        assertEquals("1/1/2023", DateUtil.format(LocalDate.of(2023, 1, 1)));
        assertEquals("12/31/2025", DateUtil.format(LocalDate.of(2025, 12, 31)));
    }

    @Test
    void testFormatLeapDay() {
        assertEquals("2/29/2024", DateUtil.format(LocalDate.of(2024, 2, 29)));
    }

    @Test
    void testFormatNull() {
        assertEquals("", DateUtil.format(null));
    }
}
