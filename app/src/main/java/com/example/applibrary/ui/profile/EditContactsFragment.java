package com.example.applibrary.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.applibrary.LibraryApplication;
import com.example.applibrary.R;
import com.example.applibrary.databinding.FragmentEditContactsBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

public class EditContactsFragment extends Fragment {

    private FragmentEditContactsBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEditContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var app = (LibraryApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(app, app.getAppContainer()))
                .get(ProfileViewModel.class);

        viewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile == null) return;
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
                NavHostFragment.findNavController(this).navigateUp();
            }
        });

        binding.btnSave.setOnClickListener(v -> viewModel.save(
                text(binding.inputEmail), text(binding.inputPhone)));

        viewModel.load();
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
