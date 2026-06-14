package com.example.applibrary.ui.ticket;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.applibrary.R;
import com.example.applibrary.databinding.DialogTicketInfoBinding;

public class TicketInfoDialog extends DialogFragment {

    private static final String ARG_NAME = "fullName";
    private static final String ARG_CARD = "cardNumber";
    private static final String ARG_STATUS = "status";
    private static final String ARG_VALID = "validUntil";

    public static TicketInfoDialog newInstance(
            String fullName,
            String cardNumber,
            String status,
            String validUntil
    ) {
        TicketInfoDialog dialog = new TicketInfoDialog();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, fullName);
        args.putString(ARG_CARD, cardNumber);
        args.putString(ARG_STATUS, status);
        args.putString(ARG_VALID, validUntil);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Theme_AppLibrary_QrFullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DialogTicketInfoBinding binding = DialogTicketInfoBinding.inflate(inflater, container, false);
        Bundle args = getArguments();
        if (args != null) {
            binding.textFullName.setText(nullToDash(args.getString(ARG_NAME)));
            String card = args.getString(ARG_CARD, "");
            binding.textCardNumber.setText(formatCardNumber(card));
            String status = args.getString(ARG_STATUS);
            binding.textStatus.setText(statusLabel(status));
            styleStatusBadge(binding.textStatus, status);
            String validUntil = args.getString(ARG_VALID);
            if (validUntil != null && !validUntil.isEmpty()) {
                binding.textValidUntil.setText(getString(R.string.ticket_card_valid_format, validUntil));
            } else {
                binding.textValidUntil.setText("—");
            }
        }
        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.getRoot().setOnClickListener(v -> dismiss());
        binding.cardPlastic.setOnClickListener(v -> {});
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(0xCC000000));
    }

    private void styleStatusBadge(@NonNull TextView badge, @Nullable String status) {
        if ("BLOCKED".equals(status)) {
            badge.setBackgroundResource(R.drawable.bg_chip_unavailable);
            badge.setTextColor(ContextCompat.getColor(requireContext(), R.color.library_on_surface_variant));
        } else {
            badge.setBackgroundResource(R.drawable.bg_chip_available);
            badge.setTextColor(ContextCompat.getColor(requireContext(), R.color.library_on_tertiary_container));
        }
    }

    private String statusLabel(@Nullable String status) {
        if ("ACTIVE".equals(status)) return getString(R.string.status_active);
        if ("BLOCKED".equals(status)) return getString(R.string.status_blocked);
        return status != null && !status.isEmpty() ? status : "—";
    }

    @NonNull
    private String formatCardNumber(@Nullable String card) {
        if (card == null || card.isBlank()) {
            return "—";
        }
        String digits = card.replaceAll("\\s+", "");
        if (digits.length() == 5) {
            return digits.substring(0, 2) + " " + digits.substring(2);
        }
        return digits;
    }

    private static String nullToDash(@Nullable String value) {
        return value != null && !value.isEmpty() ? value : "—";
    }
}
