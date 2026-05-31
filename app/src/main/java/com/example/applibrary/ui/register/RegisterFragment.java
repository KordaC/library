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
import com.example.applibrary.data.remote.dto.RegistrationDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.databinding.FragmentRegisterBinding;
import com.google.android.material.snackbar.Snackbar;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private String requestId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var reg = ((LibraryApplication) requireActivity().getApplication())
                .getAppContainer().getRegistrationRepository();
        var auth = ((LibraryApplication) requireActivity().getApplication())
                .getAppContainer().getAuthRepository();

        binding.btnCreate.setOnClickListener(v -> {
            RegistrationDtos.NewRegistrationRequest req = new RegistrationDtos.NewRegistrationRequest();
            req.lastName = text(binding.inputLastName);
            req.firstName = text(binding.inputFirstName);
            req.birthDate = text(binding.inputBirth);
            req.passportSeries = "1234";
            req.passportNumber = "567890";
            req.address = "г. Москва";
            req.phone = text(binding.inputPhone);
            req.email = text(binding.inputEmail);
            if (req.email.isEmpty()) {
                req.email = "user" + System.currentTimeMillis() + "@test.local";
            }

            binding.btnCreate.setEnabled(false);
            new Thread(() -> {
                ApiResult<RegistrationDtos.NewRegistrationResponse> result = reg.createRegistration(req);
                requireActivity().runOnUiThread(() -> {
                    binding.btnCreate.setEnabled(true);
                    if (result instanceof ApiResult.Success) {
                        requestId = ((ApiResult.Success<RegistrationDtos.NewRegistrationResponse>) result)
                                .getData().requestId;
                        binding.paymentSection.setVisibility(View.VISIBLE);
                        binding.textPaymentInfo.setText(getString(R.string.payment_request_created));
                    } else if (result instanceof ApiResult.Error) {
                        Snackbar.make(binding.getRoot(),
                                ((ApiResult.Error<RegistrationDtos.NewRegistrationResponse>) result).getMessage(),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }).start();
        });

        binding.btnMockPay.setOnClickListener(v -> {
            if (requestId == null) return;
            binding.btnMockPay.setEnabled(false);
            new Thread(() -> {
                ApiResult<RegistrationDtos.MockPayResponse> pay = reg.mockPay(requestId);
                requireActivity().runOnUiThread(() -> {
                    binding.btnMockPay.setEnabled(true);
                    if (pay instanceof ApiResult.Success) {
                        RegistrationDtos.MockPayResponse data =
                                ((ApiResult.Success<RegistrationDtos.MockPayResponse>) pay).getData();
                        binding.passwordSection.setVisibility(View.VISIBLE);
                        binding.textPaymentInfo.setText(getString(
                                R.string.payment_success, data.cardNumber));
                    } else if (pay instanceof ApiResult.Error) {
                        Snackbar.make(binding.getRoot(),
                                ((ApiResult.Error<RegistrationDtos.MockPayResponse>) pay).getMessage(),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }).start();
        });

        binding.btnComplete.setOnClickListener(v -> {
            if (requestId == null) return;
            String pass = text(binding.inputPassword);
            binding.btnComplete.setEnabled(false);
            new Thread(() -> {
                ApiResult<AuthDtos.LoginResponse> done = reg.complete(requestId, pass, pass);
                requireActivity().runOnUiThread(() -> {
                    binding.btnComplete.setEnabled(true);
                    if (done instanceof ApiResult.Success) {
                        auth.persistSession(((ApiResult.Success<AuthDtos.LoginResponse>) done).getData());
                        Navigation.findNavController(binding.getRoot())
                                .navigate(R.id.action_register_to_main);
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
        return edit.getText() != null ? edit.getText().toString().trim() : "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
