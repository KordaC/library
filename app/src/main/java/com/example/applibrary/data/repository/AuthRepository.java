package com.example.applibrary.data.repository;

import android.content.Context;

import com.example.applibrary.data.remote.LibraryApi;
import com.example.applibrary.data.remote.dto.AuthDtos;
import com.example.applibrary.util.FcmTokenRegistrar;
import com.example.applibrary.util.TokenStorage;

public class AuthRepository {

    private final LibraryApi api;
    private final TokenStorage tokenStorage;
    private Context appContext;

    public AuthRepository(LibraryApi api, TokenStorage tokenStorage) {
        this.api = api;
        this.tokenStorage = tokenStorage;
    }

    public void attachContext(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public ApiResult<AuthDtos.LoginResponse> login(String login, String password) {
        ApiResult<AuthDtos.LoginResponse> result = ApiCallHandler.execute(
                api.login(new AuthDtos.LoginRequest(login, password)));
        if (result instanceof ApiResult.Success) {
            persistSession(((ApiResult.Success<AuthDtos.LoginResponse>) result).getData());
        }
        return result;
    }

    public void persistSession(AuthDtos.LoginResponse response) {
        tokenStorage.saveSession(
                response.accessToken,
                response.user.cardNumber,
                response.user.fullName
        );
        if (appContext != null) {
            FcmTokenRegistrar.registerIfLoggedIn(appContext);
        }
    }

    public void logout() {
        if (appContext != null) {
            FcmTokenRegistrar.unregister(appContext, tokenStorage::clear);
        } else {
            tokenStorage.clear();
        }
    }

    public boolean isLoggedIn() {
        return tokenStorage.isLoggedIn();
    }
}
