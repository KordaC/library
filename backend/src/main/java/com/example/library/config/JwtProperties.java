package com.example.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "library.jwt")
public record JwtProperties(String secret, int expirationHours) {
}
