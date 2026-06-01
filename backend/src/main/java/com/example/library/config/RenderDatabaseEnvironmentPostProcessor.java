package com.example.library.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Render отдаёт postgresql://... — Spring JDBC ждёт jdbc:postgresql://...
 */
public class RenderDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
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
}
