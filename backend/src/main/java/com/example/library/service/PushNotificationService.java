package com.example.library.service;

import com.example.library.repository.FcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PushNotificationService {

    private final Optional<FirebaseMessaging> firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    public PushNotificationService(
            Optional<FirebaseMessaging> firebaseMessaging,
            FcmTokenRepository fcmTokenRepository
    ) {
        this.firebaseMessaging = firebaseMessaging;
        this.fcmTokenRepository = fcmTokenRepository;
    }

    public void sendToUser(UUID userId, String title, String body, Map<String, String> data) {
        FirebaseMessaging messaging = firebaseMessaging.orElse(null);
        if (messaging == null) {
            return;
        }
        var tokens = fcmTokenRepository.findByUserId(userId);
        for (var entry : tokens) {
            try {
                Message.Builder builder = Message.builder()
                        .setToken(entry.getToken())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build());
                if (data != null && !data.isEmpty()) {
                    builder.putAllData(data);
                }
                messaging.send(builder.build());
            } catch (FirebaseMessagingException e) {
                if (isInvalidToken(e)) {
                    fcmTokenRepository.delete(entry);
                }
            }
        }
    }

    private static boolean isInvalidToken(FirebaseMessagingException e) {
        MessagingErrorCode code = e.getMessagingErrorCode();
        return code == MessagingErrorCode.UNREGISTERED
                || code == MessagingErrorCode.INVALID_ARGUMENT;
    }
}
