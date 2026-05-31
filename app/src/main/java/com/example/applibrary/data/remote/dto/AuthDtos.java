package com.example.applibrary.data.remote.dto;

public final class AuthDtos {

    private AuthDtos() {}

    public static class LoginRequest {
        public String cardNumber;
        public String password;

        public LoginRequest(String cardNumber, String password) {
            this.cardNumber = cardNumber;
            this.password = password;
        }
    }

    public static class LoginResponse {
        public String accessToken;
        public long expiresIn;
        public UserBrief user;
    }

    public static class UserBrief {
        public String id;
        public String fullName;
        public String cardNumber;
    }
}
