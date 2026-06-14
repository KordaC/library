package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.NotificationDtos;
import com.example.library.service.FcmTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final FcmTokenService fcmTokenService;

    public NotificationController(FcmTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }

    @PostMapping("/fcm-token")
    public ApiResponse<Void> registerToken(
            Authentication authentication,
            @RequestBody NotificationDtos.RegisterFcmTokenRequest body
    ) {
        UUID userId = (UUID) authentication.getPrincipal();
        fcmTokenService.registerToken(userId, body);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/fcm-token")
    public ApiResponse<Void> unregisterToken(
            Authentication authentication,
            @RequestBody NotificationDtos.UnregisterFcmTokenRequest body
    ) {
        UUID userId = (UUID) authentication.getPrincipal();
        fcmTokenService.unregisterToken(userId, body.token());
        return ApiResponse.ok(null);
    }
}
