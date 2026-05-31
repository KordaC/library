package com.example.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "library.qr")
public record QrProperties(String secret) {
}
