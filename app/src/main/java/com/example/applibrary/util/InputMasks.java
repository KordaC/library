package com.example.applibrary.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class InputMasks {

    private static final Locale RU = new Locale("ru");
    private static final DateTimeFormatter API_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("d MMMM yyyy", RU);

    private InputMasks() {}

    public static void attachPhoneMask(EditText field) {
        field.addTextChangedListener(new PhoneMaskWatcher(field));
    }

    public static void attachBirthDateMask(Fragment fragment, EditText field) {
        field.addTextChangedListener(new DateMaskWatcher());
        field.setFocusable(false);
        field.setClickable(true);
        field.setOnClickListener(v -> showRussianDatePicker(fragment, field));
    }

    public static String normalizePhone(String raw) {
        if (raw == null) return "";
        String digits = raw.replaceAll("\\D", "");
        if (digits.length() == 11 && digits.startsWith("8")) {
            digits = "7" + digits.substring(1);
        }
        if (digits.length() == 10 && digits.startsWith("9")) {
            digits = "7" + digits;
        }
        if (digits.isEmpty()) return "";
        return "+" + digits;
    }

    public static String toApiDate(String displayDdMmYyyy) {
        if (displayDdMmYyyy == null || displayDdMmYyyy.isBlank()) return "";
        String digits = displayDdMmYyyy.replaceAll("\\D", "");
        if (digits.length() != 8) return "";
        String iso = digits.substring(4) + "-" + digits.substring(2, 4) + "-" + digits.substring(0, 2);
        try {
            LocalDate.parse(iso, API_DATE);
            return iso;
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    public static String formatBirthDateDisplay(String iso) {
        if (iso == null || iso.isBlank()) return "—";
        try {
            return DISPLAY_DATE.format(LocalDate.parse(iso, API_DATE));
        } catch (DateTimeParseException e) {
            return iso;
        }
    }

    private static void showRussianDatePicker(Fragment fragment, EditText field) {
        Context ruContext = russianContext(fragment.requireContext());
        LocalDate initial = parseInitialDate(field.getText() != null ? field.getText().toString() : "");

        DatePickerDialog dialog = new DatePickerDialog(
                ruContext,
                (view, year, month, dayOfMonth) -> field.setText(
                        String.format(RU, "%02d.%02d.%04d", dayOfMonth, month + 1, year)),
                initial.getYear(),
                initial.getMonthValue() - 1,
                initial.getDayOfMonth()
        );
        dialog.setTitle("Выберите дату рождения");
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        long min = LocalDate.of(1920, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        dialog.getDatePicker().setMinDate(min);
        dialog.show();
    }

    @NonNull
    private static Context russianContext(Context base) {
        Configuration config = new Configuration(base.getResources().getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(new android.os.LocaleList(RU));
        } else {
            config.locale = RU;
        }
        return base.createConfigurationContext(config);
    }

    private static LocalDate parseInitialDate(String display) {
        String api = toApiDate(display);
        if (api.isEmpty()) {
            return LocalDate.of(1995, 1, 1);
        }
        return LocalDate.parse(api, API_DATE);
    }

    private static final class PhoneMaskWatcher implements TextWatcher {
        private final EditText field;
        private boolean updating;

        PhoneMaskWatcher(EditText field) {
            this.field = field;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (updating) return;
            updating = true;
            String digits = s.toString().replaceAll("\\D", "");
            if (digits.startsWith("8")) digits = "7" + digits.substring(1);
            if (digits.length() > 11) digits = digits.substring(0, 11);
            StringBuilder formatted = new StringBuilder("+7");
            if (digits.length() > 1) {
                String rest = digits.startsWith("7") ? digits.substring(1) : digits;
                if (!rest.isEmpty()) formatted.append(" (").append(rest.substring(0, Math.min(3, rest.length())));
                if (rest.length() > 3) formatted.append(") ").append(rest.substring(3, Math.min(6, rest.length())));
                if (rest.length() > 6) formatted.append("-").append(rest.substring(6, Math.min(8, rest.length())));
                if (rest.length() > 8) formatted.append("-").append(rest.substring(8, Math.min(10, rest.length())));
            }
            field.setText(formatted.toString());
            field.setSelection(field.getText().length());
            updating = false;
        }
    }

    private static final class DateMaskWatcher implements TextWatcher {
        private boolean updating;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (updating) return;
            updating = true;
            String digits = s.toString().replaceAll("\\D", "");
            if (digits.length() > 8) digits = digits.substring(0, 8);
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                out.append(digits.charAt(i));
                if (i == 1 || i == 3) out.append('.');
            }
            s.replace(0, s.length(), out.toString());
            updating = false;
        }
    }
}
