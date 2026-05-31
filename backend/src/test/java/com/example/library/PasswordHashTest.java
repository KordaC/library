package com.example.library;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHashTest {

    private static final String DEMO_PASSWORD_HASH =
            "$2a$10$iH4p1HumWGqicA46VpRiJepxMUHK/Rp3XqLhcW0b48gIJ1y.YNkpa";

    @Test
    void demoPasswordHashMatchesDemo1234() {
        var encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("Demo1234", DEMO_PASSWORD_HASH));
    }

}
