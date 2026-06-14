package com.example.applibrary.data.remote.dto;

public final class NotificationDtos {

    private NotificationDtos() {}

    public static class RegisterFcmTokenRequest {
        public String token;
        public String deviceId;

        public RegisterFcmTokenRequest(String token, String deviceId) {
            this.token = token;
            this.deviceId = deviceId;
        }
    }

    public static class UnregisterFcmTokenRequest {
        public String token;

        public UnregisterFcmTokenRequest(String token) {
            this.token = token;
        }
    }
}
