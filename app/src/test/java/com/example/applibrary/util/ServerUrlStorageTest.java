package com.example.applibrary.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServerUrlStorageTest {

    @Test
    public void normalize_addsHttpsAndApiPath() {
        assertEquals("https://example.com/api/v1/", ServerUrlStorage.normalize("example.com"));
    }

    @Test
    public void normalize_keepsExistingApiSuffix() {
        assertEquals("https://host.test/api/v1/", ServerUrlStorage.normalize("https://host.test/api/v1/"));
    }

    @Test
    public void normalize_emptyInput() {
        assertEquals("", ServerUrlStorage.normalize(""));
        assertEquals("", ServerUrlStorage.normalize(null));
    }
}
