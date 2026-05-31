package com.example.applibrary.ui.link;

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
import com.example.applibrary.databinding.FragmentLinkCardBinding;
import com.google.android.material.snackbar.Snackbar;

public class LinkCardFragment extends Fragment {

    private FragmentLinkCardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLinkCardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var repo = ((LibraryApplication) requireActivity().getApplication())
                .getAppContainer().getRegistrationRepository();
        var auth = ((LibraryApplication) requireActivity().getApplication())
                .getAppContainer().getAuthRepository();

        binding.btnSubmit.setOnClickListener(v -> {
            String card = text(binding.inputCard);
            String birth = text(binding.inputBirth);
            String pass = text(binding.inputPassword);
            String confirm = text(binding.inputPasswordConfirm);
            binding.btnSubmit.setEnabled(false);

            new Thread(() -> {
                ApiResult<?> verify = repo.verifyCard(card);
                if (verify instanceof ApiResult.Error) {
                    showError(((ApiResult.Error<?>) verify).getMessage());
                    return;
                }
                ApiResult<AuthDtos.LoginResponse> link = repo.linkCard(card, birth, pass, confirm);
                if (link instanceof ApiResult.Success) {
                    auth.persistSession(((ApiResult.Success<AuthDtos.LoginResponse>) link).getData());
                    requireActivity().runOnUiThread(() ->
                            Navigation.findNavController(binding.getRoot())
                                    .navigate(R.id.action_link_to_main));
                } else if (link instanceof ApiResult.Error) {
                    showError(((ApiResult.Error<AuthDtos.LoginResponse>) link).getMessage());
                }
            }).start();
        });
    }

    private void showError(String msg) {
        requireActivity().runOnUiThread(() -> {
            binding.btnSubmit.setEnabled(true);
            Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
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
