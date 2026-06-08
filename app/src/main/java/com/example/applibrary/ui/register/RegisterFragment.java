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
import com.example.applibrary.data.remote.dto.RegistrationDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.databinding.FragmentRegisterBinding;
import com.example.applibrary.util.InputMasks;
import com.google.android.material.snackbar.Snackbar;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

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

        InputMasks.attachPhoneMask(binding.inputPhone);
        InputMasks.attachBirthDateMask(this, binding.inputBirth);
        binding.layoutBirth.setEndIconOnClickListener(v -> binding.inputBirth.performClick());

        binding.btnCreate.setOnClickListener(v -> {
            String lastName = text(binding.inputLastName);
            String firstName = text(binding.inputFirstName);
            String birthDisplay = text(binding.inputBirth);
            String birthApi = InputMasks.toApiDate(birthDisplay);
            String phone = InputMasks.normalizePhone(text(binding.inputPhone));
            String email = text(binding.inputEmail);

            if (lastName.isEmpty() || firstName.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.name_required, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (birthApi.isEmpty()) {
                Snackbar.make(binding.getRoot(), R.string.birth_date_invalid, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (phone.length() < 12) {
                Snackbar.make(binding.getRoot(), R.string.phone_invalid, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                email = "user" + System.currentTimeMillis() + "@test.local";
            }

            RegistrationDtos.NewRegistrationRequest req = new RegistrationDtos.NewRegistrationRequest();
            req.lastName = lastName;
            req.firstName = firstName;
            req.birthDate = birthApi;
            req.passportSeries = "1234";
            req.passportNumber = "567890";
            req.phone = phone;
            req.email = email;

            binding.btnCreate.setEnabled(false);
            new Thread(() -> {
                ApiResult<RegistrationDtos.NewRegistrationResponse> result = reg.createRegistration(req);
                requireActivity().runOnUiThread(() -> {
                    binding.btnCreate.setEnabled(true);
                    if (result instanceof ApiResult.Success) {
                        String requestId = ((ApiResult.Success<RegistrationDtos.NewRegistrationResponse>) result)
                                .getData().requestId;
                        Bundle args = new Bundle();
                        args.putString("requestId", requestId);
                        Navigation.findNavController(binding.getRoot())
                                .navigate(R.id.action_register_to_payment, args);
                    } else if (result instanceof ApiResult.Error) {
                        Snackbar.make(binding.getRoot(),
                                ((ApiResult.Error<RegistrationDtos.NewRegistrationResponse>) result).getMessage(),
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
