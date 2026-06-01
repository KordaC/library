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
import com.example.applibrary.databinding.FragmentHomeBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.example.applibrary.util.QrCodeUtil;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

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
            if (dash.user != null) {
                binding.textName.setText(dash.user.fullName);
            }
            if (dash.card != null) {
                binding.textCardNumber.setText(getString(
                        com.example.applibrary.R.string.card_number_format, dash.card.number));
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
            if (scanUrl == null || scanUrl.isEmpty()) {
                binding.imageQr.setImageDrawable(null);
                return;
            }
            var bitmap = QrCodeUtil.encodeText(scanUrl, 512);
            if (bitmap != null) {
                binding.imageQr.setImageBitmap(bitmap);
            } else {
                binding.imageQr.setImageDrawable(null);
                Snackbar.make(binding.getRoot(),
                        com.example.applibrary.R.string.qr_generate_error,
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.load();
    }

    private String statusLabel(String status) {
        if ("ACTIVE".equals(status)) return getString(com.example.applibrary.R.string.status_active);
        if ("BLOCKED".equals(status)) return getString(com.example.applibrary.R.string.status_blocked);
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
