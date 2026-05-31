package com.example.applibrary.ui.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.applibrary.R;
import com.example.applibrary.databinding.FragmentWelcomeBinding;

public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.btnLogin.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_welcome_to_login));
        binding.btnLinkCard.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_welcome_to_link_card));
        binding.btnRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_welcome_to_register));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
