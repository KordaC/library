package com.example.applibrary;

import android.app.Application;

import com.example.applibrary.di.AppContainer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibraryApplication extends Application {

    private volatile AppContainer appContainer;
    private final ExecutorService initExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        // Прогрев контейнера в фоне, чтобы не блокировать старт Activity
        initExecutor.execute(this::getAppContainer);
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

    /** После смены адреса сервера в профиле. */
    public void recreateAppContainer() {
        synchronized (this) {
            appContainer = new AppContainer(getApplicationContext());
        }
    }
}
