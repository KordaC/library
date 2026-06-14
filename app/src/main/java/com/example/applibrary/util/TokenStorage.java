package com.example.applibrary.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenStorage {

    private static final String PREFS = "library_auth";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_CARD = "card_number";
    private static final String KEY_NAME = "full_name";

    private final SharedPreferences prefs;

    public TokenStorage(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String cardNumber, String fullName) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_CARD, cardNumber)
                .putString(KEY_NAME, fullName)
                .apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }

    public String getCardNumber() {
        return prefs.getString(KEY_CARD, "");
    }

    public String getFullName() {
        return prefs.getString(KEY_NAME, "");
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
