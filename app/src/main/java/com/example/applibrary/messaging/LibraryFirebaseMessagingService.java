package com.example.applibrary.messaging;

import androidx.annotation.NonNull;

import com.example.applibrary.util.FcmTokenRegistrar;
import com.example.applibrary.util.PushNotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class LibraryFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = message.getNotification() != null ? message.getNotification().getTitle() : "";
        String body = message.getNotification() != null ? message.getNotification().getBody() : "";
        PushNotificationHelper.show(this, title, body, message.getData());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        FcmTokenRegistrar.registerIfLoggedIn(this);
    }
}
