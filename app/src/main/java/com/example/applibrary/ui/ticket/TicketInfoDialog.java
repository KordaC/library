package com.example.applibrary.ui.ticket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        setStyle(STYLE_NORMAL, R.style.Theme_AppLibrary);
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
            binding.textCardNumber.setText(card.isEmpty()
                    ? "—"
                    : getString(R.string.card_number_format, card));
            binding.textStatus.setText(statusLabel(args.getString(ARG_STATUS)));
            binding.textValidUntil.setText(nullToDash(args.getString(ARG_VALID)));
        }
        binding.btnClose.setOnClickListener(v -> dismiss());
        return binding.getRoot();
    }

    private String statusLabel(@Nullable String status) {
        if ("ACTIVE".equals(status)) return getString(R.string.status_active);
        if ("BLOCKED".equals(status)) return getString(R.string.status_blocked);
        return status != null && !status.isEmpty() ? status : "—";
    }

    private static String nullToDash(@Nullable String value) {
        return value != null && !value.isEmpty() ? value : "—";
    }
}
