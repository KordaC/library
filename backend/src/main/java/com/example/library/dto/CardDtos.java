package com.example.library.dto;

import java.util.Map;

public final class CardDtos {

    private CardDtos() {}

    public record QrPayload(int v, String uid, String card, long iat, long exp, String sig) {}

    public record QrResponse(QrPayload payload, Map<String, String> display, String scanUrl) {}

    public record QrCardView(String fullName, String cardNumber, String status, String validUntil) {}

    public record CardResponse(String cardNumber, String status, String issuedAt, String registeredAt) {}
}
