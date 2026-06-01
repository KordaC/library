package com.example.library.web;

import com.example.library.config.PublicUrlProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class PublicUrlResolver {

    private final PublicUrlProperties properties;

    public PublicUrlResolver(PublicUrlProperties properties) {
        this.properties = properties;
    }

    public String resolveBaseUrl() {
        String configured = properties.baseUrl();
        if (configured != null && !configured.isBlank()) {
            return trimTrailingSlash(configured.trim());
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    private static String trimTrailingSlash(String url) {
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
