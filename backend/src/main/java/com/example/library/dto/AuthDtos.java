package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {}

    public record LoginRequest(
            @NotBlank @Pattern(regexp = "\\d{5}") String cardNumber,
            @NotBlank @Size(min = 8) String password
    ) {}

    public record LoginResponse(
            String accessToken,
            long expiresIn,
            UserBrief user
    ) {}

    public record UserBrief(String id, String fullName, String cardNumber) {}
}
