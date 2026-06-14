package com.example.library.service;

import com.example.library.entity.Notification;
import com.example.library.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationDispatchService {

    private final NotificationRepository notificationRepository;
    private final PushNotificationService pushNotificationService;

    public NotificationDispatchService(
            NotificationRepository notificationRepository,
            PushNotificationService pushNotificationService
    ) {
        this.notificationRepository = notificationRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @Transactional
    public void dispatch(
            UUID userId,
            String type,
            String title,
            String body,
            String dedupKey,
            Map<String, String> data
    ) {
        if (dedupKey != null && notificationRepository.existsByUserIdAndDedupKey(userId, dedupKey)) {
            return;
        }
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setReadFlag(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setDedupKey(dedupKey);
        notificationRepository.save(notification);

        Map<String, String> payload = data != null ? data : Map.of();
        pushNotificationService.sendToUser(userId, title, body, payload);
    }
}
