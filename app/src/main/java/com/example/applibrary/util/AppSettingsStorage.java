package com.example.applibrary.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class AppSettingsStorage {

    public static final String THEME_SYSTEM = "system";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";

    public static final String LANG_SYSTEM = "system";
    public static final String LANG_RU = "ru";
    public static final String LANG_EN = "en";

    private static final String PREFS = "library_settings";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_QR_BRIGHTNESS = "qr_brightness";

    private final SharedPreferences prefs;

    public AppSettingsStorage(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public String getTheme() {
        return prefs.getString(KEY_THEME, THEME_SYSTEM);
    }

    public void setTheme(String theme) {
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, LANG_SYSTEM);
    }

    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }

    public boolean isQrBrightnessBoost() {
        return prefs.getBoolean(KEY_QR_BRIGHTNESS, true);
    }

    public void setQrBrightnessBoost(boolean enabled) {
        prefs.edit().putBoolean(KEY_QR_BRIGHTNESS, enabled).apply();
    }

    public static int themeToNightMode(String theme) {
        if (THEME_LIGHT.equals(theme)) {
            return AppCompatDelegate.MODE_NIGHT_NO;
        }
        if (THEME_DARK.equals(theme)) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        }
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }
}
