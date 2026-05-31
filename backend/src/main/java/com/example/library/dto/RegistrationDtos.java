package com.example.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class RegistrationDtos {

    private RegistrationDtos() {}

    public record VerifyCardRequest(@NotBlank @Pattern(regexp = "\\d{5}") String cardNumber) {}

    public record VerifyCardResponse(boolean requiresVerification, String hint) {}

    public record LinkCardRequest(
            @NotBlank @Pattern(regexp = "\\d{5}") String cardNumber,
            @NotNull LocalDate birthDate,
            @NotBlank @Size(min = 8) String password,
            @NotBlank String passwordConfirm
    ) {}

    public record NewRegistrationRequest(
            @NotBlank String lastName,
            @NotBlank String firstName,
            String middleName,
            @NotNull LocalDate birthDate,
            @NotBlank @Pattern(regexp = "\\d{4}") String passportSeries,
            @NotBlank @Pattern(regexp = "\\d{6}") String passportNumber,
            @NotBlank String address,
            @NotBlank String phone,
            @NotBlank @Email String email
    ) {}

    public record NewRegistrationResponse(String requestId, String status) {}

    public record MockPayResponse(String paymentStatus, String cardNumber, String message) {}

    public record CompleteRegistrationRequest(
            @NotBlank @Size(min = 8) String password,
            @NotBlank String passwordConfirm
    ) {}

    public record RegistrationStatusResponse(String status, String cardNumber) {}
}
