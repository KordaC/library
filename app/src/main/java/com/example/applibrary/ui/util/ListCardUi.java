package com.example.applibrary.ui.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.applibrary.R;

import coil.Coil;
import coil.request.ImageRequest;
import coil.size.Scale;
import coil.size.ViewSizeResolvers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class ListCardUi {

    private static final int[] COVER_PALETTES = {
            R.drawable.bg_cover_palette_0,
            R.drawable.bg_cover_palette_1,
            R.drawable.bg_cover_palette_2,
            R.drawable.bg_cover_palette_3,
            R.drawable.bg_cover_palette_4
    };

    private static final DateTimeFormatter EVENT_OUTPUT =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", new Locale("ru"));
    private static final DateTimeFormatter EVENT_DATE_ONLY =
            DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));

    private ListCardUi() {}

    public static void bindBookCover(
            @NonNull View coverContainer,
            @NonNull TextView initialView,
            @NonNull TextView yearView,
            @Nullable String title,
            @Nullable String author,
            @Nullable Integer publicationYear
    ) {
        String key = (title != null ? title : "") + (author != null ? author : "");
        int paletteIndex = Math.floorMod(key.hashCode(), COVER_PALETTES.length);
        coverContainer.setBackgroundResource(COVER_PALETTES[paletteIndex]);
        initialView.setText(extractInitials(title, author));
        if (publicationYear != null) {
            yearView.setVisibility(View.VISIBLE);
            yearView.setText(String.valueOf(publicationYear));
        } else {
            yearView.setVisibility(View.GONE);
        }
    }

    public static void bindBookCoverImage(
            @NonNull ImageView coverImage,
            @NonNull View coverContainer,
            @NonNull TextView initialView,
            @NonNull TextView yearView,
            @Nullable String coverImageUrl,
            @Nullable String title,
            @Nullable String author,
            @Nullable Integer publicationYear
    ) {
        bindBookCover(coverContainer, initialView, yearView, title, author, publicationYear);
        if (coverImageUrl == null || coverImageUrl.isBlank()) {
            coverImage.setVisibility(View.GONE);
            initialView.setVisibility(View.VISIBLE);
            return;
        }
        coverImage.setVisibility(View.VISIBLE);
        initialView.setVisibility(View.INVISIBLE);
        coverContainer.setBackgroundResource(R.drawable.bg_cover_image_frame);
        String url = toLargeCoverUrl(coverImageUrl);
        Coil.imageLoader(coverImage.getContext())
                .enqueue(new ImageRequest.Builder(coverImage.getContext())
                        .data(url)
                        .target(coverImage)
                        .size(ViewSizeResolvers.create(coverImage))
                        .scale(Scale.FIT)
                        .allowHardware(false)
                        .crossfade(true)
                        .listener(new ImageRequest.Listener() {
                            @Override
                            public void onError(@NonNull ImageRequest request,
                                                @NonNull coil.request.ErrorResult result) {
                                coverImage.setVisibility(View.GONE);
                                initialView.setVisibility(View.VISIBLE);
                                bindBookCover(coverContainer, initialView, yearView,
                                        title, author, publicationYear);
                            }
                        })
                        .build());
    }

    @NonNull
    private static String toLargeCoverUrl(@NonNull String url) {
        return url.replace("-S.jpg", "-L.jpg").replace("-M.jpg", "-L.jpg");
    }

    @NonNull
    public static String formatEventDate(@Nullable String startsAt) {
        if (startsAt == null || startsAt.isBlank()) {
            return "";
        }
        try {
            LocalDateTime dt = LocalDateTime.parse(startsAt);
            if (dt.getHour() == 0 && dt.getMinute() == 0) {
                return EVENT_DATE_ONLY.format(dt) + ", 18:00";
            }
            return EVENT_OUTPUT.format(dt);
        } catch (DateTimeParseException e) {
            return startsAt.replace('T', ' ');
        }
    }

    @StringRes
    public static int eventTypeLabelRes(@Nullable String type) {
        if (type == null) {
            return R.string.event_type_other;
        }
        switch (type.toUpperCase(Locale.ROOT)) {
            case "MEETING":
                return R.string.event_type_meeting;
            case "READING":
                return R.string.event_type_reading;
            case "WORKSHOP":
                return R.string.event_type_workshop;
            case "CHILDREN":
                return R.string.event_type_children;
            case "LECTURE":
                return R.string.event_type_lecture;
            case "FAIR":
                return R.string.event_type_fair;
            default:
                return R.string.event_type_other;
        }
    }

    @ColorRes
    public static int eventAccentColorRes(@Nullable String type) {
        if (type == null) {
            return R.color.library_primary;
        }
        switch (type.toUpperCase(Locale.ROOT)) {
            case "MEETING":
                return R.color.event_accent_meeting;
            case "READING":
                return R.color.event_accent_reading;
            case "WORKSHOP":
                return R.color.event_accent_workshop;
            case "CHILDREN":
                return R.color.event_accent_children;
            case "LECTURE":
                return R.color.event_accent_lecture;
            case "FAIR":
                return R.color.event_accent_fair;
            default:
                return R.color.library_primary;
        }
    }

    public static int eventProgressPercent(int registered, int capacity) {
        if (capacity <= 0) {
            return 0;
        }
        return Math.min(100, Math.round(registered * 100f / capacity));
    }

    @NonNull
    private static String extractInitials(@Nullable String title, @Nullable String author) {
        if (author != null && !author.isBlank()) {
            String[] parts = author.trim().split("\\s+");
            if (parts.length >= 2) {
                return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase(Locale.ROOT);
            }
            if (parts.length == 1 && !parts[0].isEmpty()) {
                return ("" + parts[0].charAt(0)).toUpperCase(Locale.ROOT);
            }
        }
        if (title != null && !title.isBlank()) {
            return ("" + title.trim().charAt(0)).toUpperCase(Locale.ROOT);
        }
        return "?";
    }
}
