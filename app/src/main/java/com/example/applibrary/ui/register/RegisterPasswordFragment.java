package com.example.applibrary.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.applibrary.LibraryApplication;
import com.example.applibrary.R;
import com.example.applibrary.data.remote.dto.AuthDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.databinding.FragmentRegisterPasswordBinding;
import com.google.android.material.snackbar.Snackbar;

public class RegisterPasswordFragment extends Fragment {

    private FragmentRegisterPasswordBinding binding;
    private String requestId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            requestId = getArguments().getString("requestId");
            String cardNumber = getArguments().getString("cardNumber");
            if (cardNumber != null && !cardNumber.isEmpty()) {
                binding.textCardAssigned.setText(
                        getString(R.string.payment_success, cardNumber));
            }
        }
        if (requestId == null || requestId.isEmpty()) {
            Navigation.findNavController(binding.getRoot()).navigateUp();
            return;
        }

        var reg = ((LibraryApplication) requireActivity().getApplication())
                .getAppContainer().getRegistrationRepository();
        var auth = ((LibraryApplication) requireActivity().getApplication())
                .getAppContainer().getAuthRepository();

        binding.btnComplete.setOnClickListener(v -> {
            String pass = text(binding.inputPassword);
            String confirm = text(binding.inputPasswordConfirm);
            if (pass.length() < 8) {
                Snackbar.make(binding.getRoot(), R.string.password_too_short, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirm)) {
                Snackbar.make(binding.getRoot(), R.string.password_mismatch, Snackbar.LENGTH_SHORT).show();
                return;
            }

            binding.btnComplete.setEnabled(false);
            new Thread(() -> {
                ApiResult<AuthDtos.LoginResponse> done = reg.complete(requestId, pass, confirm);
                requireActivity().runOnUiThread(() -> {
                    binding.btnComplete.setEnabled(true);
                    if (done instanceof ApiResult.Success) {
                        auth.persistSession(((ApiResult.Success<AuthDtos.LoginResponse>) done).getData());
                        Navigation.findNavController(binding.getRoot())
                                .navigate(R.id.action_password_to_main);
                    } else if (done instanceof ApiResult.Error) {
                        Snackbar.make(binding.getRoot(),
                                ((ApiResult.Error<AuthDtos.LoginResponse>) done).getMessage(),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }).start();
        });
    }

    private static String text(com.google.android.material.textfield.TextInputEditText edit) {
        return edit.getText() != null ? edit.getText().toString() : "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
