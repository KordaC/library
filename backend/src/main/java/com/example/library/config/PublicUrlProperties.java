package com.example.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "library.public")
public record PublicUrlProperties(String baseUrl) {
}
