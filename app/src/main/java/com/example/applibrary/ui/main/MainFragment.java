package com.example.applibrary.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.applibrary.R;
import com.example.applibrary.databinding.FragmentMainBinding;
public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavHostFragment navHost = (NavHostFragment) getChildFragmentManager()
                .findFragmentById(R.id.main_nav_host);
        if (navHost == null) return;
        NavController navController = navHost.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        NavigationUI.setupWithNavController(binding.toolbar, navController);

        binding.bottomNav.setOnItemReselectedListener(item -> {
            // не сбрасывать стек при повторном нажатии вкладки
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
