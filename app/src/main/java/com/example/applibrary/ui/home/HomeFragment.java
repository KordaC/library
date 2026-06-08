package com.example.applibrary.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.applibrary.LibraryApplication;
import com.example.applibrary.R;
import com.example.applibrary.databinding.FragmentHomeBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.example.applibrary.ui.qr.QrFullscreenDialog;
import com.example.applibrary.util.BrowserUtil;
import com.example.applibrary.util.InputMasks;
import com.example.applibrary.util.QrCodeUtil;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private String lastTicketUrl;
    private String lastFullName = "";
    private String lastCardNumber = "";
    private String lastCardStatus = "";
    private String lastValidUntil = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var app = (LibraryApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(app, app.getAppContainer()))
                .get(HomeViewModel.class);

        viewModel.getDashboard().observe(getViewLifecycleOwner(), dash -> {
            if (dash == null) return;
            if (dash.user != null && dash.user.fullName != null) {
                lastFullName = dash.user.fullName;
                binding.textName.setText(dash.user.fullName);
            }
            if (dash.card != null) {
                lastCardNumber = dash.card.number != null ? dash.card.number : "";
                lastCardStatus = dash.card.status != null ? dash.card.status : "";
                binding.textCardNumber.setText(getString(R.string.card_number_format, dash.card.number));
                binding.chipCardStatus.setText(statusLabel(dash.card.status));
            }
            if (dash.loans != null) {
                binding.textActiveLoans.setText(String.valueOf(dash.loans.activeCount));
                binding.textOverdueLoans.setText(String.valueOf(dash.loans.overdueCount));
            }
            if (dash.notifications != null && !dash.notifications.isEmpty()) {
                var n = dash.notifications.get(0);
                binding.textNotification.setText(n.title + ": " + n.body);
            }
        });

        viewModel.getQrScanUrl().observe(getViewLifecycleOwner(), scanUrl -> {
            lastTicketUrl = scanUrl;
            if (scanUrl == null || scanUrl.isEmpty()) {
                binding.imageQr.setImageDrawable(null);
                binding.btnOpenTicket.setEnabled(false);
                return;
            }
            binding.btnOpenTicket.setEnabled(true);
            var bitmap = QrCodeUtil.encodeText(scanUrl, 512);
            if (bitmap != null) {
                binding.imageQr.setImageBitmap(bitmap);
            } else {
                binding.imageQr.setImageDrawable(null);
                Snackbar.make(binding.getRoot(), R.string.qr_generate_error, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getQrPayload().observe(getViewLifecycleOwner(), payload -> {
            if (payload != null && payload.exp > 0) {
                lastValidUntil = InputMasks.formatQrValidUntil(payload.exp);
            }
        });

        binding.imageQr.setOnClickListener(v -> openQrFullscreen());
        binding.btnOpenTicket.setOnClickListener(v -> openTicketInBrowser());

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.load();
    }

    private void openQrFullscreen() {
        if (lastTicketUrl == null || lastTicketUrl.isEmpty()) {
            return;
        }
        QrFullscreenDialog.newInstance(
                        lastTicketUrl,
                        lastCardNumber,
                        lastFullName,
                        lastCardStatus,
                        lastValidUntil)
                .show(getParentFragmentManager(), "qr_fullscreen");
    }

    private void openTicketInBrowser() {
        if (lastTicketUrl == null || lastTicketUrl.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.ticket_url_missing, Snackbar.LENGTH_SHORT).show();
            return;
        }
        BrowserUtil.openUrl(requireContext(), lastTicketUrl);
    }

    private String statusLabel(String status) {
        if ("ACTIVE".equals(status)) return getString(R.string.status_active);
        if ("BLOCKED".equals(status)) return getString(R.string.status_blocked);
        return status;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.load();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
