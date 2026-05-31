package com.example.applibrary.data.remote.dto;

public class ProfileDtos {

    public static class ProfileResponse {
        public String userId;
        public String fullName;
        public String cardNumber;
        public String cardStatus;
        public String email;
        public String phone;
        public String birthDate;
        public String address;
    }

    public static class UpdateProfileRequest {
        public String email;
        public String phone;

        public UpdateProfileRequest(String email, String phone) {
            this.email = email;
            this.phone = phone;
        }
    }
}
