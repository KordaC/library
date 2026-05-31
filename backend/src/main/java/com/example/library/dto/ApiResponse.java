package com.example.library.dto;

import java.time.Instant;
import java.util.Map;

public record ApiResponse<T>(T data, Map<String, Object> meta) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data, Map.of("timestamp", Instant.now().toString()));
    }
}
