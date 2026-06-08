package com.example.applibrary.util;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import com.example.applibrary.BuildConfig;
import com.example.applibrary.data.remote.dto.DashboardDtos;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public final class QrScanUrlHelper {

    private static final Gson GSON = new Gson();
    private static final String APP_SCHEME = "applibrary";
    private static final String APP_HOST = "ticket";
    private static final String TICKET_PATH = "/card/ticket.html";

    private QrScanUrlHelper() {}

    /** HTTPS-ссылка для QR и браузера — при сканировании открывается страница билета. */
    public static String resolve(Context context, DashboardDtos.QrResponse qr) {
        return webTicketUrl(context, qr);
    }

    public static String webTicketUrl(Context context, DashboardDtos.QrResponse qr) {
        String token = resolveToken(context, qr);
        if (token == null || token.isEmpty()) {
            return null;
        }
        if (qr != null && qr.scanUrl != null && qr.scanUrl.startsWith("https://")) {
            return qr.scanUrl;
        }
        String base = serverBaseUrl(context);
        return Uri.parse(base + TICKET_PATH)
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
        if (isLocalUrl(base)) {
            base = cloudBaseFromBuildConfig();
        }
        return base;
    }

    private static boolean isLocalUrl(String base) {
        String lower = base.toLowerCase();
        return lower.contains("192.168.")
                || lower.contains("10.0.2.2")
                || lower.contains("localhost")
                || lower.startsWith("http://");
    }

    private static String cloudBaseFromBuildConfig() {
        String base = BuildConfig.BASE_URL.trim();
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
