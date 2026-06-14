package com.example.applibrary.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.applibrary.BuildConfig;
import com.example.applibrary.LibraryApplication;
import com.example.applibrary.MainActivity;
import com.example.applibrary.R;
import com.example.applibrary.databinding.FragmentLoginBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.example.applibrary.util.InputMasks;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var app = (LibraryApplication) requireActivity().getApplication();
        var container = app.getAppContainer();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(app, container))
                .get(LoginViewModel.class);

        binding.textServer.setVisibility(View.VISIBLE);
        binding.textServer.setText(getString(R.string.login_server, viewModel.getServerUrl()));

        if (BuildConfig.DEBUG) {
            binding.layoutServerUrl.setVisibility(View.VISIBLE);
            binding.inputServerUrl.setText(container.getServerUrlStorage().getEffectiveBaseUrl());
        }

        viewModel.getStatus().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                binding.textStatus.setVisibility(View.VISIBLE);
                binding.textStatus.setText(msg);
            } else {
                binding.textStatus.setVisibility(View.GONE);
            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (BuildConfig.DEBUG) {
                String serverRaw = binding.inputServerUrl.getText() != null
                        ? binding.inputServerUrl.getText().toString().trim() : "";
                if (!serverRaw.isEmpty()) {
                    app.getAppContainer().getServerUrlStorage().saveBaseUrl(serverRaw);
                    app.recreateAppContainer();
                }
            }
            String login = binding.inputLogin.getText() != null
                    ? binding.inputLogin.getText().toString().trim() : "";
            if (!login.contains("@") && !login.matches("\\d{5}")) {
                login = InputMasks.normalizePhone(login);
            }
            String pass = binding.inputPassword.getText() != null
                    ? binding.inputPassword.getText().toString() : "";
            viewModel.login(login, pass);
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                binding.btnLogin.setEnabled(!Boolean.TRUE.equals(loading)));

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccess().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                if (requireActivity() instanceof MainActivity) {
                    ((MainActivity) requireActivity()).requestNotificationPermissionIfNeeded();
                }
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_login_to_main);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
