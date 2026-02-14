package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(1, jan31.getMonth());
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
        assertEquals(2024, testDate1.getYear()); 
        assertEquals(2023, testDate2.getYear()); 
        assertEquals(2025, testDate3.getYear()); 
    }

    @Test
    void testSetDate() {
        testDate1.setDate(23, 11, 2030);
        assertEquals(23, testDate1.getDay()); 
        assertEquals(11, testDate1.getMonth()); 
        assertEquals(2030, testDate1.getYear()); 
    }

    @Test
    void testGetMonthInStringFormatAllMonths() {
    Date jan = new Date(1, 1, 2024);
    Date feb = new Date(1, 2, 2024);
    Date mar = new Date(1, 3, 2024);
    Date apr = new Date(1, 4, 2024);
    Date may = new Date(1, 5, 2024);
    Date jun = new Date(1, 6, 2024);
    Date jul = new Date(1, 7, 2024);
    Date aug = new Date(1, 8, 2024);
    Date sep = new Date(1, 9, 2024);
    Date oct = new Date(1, 10, 2024);
    Date nov = new Date(1, 11, 2024);
    Date dec = new Date(1, 12, 2024);
    
    assertEquals("January", jan.getMonthInStringFormat());
    assertEquals("February", feb.getMonthInStringFormat());
    assertEquals("March", mar.getMonthInStringFormat());
    assertEquals("April", apr.getMonthInStringFormat());
    assertEquals("May", may.getMonthInStringFormat());
    assertEquals("June", jun.getMonthInStringFormat());
    assertEquals("July", jul.getMonthInStringFormat());
    assertEquals("August", aug.getMonthInStringFormat());
    assertEquals("September", sep.getMonthInStringFormat());
    assertEquals("October", oct.getMonthInStringFormat());
    assertEquals("November", nov.getMonthInStringFormat());
    assertEquals("December", dec.getMonthInStringFormat());
}

    @Test
    void testGetFullDateInStringFormat() {
        assertEquals("6/15/2024", testDate1.getFullDateInStringFormat()); 
        assertEquals("1/1/2023", testDate2.getFullDateInStringFormat());
        assertEquals("12/31/2025", testDate3.getFullDateInStringFormat()); 
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
