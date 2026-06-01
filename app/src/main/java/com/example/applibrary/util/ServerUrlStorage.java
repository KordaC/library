package com.example.applibrary.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.applibrary.BuildConfig;

public class ServerUrlStorage {

    private static final String PREFS = "library_server";
    private static final String KEY_BASE_URL = "api_base_url";

    private final SharedPreferences prefs;

    public ServerUrlStorage(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public String getEffectiveBaseUrl() {
        if (!BuildConfig.DEBUG) {
            return BuildConfig.BASE_URL;
        }
        String stored = prefs.getString(KEY_BASE_URL, "");
        if (stored != null && !stored.isBlank()) {
            return stored;
        }
        return BuildConfig.BASE_URL;
    }

    public void saveBaseUrl(String input) {
        prefs.edit().putString(KEY_BASE_URL, normalize(input)).apply();
    }

    public void clear() {
        prefs.edit().remove(KEY_BASE_URL).apply();
    }

    public boolean hasOverride() {
        String stored = prefs.getString(KEY_BASE_URL, "");
        return stored != null && !stored.isBlank();
    }

    /** Приводит ввод к виду https://host/api/v1/ */
    public static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String url = input.trim();
        if (url.isEmpty()) {
            return "";
        }
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (!url.matches("(?i)https?://.*")) {
            url = "https://" + url;
        }
        if (!url.endsWith("/api/v1")) {
            if (url.endsWith("/api")) {
                url = url + "/v1";
            } else {
                url = url + "/api/v1";
            }
        }
        return url + "/";
    }
}
