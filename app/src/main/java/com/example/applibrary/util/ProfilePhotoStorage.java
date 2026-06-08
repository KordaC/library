package com.example.applibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class ProfilePhotoStorage {

    private static final String PREFS = "library_profile_photo";
    private final SharedPreferences prefs;

    public ProfilePhotoStorage(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(String cardNumber, Uri uri) {
        if (cardNumber == null || cardNumber.isBlank() || uri == null) return;
        prefs.edit().putString(key(cardNumber), uri.toString()).apply();
    }

    public Uri get(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) return null;
        String raw = prefs.getString(key(cardNumber), null);
        return raw != null ? Uri.parse(raw) : null;
    }

    public void clear(String cardNumber) {
        if (cardNumber == null) return;
        prefs.edit().remove(key(cardNumber)).apply();
    }

    private static String key(String cardNumber) {
        return "photo_" + cardNumber;
    }
}
