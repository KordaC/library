package com.example.applibrary.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InputMasksTest {

    @Test
    public void normalizePhone_russianMobile() {
        assertEquals("+79001234567", InputMasks.normalizePhone("8 (900) 123-45-67"));
        assertEquals("+79001234567", InputMasks.normalizePhone("9001234567"));
    }

    @Test
    public void toApiDate_fromDisplayFormat() {
        assertEquals("1990-05-15", InputMasks.toApiDate("15.05.1990"));
    }

    @Test
    public void toApiDate_invalidReturnsEmpty() {
        assertEquals("", InputMasks.toApiDate("15.05"));
        assertEquals("", InputMasks.toApiDate(""));
    }

    @Test
    public void formatBirthDateDisplay_fromIso() {
        String display = InputMasks.formatBirthDateDisplay("1990-05-15");
        assertEquals(true, display.contains("1990"));
        assertEquals(true, display.contains("15"));
    }
}
