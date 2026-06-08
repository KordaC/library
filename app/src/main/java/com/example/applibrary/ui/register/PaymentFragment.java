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
import com.example.applibrary.databinding.FragmentPaymentBinding;
import com.google.android.material.snackbar.Snackbar;

public class PaymentFragment extends Fragment {

    private FragmentPaymentBinding binding;
    private String requestId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            requestId = getArguments().getString("requestId");
        }
        if (requestId == null || requestId.isEmpty()) {
            Navigation.findNavController(binding.getRoot()).navigateUp();
            return;
        }

        var reg = ((LibraryApplication) requireActivity().getApplication())
                .getAppContainer().getRegistrationRepository();

        binding.btnMockPay.setOnClickListener(v -> {
            binding.btnMockPay.setEnabled(false);
            binding.progress.setVisibility(View.VISIBLE);
            new Thread(() -> {
                ApiResult<RegistrationDtos.MockPayResponse> pay = reg.mockPay(requestId);
                requireActivity().runOnUiThread(() -> {
                    binding.btnMockPay.setEnabled(true);
                    binding.progress.setVisibility(View.GONE);
                    if (pay instanceof ApiResult.Success) {
                        RegistrationDtos.MockPayResponse data =
                                ((ApiResult.Success<RegistrationDtos.MockPayResponse>) pay).getData();
                        Bundle args = new Bundle();
                        args.putString("requestId", requestId);
                        args.putString("cardNumber", data.cardNumber);
                        Navigation.findNavController(binding.getRoot())
                                .navigate(R.id.action_payment_to_password, args);
                    } else if (pay instanceof ApiResult.Error) {
                        Snackbar.make(binding.getRoot(),
                                ((ApiResult.Error<RegistrationDtos.MockPayResponse>) pay).getMessage(),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }).start();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
