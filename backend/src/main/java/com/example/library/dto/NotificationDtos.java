package com.example.library.dto;

public final class NotificationDtos {

    private NotificationDtos() {}

    public record RegisterFcmTokenRequest(String token, String deviceId) {}

    public record UnregisterFcmTokenRequest(String token) {}
}
