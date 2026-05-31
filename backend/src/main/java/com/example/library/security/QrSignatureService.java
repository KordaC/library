package com.example.library.security;

import com.example.library.config.QrProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class QrSignatureService {

    private final byte[] secret;

    public QrSignatureService(QrProperties properties) {
        this.secret = properties.secret().getBytes(StandardCharsets.UTF_8);
    }

    public String sign(int version, UUID userId, String cardNumber, long iat, long exp) {
        String canonical = "v" + version + "|" + userId + "|" + cardNumber + "|" + iat + "|" + exp;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            byte[] raw = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        } catch (Exception e) {
            throw new IllegalStateException("QR sign failed", e);
        }
    }

    public boolean verify(int version, UUID userId, String cardNumber, long iat, long exp, String sig) {
        return sign(version, userId, cardNumber, iat, exp).equals(sig);
    }
}
