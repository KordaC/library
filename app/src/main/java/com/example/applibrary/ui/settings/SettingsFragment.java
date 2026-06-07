package com.example.applibrary.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.applibrary.R;
import com.example.applibrary.databinding.FragmentSettingsBinding;
import com.example.applibrary.util.AppSettingsApplier;
import com.example.applibrary.util.AppSettingsStorage;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private AppSettingsStorage settings;
    private boolean suppressCallbacks;

    private final String[] themeValues = {
            AppSettingsStorage.THEME_SYSTEM,
            AppSettingsStorage.THEME_LIGHT,
            AppSettingsStorage.THEME_DARK
    };

    private final String[] langValues = {
            AppSettingsStorage.LANG_SYSTEM,
            AppSettingsStorage.LANG_RU,
            AppSettingsStorage.LANG_EN
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        settings = new AppSettingsStorage(requireContext());
        suppressCallbacks = true;

        String[] themeLabels = {
                getString(R.string.settings_theme_system),
                getString(R.string.settings_theme_light),
                getString(R.string.settings_theme_dark)
        };
        String[] langLabels = {
                getString(R.string.settings_lang_system),
                getString(R.string.settings_lang_ru),
                getString(R.string.settings_lang_en)
        };

        binding.dropdownTheme.setAdapter(new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, themeLabels));
        binding.dropdownLanguage.setAdapter(new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, langLabels));

        binding.dropdownTheme.setText(labelFor(themeLabels, themeValues, settings.getTheme()), false);
        binding.dropdownLanguage.setText(labelFor(langLabels, langValues, settings.getLanguage()), false);
        binding.switchQrBrightness.setChecked(settings.isQrBrightnessBoost());

        binding.dropdownTheme.setOnItemClickListener((parent, v, position, id) -> {
            if (suppressCallbacks) return;
            settings.setTheme(themeValues[position]);
            restartForSettings();
        });
        binding.dropdownLanguage.setOnItemClickListener((parent, v, position, id) -> {
            if (suppressCallbacks) return;
            settings.setLanguage(langValues[position]);
            restartForSettings();
        });
        binding.switchQrBrightness.setOnCheckedChangeListener((button, checked) -> {
            if (suppressCallbacks) return;
            settings.setQrBrightnessBoost(checked);
        });

        suppressCallbacks = false;
    }

    private void restartForSettings() {
        AppSettingsApplier.apply(requireContext());
        requireActivity().recreate();
    }

    private static String labelFor(String[] labels, String[] values, String current) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(current)) {
                return labels[i];
            }
        }
        return labels[0];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
