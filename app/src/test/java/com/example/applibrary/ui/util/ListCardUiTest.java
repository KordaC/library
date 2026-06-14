package com.example.applibrary.ui.util;

import com.example.applibrary.R;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListCardUiTest {

    @Test
    public void formatEventDate_withTime() {
        String formatted = ListCardUi.formatEventDate("2026-06-15T14:30:00");
        assertTrue(formatted.contains("2026"));
        assertTrue(formatted.contains("14:30"));
    }

    @Test
    public void formatEventDate_midnightUsesDefaultTime() {
        String formatted = ListCardUi.formatEventDate("2026-06-15T00:00:00");
        assertTrue(formatted.contains("18:00"));
    }

    @Test
    public void eventProgressPercent_calculatesShare() {
        assertEquals(50, ListCardUi.eventProgressPercent(15, 30));
        assertEquals(100, ListCardUi.eventProgressPercent(40, 30));
        assertEquals(0, ListCardUi.eventProgressPercent(10, 0));
    }

    @Test
    public void eventTypeLabelRes_knownTypes() {
        assertEquals(R.string.event_type_meeting, ListCardUi.eventTypeLabelRes("MEETING"));
        assertEquals(R.string.event_type_reading, ListCardUi.eventTypeLabelRes("READING"));
    }
}
