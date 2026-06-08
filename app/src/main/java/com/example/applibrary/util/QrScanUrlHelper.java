package com.example.applibrary.util;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import com.example.applibrary.data.remote.dto.DashboardDtos;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public final class QrScanUrlHelper {

    private static final Gson GSON = new Gson();
    private static final String APP_SCHEME = "applibrary";
    private static final String APP_HOST = "ticket";

    private QrScanUrlHelper() {}

    /** URL, который кодируется в QR: открывает приложение при сканировании. */
    public static String resolve(Context context, DashboardDtos.QrResponse qr) {
        String token = resolveToken(context, qr);
        if (token == null || token.isEmpty()) {
            return null;
        }
        return Uri.parse(APP_SCHEME + "://" + APP_HOST)
                .buildUpon()
                .appendQueryParameter("token", token)
                .build()
                .toString();
    }

    /** HTTPS-ссылка для браузера (библиотекарь без приложения). */
    public static String webTicketUrl(Context context, DashboardDtos.QrResponse qr) {
        String token = resolveToken(context, qr);
        if (token == null || token.isEmpty()) {
            return null;
        }
        return Uri.parse(serverBaseUrl(context) + "/card/ticket.html")
                .buildUpon()
                .appendQueryParameter("token", token)
                .build()
                .toString();
    }

    public static String extractToken(Uri uri) {
        if (uri == null) return null;
        if (APP_SCHEME.equals(uri.getScheme()) && APP_HOST.equals(uri.getHost())) {
            return uri.getQueryParameter("token");
        }
        if (uri.getPath() != null && uri.getPath().contains("/card/ticket")) {
            return uri.getQueryParameter("token");
        }
        return null;
    }

    private static String resolveToken(Context context, DashboardDtos.QrResponse qr) {
        if (qr == null) return null;
        if (qr.scanUrl != null && !qr.scanUrl.isEmpty()) {
            String fromUrl = extractToken(Uri.parse(qr.scanUrl));
            if (fromUrl != null && !fromUrl.isEmpty()) {
                return fromUrl;
            }
        }
        if (qr.payload == null) return null;
        return encodeToken(qr.payload);
    }

    private static String serverBaseUrl(Context context) {
        String base = new ServerUrlStorage(context).getEffectiveBaseUrl().trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (base.endsWith("/api/v1")) {
            base = base.substring(0, base.length() - "/api/v1".length());
        }
        return base;
    }

    private static String encodeToken(DashboardDtos.QrPayload payload) {
        String json = GSON.toJson(payload);
        return Base64.encodeToString(
                json.getBytes(StandardCharsets.UTF_8),
                Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING
        );
    }
}
