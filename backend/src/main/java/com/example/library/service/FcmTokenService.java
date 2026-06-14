package com.example.library.service;

import com.example.library.dto.NotificationDtos;
import com.example.library.entity.FcmToken;
import com.example.library.repository.FcmTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    public FcmTokenService(FcmTokenRepository fcmTokenRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
    }

    @Transactional
    public void registerToken(UUID userId, NotificationDtos.RegisterFcmTokenRequest request) {
        String token = request.token() != null ? request.token().trim() : "";
        if (token.isEmpty()) {
            return;
        }
        FcmToken existing = fcmTokenRepository.findByToken(token).orElse(null);
        if (existing != null) {
            existing.setUserId(userId);
            existing.setDeviceId(request.deviceId());
            fcmTokenRepository.save(existing);
            return;
        }
        FcmToken entity = new FcmToken();
        entity.setUserId(userId);
        entity.setToken(token);
        entity.setDeviceId(request.deviceId());
        fcmTokenRepository.save(entity);
    }

    @Transactional
    public void unregisterToken(UUID userId, String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        fcmTokenRepository.deleteByUserIdAndToken(userId, token.trim());
    }
}
