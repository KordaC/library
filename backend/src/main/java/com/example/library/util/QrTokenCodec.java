package com.example.library.util;

import com.example.library.dto.CardDtos;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class QrTokenCodec {

    private final ObjectMapper objectMapper;

    public QrTokenCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String encode(CardDtos.QrPayload payload) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(payload);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (Exception e) {
            throw new IllegalStateException("QR encode failed", e);
        }
    }

    public CardDtos.QrPayload decode(String token) {
        try {
            byte[] json = Base64.getUrlDecoder().decode(token);
            return objectMapper.readValue(json, CardDtos.QrPayload.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректный QR-код");
        }
    }
}
