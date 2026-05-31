package com.example.library.dto;

public final class ProfileDtos {

    private ProfileDtos() {}

    public record ProfileResponse(
            String userId,
            String fullName,
            String cardNumber,
            String cardStatus,
            String email,
            String phone,
            String birthDate,
            String address
    ) {}

    public record UpdateProfileRequest(String email, String phone) {}
}
