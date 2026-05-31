package com.example.applibrary.data.remote.dto;

public final class RegistrationDtos {

    private RegistrationDtos() {}

    public static class VerifyCardRequest {
        public String cardNumber;

        public VerifyCardRequest(String cardNumber) {
            this.cardNumber = cardNumber;
        }
    }

    public static class VerifyCardResponse {
        public boolean requiresVerification;
        public String hint;
    }

    public static class LinkCardRequest {
        public String cardNumber;
        public String birthDate;
        public String password;
        public String passwordConfirm;

        public LinkCardRequest(String cardNumber, String birthDate, String password, String passwordConfirm) {
            this.cardNumber = cardNumber;
            this.birthDate = birthDate;
            this.password = password;
            this.passwordConfirm = passwordConfirm;
        }
    }

    public static class NewRegistrationRequest {
        public String lastName;
        public String firstName;
        public String middleName;
        public String birthDate;
        public String passportSeries;
        public String passportNumber;
        public String address;
        public String phone;
        public String email;
    }

    public static class NewRegistrationResponse {
        public String requestId;
        public String status;
    }

    public static class MockPayResponse {
        public String paymentStatus;
        public String cardNumber;
        public String message;
    }

    public static class CompleteRequest {
        public String password;
        public String passwordConfirm;

        public CompleteRequest(String password, String passwordConfirm) {
            this.password = password;
            this.passwordConfirm = passwordConfirm;
        }
    }
}
