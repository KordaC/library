package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;

import java.io.IOException;

import retrofit2.Response;

public class HealthRepository {

    private static final int WAKE_ATTEMPTS = 8;
    private static final long WAKE_PAUSE_MS = 12_000L;

    private final LibraryApi api;

    public HealthRepository(LibraryApi api) {
        this.api = api;
    }

    public boolean ping() {
        try {
            Response<?> response = api.health().execute();
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    /** Несколько попыток — Render free «просыпается» до 1–2 минут. */
    public boolean waitUntilReady() {
        for (int i = 0; i < WAKE_ATTEMPTS; i++) {
            if (ping()) {
                return true;
            }
            if (i < WAKE_ATTEMPTS - 1) {
                try {
                    Thread.sleep(WAKE_PAUSE_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
}
