package com.example.applibrary.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.applibrary.LibraryApplication;
import com.example.applibrary.R;
import com.example.applibrary.databinding.FragmentProfileBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var app = (LibraryApplication) requireActivity().getApplication();
        var container = app.getAppContainer();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(container))
                .get(ProfileViewModel.class);

        viewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile == null) return;
            binding.textName.setText(profile.fullName);
            binding.textAvatar.setText(initials(profile.fullName));
            binding.textCard.setText(getString(R.string.card_number_format, profile.cardNumber));
            binding.chipStatus.setText(statusLabel(profile.cardStatus));
            binding.textBirthDate.setText(
                    profile.birthDate != null && !profile.birthDate.isEmpty()
                            ? profile.birthDate : "—");
            binding.textAddress.setText(
                    profile.address != null && !profile.address.isEmpty()
                            ? profile.address : "—");
            binding.inputEmail.setText(profile.email != null ? profile.email : "");
            binding.inputPhone.setText(profile.phone != null ? profile.phone : "");
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progress.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), msg -> {
            if ("saved".equals(msg)) {
                Snackbar.make(binding.getRoot(), R.string.profile_saved, Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.btnSave.setOnClickListener(v -> viewModel.save(
                text(binding.inputEmail), text(binding.inputPhone)));

        binding.btnLogout.setOnClickListener(v -> {
            container.getAuthRepository().logout();
            NavHostFragment navHost = (NavHostFragment) requireActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.nav_host);
            if (navHost != null) {
                NavController nav = navHost.getNavController();
                nav.navigate(R.id.welcomeFragment, null,
                        new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
            }
        });

        viewModel.load();
    }

    private static String initials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length && i < 2; i++) {
            if (!parts[i].isEmpty()) {
                sb.append(parts[i].charAt(0));
            }
        }
        return sb.length() > 0 ? sb.toString().toUpperCase() : "?";
    }

    private String statusLabel(String status) {
        if ("ACTIVE".equals(status)) return getString(R.string.status_active);
        if ("BLOCKED".equals(status)) return getString(R.string.status_blocked);
        return status != null ? status : "";
    }

    private static String text(com.google.android.material.textfield.TextInputEditText edit) {
        return edit.getText() != null ? edit.getText().toString().trim() : "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
