package com.example.applibrary.ui.loans;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.applibrary.LibraryApplication;
import com.example.applibrary.R;
import com.example.applibrary.databinding.FragmentLoansBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class LoansFragment extends Fragment {

    private FragmentLoansBinding binding;
    private LoansViewModel viewModel;
    private LoanListAdapter adapter;
    private boolean showingActive = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoansBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var app = (LibraryApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(app, app.getAppContainer()))
                .get(LoansViewModel.class);

        adapter = new LoanListAdapter(true, loan -> viewModel.renew(loan.id));
        binding.recyclerLoans.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerLoans.setAdapter(adapter);

        binding.toggleLoans.check(R.id.btn_active);
        binding.toggleLoans.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                selectTab(checkedId == R.id.btn_active);
            }
        });

        viewModel.getActiveLoans().observe(getViewLifecycleOwner(), list -> {
            if (showingActive) updateListDisplay(list);
        });
        viewModel.getHistoryLoans().observe(getViewLifecycleOwner(), list -> {
            if (!showingActive) updateListDisplay(list);
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progress.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.loadAll();
    }

    private void selectTab(boolean active) {
        showingActive = active;
        updateList();
    }

    private void updateList() {
        if (showingActive) {
            updateListDisplay(viewModel.getActiveLoans().getValue());
        } else {
            updateListDisplay(viewModel.getHistoryLoans().getValue());
        }
    }

    private void updateListDisplay(List<com.example.applibrary.data.remote.dto.LoanDtos.LoanItem> list) {
        adapter = new LoanListAdapter(showingActive, loan -> viewModel.renew(loan.id));
        binding.recyclerLoans.setAdapter(adapter);
        adapter.submit(list);
        boolean empty = list == null || list.isEmpty();
        binding.textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.textEmpty.setText(showingActive
                ? getString(R.string.loans_empty_active)
                : getString(R.string.loans_empty_history));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
