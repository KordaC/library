package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;

import java.io.IOException;

import retrofit2.Response;

public class HealthRepository {

    private final LibraryApi api;

    public HealthRepository(LibraryApi api) {
        this.api = api;
    }

    /** Пробуждение Render и проверка доступности. */
    public boolean ping() {
        try {
            Response<?> response = api.health().execute();
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }
}
