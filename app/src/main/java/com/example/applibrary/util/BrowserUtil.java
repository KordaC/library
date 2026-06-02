package com.example.applibrary.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import com.example.applibrary.R;

public final class BrowserUtil {

    private BrowserUtil() {}

    public static void openUrl(Context context, @Nullable String url) {
        if (url == null || url.isBlank()) {
            Toast.makeText(context, R.string.ticket_url_missing, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            CustomTabsIntent tabs = new CustomTabsIntent.Builder().build();
            tabs.launchUrl(context, Uri.parse(url));
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }
}
