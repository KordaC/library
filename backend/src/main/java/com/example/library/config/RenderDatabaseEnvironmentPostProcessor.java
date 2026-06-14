package com.example.library.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class RenderDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String H2_URL =
            "jdbc:h2:mem:library;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (isCloudProfile(environment)) {
            Map<String, Object> cloud = new HashMap<>();
            cloud.put("spring.datasource.url", H2_URL);
            cloud.put("spring.datasource.driver-class-name", "org.h2.Driver");
            cloud.put("spring.datasource.username", "sa");
            cloud.put("spring.datasource.password", "");
            environment.getPropertySources().addFirst(new MapPropertySource("cloudDataSource", cloud));
            return;
        }
        String raw = environment.getProperty("SPRING_DATASOURCE_URL");
        if (raw == null || raw.isBlank()) {
            raw = environment.getProperty("DATABASE_URL");
        }
        if (raw == null || raw.isBlank() || raw.startsWith("jdbc:")) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("spring.datasource.url", toJdbcUrl(raw));
        environment.getPropertySources().addFirst(new MapPropertySource("renderJdbcUrl", map));
    }

    static String toJdbcUrl(String raw) {
        if (raw.startsWith("postgresql://")) {
            return "jdbc:" + raw;
        }
        if (raw.startsWith("postgres://")) {
            return "jdbc:postgresql://" + raw.substring("postgres://".length());
        }
        return raw;
    }

    private static boolean isCloudProfile(ConfigurableEnvironment environment) {
        if (environment.matchesProfiles("cloud")) {
            return true;
        }
        String active = environment.getProperty("spring.profiles.active");
        if (active == null || active.isBlank()) {
            active = environment.getProperty("SPRING_PROFILES_ACTIVE");
        }
        return active != null && active.contains("cloud");
    }
}
