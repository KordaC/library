package com.example.applibrary.data.remote.dto;

public final class AuthDtos {

    private AuthDtos() {}

    public static class LoginRequest {
        public String login;
        public String password;

        public LoginRequest(String login, String password) {
            this.login = login;
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
