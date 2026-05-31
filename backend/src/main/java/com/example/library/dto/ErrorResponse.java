package com.example.library.dto;

import java.util.List;

public record ErrorResponse(ErrorBody error) {

    public record ErrorBody(String code, String message, List<String> details) {
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(new ErrorBody(code, message, List.of()));
    }
}
