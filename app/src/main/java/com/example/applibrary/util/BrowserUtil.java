package com.example.applibrary.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
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
        Uri uri = Uri.parse(url);
        try {
            if (context instanceof Activity) {
                CustomTabsIntent tabs = new CustomTabsIntent.Builder().build();
                tabs.launchUrl(context, uri);
                return;
            }
        } catch (ActivityNotFoundException ignored) {
        } catch (Exception ignored) {
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.browser_missing, Toast.LENGTH_LONG).show();
        }
    }
}
