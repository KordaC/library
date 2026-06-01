package com.example.applibrary.ui.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.applibrary.LibraryApplication;
import com.example.applibrary.R;
import com.example.applibrary.data.remote.dto.CatalogDtos;
import com.example.applibrary.databinding.FragmentCatalogBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {

    private FragmentCatalogBinding binding;
    private CatalogViewModel viewModel;
    private BookListAdapter adapter;
    private final List<CatalogDtos.GenreItem> genreItems = new ArrayList<>();
    private String selectedGenreId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var app = (LibraryApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(app, app.getAppContainer()))
                .get(CatalogViewModel.class);

        adapter = new BookListAdapter(this::showBookDetail);
        binding.recyclerBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBooks.setAdapter(adapter);

        viewModel.getBooks().observe(getViewLifecycleOwner(), adapter::submit);
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progress.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE));
        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getGenres().observe(getViewLifecycleOwner(), genres -> {
            genreItems.clear();
            CatalogDtos.GenreItem all = new CatalogDtos.GenreItem();
            all.id = null;
            all.name = getString(R.string.all_genres);
            genreItems.add(all);
            if (genres != null) genreItems.addAll(genres);
            ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1,
                    genreItems.stream().map(g -> g.name).toList());
            binding.dropdownGenre.setAdapter(genreAdapter);
            if (genreItems.size() > 0) {
                binding.dropdownGenre.setText(genreItems.get(0).name, false);
                selectedGenreId = genreItems.get(0).id;
            }
        });

        binding.dropdownGenre.setOnItemClickListener((parent, v, position, id) -> {
            selectedGenreId = genreItems.get(position).id;
            doSearch();
        });

        binding.inputSearch.setOnEditorActionListener((v, actionId, event) -> {
            doSearch();
            return true;
        });

        viewModel.getBookDetail().observe(getViewLifecycleOwner(), detail -> {
            if (detail == null) return;
            String genres = detail.genres != null ? String.join(", ", detail.genres) : "";
            String message = getString(R.string.book_detail_message,
                    detail.authorName != null ? detail.authorName : "",
                    detail.description != null ? detail.description : "",
                    detail.availableCount,
                    detail.totalCopies,
                    genres);
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(detail.title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            viewModel.clearBookDetail();
        });

        viewModel.loadGenres();
        viewModel.search("", null);
    }

    private void doSearch() {
        String q = binding.inputSearch.getText() != null
                ? binding.inputSearch.getText().toString().trim() : "";
        viewModel.search(q, selectedGenreId);
    }

    private void showBookDetail(CatalogDtos.BookListItem book) {
        viewModel.loadBookDetail(book.id);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
