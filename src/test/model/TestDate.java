package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestDate {
    private Date testDate1;
    private Date testDate2;
    private Date testDate3;

    @BeforeEach
    void runBefore() {
        testDate1 = new Date(15, 6, 2024);  // Regular date
        testDate2 = new Date(1, 1, 2023);   // Boundary: first day, first month
        testDate3 = new Date(31, 12, 2025); // Boundary: last day, last month
    }

    @Test
    void testConstructor() {
        assertEquals(15, testDate1.getDay());
        assertEquals(6, testDate1.getMonth());
        assertEquals(2024, testDate1.getYear());
    }

    @Test
    void testConstructor1stDayOfMonth() {
        assertEquals(1, testDate2.getDay());
        assertEquals(1, testDate2.getMonth());
        assertEquals(2023, testDate2.getYear());
    }

    @Test
    void testConstructorLastDayOfMonth() {
        assertEquals(31, testDate3.getDay());
        assertEquals(12, testDate3.getMonth());
        assertEquals(2025, testDate3.getYear());
    }
    

    @Test
    void testConstructorFeb28NonLeapYear() {
        Date feb28 = new Date(28, 2, 2023);
        assertEquals(28, feb28.getDay());
        assertEquals(2, feb28.getMonth());
        assertEquals(2023, feb28.getYear());
    }


    @Test
    void testConstructorFeb28LeapYear() {
        Date feb29 = new Date(29, 2, 2024);
        assertEquals(29, feb29.getDay());
        assertEquals(2, feb29.getMonth());
        assertEquals(2024, feb29.getYear());
    }


    @Test
    void testConstructorMonthWith30Days() {
        Date april30 = new Date(30, 4, 2023);
        assertEquals(30, april30.getDay());
        assertEquals(4, april30.getMonth());
    }

    @Test
    void testConstructorMonthWith31Days() {
        Date jan31 = new Date(31, 1, 2023);
        assertEquals(31, jan31.getDay());
        assertEquals(4, jan31.getMonth());
    }

    @Test
    void testGetDay() {
        assertEquals(15, testDate1.getDay()); //middle of month
        assertEquals(1, testDate2.getDay()); //first day of month
        assertEquals(31, testDate3.getDay()); //last day of month
    }

    @Test
    void testGetMonth() {
        assertEquals(6, testDate1.getMonth()); //middle of year
        assertEquals(1, testDate2.getMonth()); //january
        assertEquals(12, testDate3.getMonth()); //december
    }


    @Test
    void testGetYear() {
        assertEquals(2024, testDate1.getDay()); 
        assertEquals(2023, testDate2.getDay()); 
        assertEquals(2025, testDate3.getDay()); 
    }

    @Test
    void testSetDate() {
        testDate1.setDate(11, 23, 2030);
        assertEquals(23, testDate1.getDay()); 
        assertEquals(11, testDate2.getMonth()); 
        assertEquals(2030, testDate3.getYear()); 
    }

    @Test
    void testGetMonthInStringFormat() {
        assertEquals("April", testDate1.getMonthInStringFormat()); 
        assertEquals("January", testDate2.getMonthInStringFormat());
         assertEquals("December", testDate3.getMonthInStringFormat());
    }

    @Test
    void testGetFullDateInStringFormat() {
        assertEquals("4/16/2024", testDate1.getFullDateInStringFormat()); 
        assertEquals("1/1/2023", testDate2.getFullDateInStringFormat());
        assertEquals("12/31/2025", testDate3.getMonthInStringFormat()); 
    }

    @Test
    void testSetAndGetPastAndFutureDates() {
        Date pastDate = new Date(10, 11, 1995);
        Date futureDate = new Date(8, 8, 2050);
        
        assertEquals(10, pastDate.getDay());
        assertEquals(11, pastDate.getMonth());
        assertEquals(1995, pastDate.getYear());

        assertEquals(8, futureDate.getDay());
        assertEquals(8, futureDate.getMonth());
        assertEquals(2050, futureDate.getYear());
    }
}
