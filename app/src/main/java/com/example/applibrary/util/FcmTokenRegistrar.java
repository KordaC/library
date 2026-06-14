package com.example.applibrary.util;

import android.content.Context;

import com.example.applibrary.LibraryApplication;
import com.google.firebase.messaging.FirebaseMessaging;

public final class FcmTokenRegistrar {

    private FcmTokenRegistrar() {}

    public static void registerIfLoggedIn(Context context) {
        LibraryApplication app = (LibraryApplication) context.getApplicationContext();
        if (!app.getAppContainer().getAuthRepository().isLoggedIn()) {
            return;
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        return;
                    }
                    String token = task.getResult();
                    new Thread(() -> app.getAppContainer()
                            .getNotificationRepository()
                            .registerToken(token)).start();
                });
    }

    public static void unregister(Context context, Runnable onDone) {
        LibraryApplication app = (LibraryApplication) context.getApplicationContext();
        if (!app.getAppContainer().getAuthRepository().isLoggedIn()) {
            if (onDone != null) {
                onDone.run();
            }
            return;
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        new Thread(() -> {
                            app.getAppContainer().getNotificationRepository().unregisterToken(token);
                            if (onDone != null) {
                                onDone.run();
                            }
                        }).start();
                    } else if (onDone != null) {
                        onDone.run();
                    }
                });
    }
}
