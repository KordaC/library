package com.example.applibrary.util;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

public final class AppSettingsApplier {

    private AppSettingsApplier() {}

    public static void apply(Context context) {
        AppSettingsStorage storage = new AppSettingsStorage(context);
        AppCompatDelegate.setDefaultNightMode(AppSettingsStorage.themeToNightMode(storage.getTheme()));
        AppCompatDelegate.setApplicationLocales(localeList(storage.getLanguage()));
    }

    private static LocaleListCompat localeList(String language) {
        if (AppSettingsStorage.LANG_RU.equals(language)) {
            return LocaleListCompat.forLanguageTags("ru");
        }
        if (AppSettingsStorage.LANG_EN.equals(language)) {
            return LocaleListCompat.forLanguageTags("en");
        }
        return LocaleListCompat.getEmptyLocaleList();
    }
}
