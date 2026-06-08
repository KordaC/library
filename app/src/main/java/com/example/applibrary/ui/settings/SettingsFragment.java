package com.example.applibrary.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private final int[] themeRadioIds = {
            R.id.radio_theme_system,
            R.id.radio_theme_light,
            R.id.radio_theme_dark
    };

    private final String[] langValues = {
            AppSettingsStorage.LANG_SYSTEM,
            AppSettingsStorage.LANG_RU,
            AppSettingsStorage.LANG_EN
    };

    private final int[] langRadioIds = {
            R.id.radio_lang_system,
            R.id.radio_lang_ru,
            R.id.radio_lang_en
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

        selectRadio(binding.groupTheme, themeRadioIds, themeValues, settings.getTheme());
        selectRadio(binding.groupLanguage, langRadioIds, langValues, settings.getLanguage());
        binding.switchQrBrightness.setChecked(settings.isQrBrightnessBoost());

        binding.groupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (suppressCallbacks) return;
            String value = valueForRadio(themeRadioIds, themeValues, checkedId);
            if (value != null && !value.equals(settings.getTheme())) {
                settings.setTheme(value);
                restartForSettings();
            }
        });
        binding.groupLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            if (suppressCallbacks) return;
            String value = valueForRadio(langRadioIds, langValues, checkedId);
            if (value != null && !value.equals(settings.getLanguage())) {
                settings.setLanguage(value);
                restartForSettings();
            }
        });
        binding.switchQrBrightness.setOnCheckedChangeListener((button, checked) -> {
            if (suppressCallbacks) return;
            settings.setQrBrightnessBoost(checked);
        });

        suppressCallbacks = false;
    }

    private void selectRadio(android.widget.RadioGroup group, int[] radioIds, String[] values, String current) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(current)) {
                group.check(radioIds[i]);
                return;
            }
        }
        group.check(radioIds[0]);
    }

    private static String valueForRadio(int[] radioIds, String[] values, int checkedId) {
        for (int i = 0; i < radioIds.length; i++) {
            if (radioIds[i] == checkedId) {
                return values[i];
            }
        }
        return null;
    }

    private void restartForSettings() {
        AppSettingsApplier.apply(requireContext());
        requireActivity().recreate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
