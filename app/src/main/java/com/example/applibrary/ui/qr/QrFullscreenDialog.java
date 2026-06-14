package com.example.applibrary.ui.qr;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.applibrary.R;
import com.example.applibrary.databinding.DialogQrFullscreenBinding;
import com.example.applibrary.util.AppSettingsStorage;
import com.example.applibrary.ui.ticket.TicketInfoDialog;
import com.example.applibrary.util.QrCodeUtil;

public class QrFullscreenDialog extends DialogFragment {

    private static final String ARG_URL = "url";
    private static final String ARG_CARD = "card";
    private static final String ARG_NAME = "fullName";
    private static final String ARG_STATUS = "status";
    private static final String ARG_VALID = "validUntil";

    private DialogQrFullscreenBinding binding;
    private float previousBrightness = -1f;

    public static QrFullscreenDialog newInstance(
            String scanUrl,
            String cardNumber,
            String fullName,
            String status,
            String validUntil
    ) {
        QrFullscreenDialog dialog = new QrFullscreenDialog();
        Bundle args = new Bundle();
        args.putString(ARG_URL, scanUrl);
        args.putString(ARG_CARD, cardNumber);
        args.putString(ARG_NAME, fullName);
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
        binding = DialogQrFullscreenBinding.inflate(inflater, container, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        String url = args != null ? args.getString(ARG_URL, "") : "";
        String card = args != null ? args.getString(ARG_CARD, "") : "";
        String fullName = args != null ? args.getString(ARG_NAME, "") : "";
        String status = args != null ? args.getString(ARG_STATUS, "") : "";
        String validUntil = args != null ? args.getString(ARG_VALID, "") : "";

        if (!card.isEmpty()) {
            binding.textCard.setText(getString(R.string.card_number_format, card));
        }

        Bitmap bitmap = QrCodeUtil.encodeText(url, 1024);
        if (bitmap != null) {
            binding.imageQrZoom.setImageBitmap(bitmap);
            binding.imageQrZoom.post(binding.imageQrZoom::resetMatrix);
        }

        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.btnOpenTicket.setOnClickListener(v ->
                TicketInfoDialog.newInstance(fullName, card, status, validUntil)
                        .show(getParentFragmentManager(), "ticket_info"));
        binding.getRoot().setOnClickListener(v -> dismiss());
        binding.cardQr.setOnClickListener(v -> {});
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(0xF0000000));

        if (new AppSettingsStorage(requireContext()).isQrBrightnessBoost()) {
            WindowManager.LayoutParams lp = window.getAttributes();
            previousBrightness = lp.screenBrightness;
            lp.screenBrightness = 1f;
            window.setAttributes(lp);
        }

        WindowManager.LayoutParams flags = window.getAttributes();
        flags.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        window.setAttributes(flags);
    }

    @Override
    public void onStop() {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null && previousBrightness >= 0f) {
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.screenBrightness = previousBrightness;
            dialog.getWindow().setAttributes(lp);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
