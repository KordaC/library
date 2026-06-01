package com.example.applibrary.util;

import android.util.Base64;

import android.content.Context;

import com.example.applibrary.data.remote.dto.DashboardDtos;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public final class QrScanUrlHelper {

    private static final Gson GSON = new Gson();

    private QrScanUrlHelper() {}

    public static String resolve(Context context, DashboardDtos.QrResponse qr) {
        if (qr == null) {
            return null;
        }
        if (qr.scanUrl != null && !qr.scanUrl.isEmpty()) {
            return qr.scanUrl;
        }
        if (qr.payload == null) {
            return null;
        }
        String base = serverBaseUrl(context);
        String token = encodeToken(qr.payload);
        return base + "/card/ticket.html?token=" + token;
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
