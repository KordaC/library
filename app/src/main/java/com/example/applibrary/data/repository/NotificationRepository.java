package com.example.applibrary.data.repository;

import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.remote.dto.NotificationDtos;

public class NotificationRepository {

    private final LibraryApi api;

    public NotificationRepository(LibraryApi api) {
        this.api = api;
    }

    public ApiResult<Void> registerToken(String token) {
        return ApiCallHandler.execute(
                api.registerFcmToken(new NotificationDtos.RegisterFcmTokenRequest(token, null)));
    }

    public ApiResult<Void> unregisterToken(String token) {
        return ApiCallHandler.execute(
                api.unregisterFcmToken(new NotificationDtos.UnregisterFcmTokenRequest(token)));
    }
}
