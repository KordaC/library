package com.example.applibrary;

import android.app.Application;

import com.example.applibrary.di.AppContainer;
import com.example.applibrary.util.AppSettingsApplier;
import com.example.applibrary.util.FcmTokenRegistrar;
import com.example.applibrary.util.PushNotificationHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibraryApplication extends Application {

    private volatile AppContainer appContainer;
    private final ExecutorService initExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        AppSettingsApplier.apply(this);
        super.onCreate();
        PushNotificationHelper.ensureChannel(this);
        initExecutor.execute(() -> {
            getAppContainer();
            FcmTokenRegistrar.registerIfLoggedIn(this);
        });
    }

    public AppContainer getAppContainer() {
        if (appContainer == null) {
            synchronized (this) {
                if (appContainer == null) {
                    appContainer = new AppContainer(getApplicationContext());
                }
            }
        }
        return appContainer;
    }

    public void recreateAppContainer() {
        synchronized (this) {
            appContainer = new AppContainer(getApplicationContext());
            appContainer.getAuthRepository().attachContext(getApplicationContext());
        }
    }
}
