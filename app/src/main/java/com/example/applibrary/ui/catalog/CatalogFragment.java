package com.example.applibrary.ui.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.applibrary.LibraryApplication;
import com.example.applibrary.R;
import com.example.applibrary.data.remote.dto.CatalogDtos;
import com.example.applibrary.databinding.FragmentCatalogBinding;
import com.example.applibrary.ui.ViewModelFactory;
import com.example.applibrary.ui.util.ListCardUi;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        binding.recyclerBooks.setLayoutManager(new GridLayoutManager(requireContext(), 2));
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
            if (!isAdded()) return;
            genreItems.clear();
            CatalogDtos.GenreItem all = new CatalogDtos.GenreItem();
            all.id = null;
            all.name = getString(R.string.all_genres);
            genreItems.add(all);
            if (genres != null) genreItems.addAll(genres);
            List<String> names = new ArrayList<>();
            for (CatalogDtos.GenreItem g : genreItems) {
                names.add(g.name != null ? g.name : "");
            }
            ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1, names);
            binding.dropdownGenre.setAdapter(genreAdapter);
            if (!genreItems.isEmpty()) {
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
            if (detail == null || !isAdded()) return;
            showBookDetailSheet(detail);
            viewModel.clearBookDetail();
        });

        viewModel.loadGenres();
        viewModel.search("", null);
    }

    private void showBookDetailSheet(CatalogDtos.BookDetail detail) {
        View sheet = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_book_detail, null, false);

        TextView title = sheet.findViewById(R.id.detail_title);
        TextView author = sheet.findViewById(R.id.detail_author);
        TextView meta = sheet.findViewById(R.id.detail_meta);
        TextView genres = sheet.findViewById(R.id.detail_genres);
        TextView description = sheet.findViewById(R.id.detail_description);
        View coverContainer = sheet.findViewById(R.id.detail_cover_container);
        ImageView coverImage = sheet.findViewById(R.id.detail_image_cover);
        TextView coverInitial = sheet.findViewById(R.id.detail_cover_initial);

        title.setText(detail.title != null ? detail.title : "");
        author.setText(detail.authorName != null ? detail.authorName : "");
        meta.setText(getString(R.string.book_detail_meta, detail.availableCount, detail.totalCopies));
        String genreText = formatGenres(detail.genres);
        if (genreText.isEmpty()) {
            genres.setVisibility(View.GONE);
        } else {
            genres.setVisibility(View.VISIBLE);
            genres.setText(getString(R.string.book_detail_genres, genreText));
        }
        if (detail.description != null && !detail.description.isBlank()) {
            description.setText(detail.description);
        } else {
            description.setVisibility(View.GONE);
        }

        TextView yearView = sheet.findViewById(R.id.detail_text_year);
        ListCardUi.bindBookCoverImage(
                coverImage,
                coverContainer,
                coverInitial,
                yearView,
                detail.coverImageUrl,
                detail.title,
                detail.authorName,
                detail.publicationYear);

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(sheet);
        dialog.show();
    }

    private void doSearch() {
        String q = binding.inputSearch.getText() != null
                ? binding.inputSearch.getText().toString().trim() : "";
        viewModel.search(q, selectedGenreId);
    }

    private void showBookDetail(CatalogDtos.BookListItem book) {
        if (book.id != null) {
            viewModel.loadBookDetail(book.id);
        }
    }

    private static String formatGenres(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            return "";
        }
        return genres.stream()
                .filter(g -> g != null && !g.isBlank())
                .collect(Collectors.joining(", "));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
