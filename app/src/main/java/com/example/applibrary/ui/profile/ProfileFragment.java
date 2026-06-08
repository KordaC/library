package com.example.applibrary.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.applibrary.BuildConfig;
import com.example.applibrary.LibraryApplication;
import com.example.applibrary.R;
import com.example.applibrary.databinding.FragmentProfileBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.example.applibrary.util.InputMasks;
import com.google.android.material.snackbar.Snackbar;

import coil.Coil;
import coil.request.ImageRequest;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private ActivityResultLauncher<String> pickPhotoLauncher;
    private String currentCardNumber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::onPhotoPicked);
    }

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
        viewModel = new ViewModelProvider(this, new ViewModelFactory(app, container))
                .get(ProfileViewModel.class);

        if (BuildConfig.DEBUG) {
            binding.cardServerSettings.setVisibility(View.VISIBLE);
            binding.inputServerUrl.setText(container.getServerUrlStorage().getEffectiveBaseUrl());
            binding.btnSaveServer.setOnClickListener(v -> {
                String raw = text(binding.inputServerUrl);
                if (raw.isEmpty()) {
                    container.getServerUrlStorage().clear();
                } else {
                    container.getServerUrlStorage().saveBaseUrl(raw);
                }
                app.recreateAppContainer();
                Snackbar.make(binding.getRoot(), R.string.server_saved, Snackbar.LENGTH_LONG).show();
            });
        }

        viewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile == null) return;
            currentCardNumber = profile.cardNumber;
            binding.textName.setText(profile.fullName);
            binding.textAvatar.setText(initials(profile.fullName));
            binding.textCard.setText(getString(R.string.card_number_format, profile.cardNumber));
            binding.chipStatus.setText(statusLabel(profile.cardStatus));
            binding.textBirthDate.setText(InputMasks.formatBirthDateDisplay(profile.birthDate));
            binding.textEmail.setText(
                    profile.email != null && !profile.email.isEmpty() ? profile.email : "—");
            binding.textPhone.setText(
                    profile.phone != null && !profile.phone.isEmpty() ? profile.phone : "—");
            loadSavedPhoto(profile.cardNumber);
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progress.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        binding.btnChangePhoto.setOnClickListener(v -> pickPhotoLauncher.launch("image/*"));
        binding.textAvatar.setOnClickListener(v -> pickPhotoLauncher.launch("image/*"));
        binding.imageAvatar.setOnClickListener(v -> pickPhotoLauncher.launch("image/*"));

        binding.btnEditContacts.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_profile_to_edit_contacts));

        binding.cardSettings.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_profile_to_settings));

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

    private void onPhotoPicked(Uri uri) {
        if (uri == null || currentCardNumber == null || currentCardNumber.isEmpty()) return;
        var storage = ((LibraryApplication) requireActivity().getApplication())
                .getAppContainer().getProfilePhotoStorage();
        storage.save(currentCardNumber, uri);
        showPhoto(uri);
        Snackbar.make(binding.getRoot(), R.string.profile_photo_saved, Snackbar.LENGTH_SHORT).show();
    }

    private void loadSavedPhoto(String cardNumber) {
        Uri uri = ((LibraryApplication) requireActivity().getApplication())
                .getAppContainer().getProfilePhotoStorage().get(cardNumber);
        if (uri != null) {
            showPhoto(uri);
        } else {
            binding.imageAvatar.setVisibility(View.GONE);
            binding.textAvatar.setVisibility(View.VISIBLE);
        }
    }

    private void showPhoto(Uri uri) {
        binding.imageAvatar.setVisibility(View.VISIBLE);
        binding.textAvatar.setVisibility(View.GONE);
        Coil.imageLoader(requireContext()).enqueue(
                new ImageRequest.Builder(requireContext())
                        .data(uri)
                        .target(binding.imageAvatar)
                        .crossfade(true)
                        .build());
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
