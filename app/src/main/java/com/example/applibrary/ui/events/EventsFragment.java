package com.example.applibrary.ui.events;

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
import com.example.applibrary.databinding.FragmentEventsBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    private EventsViewModel viewModel;
    private EventListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var app = (LibraryApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(app.getAppContainer()))
                .get(EventsViewModel.class);

        adapter = new EventListAdapter(viewModel::toggleRegistration);
        binding.recyclerEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerEvents.setAdapter(adapter);

        viewModel.getEvents().observe(getViewLifecycleOwner(), list -> {
            boolean empty = list == null || list.isEmpty();
            binding.textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.recyclerEvents.setVisibility(empty ? View.GONE : View.VISIBLE);
            adapter.submit(list);
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progress.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
