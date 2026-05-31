package com.example.applibrary.ui.catalog;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applibrary.data.remote.dto.CatalogDtos;
import com.example.applibrary.databinding.ItemBookBinding;

import java.util.ArrayList;
import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.Holder> {

    public interface OnBookClickListener {
        void onBookClick(CatalogDtos.BookListItem book);
    }

    private final List<CatalogDtos.BookListItem> items = new ArrayList<>();
    private final OnBookClickListener listener;

    public BookListAdapter(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<CatalogDtos.BookListItem> books) {
        items.clear();
        if (books != null) {
            items.addAll(books);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookBinding binding = ItemBookBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final ItemBookBinding binding;

        Holder(ItemBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CatalogDtos.BookListItem book) {
            binding.textTitle.setText(book.title);
            binding.textAuthor.setText(book.authorName != null ? book.authorName : "");
            String avail = book.availableCount > 0
                    ? binding.getRoot().getContext().getString(
                    com.example.applibrary.R.string.copies_available, book.availableCount)
                    : binding.getRoot().getContext().getString(
                    com.example.applibrary.R.string.copies_unavailable);
            binding.textAvailability.setText(avail);
            boolean available = book.availableCount > 0;
            binding.textAvailability.setBackgroundResource(available
                    ? com.example.applibrary.R.drawable.bg_chip_available
                    : com.example.applibrary.R.drawable.bg_chip_unavailable);
            int textColor = ContextCompat.getColor(binding.getRoot().getContext(), available
                    ? com.example.applibrary.R.color.library_on_tertiary_container
                    : com.example.applibrary.R.color.library_on_surface_variant);
            binding.textAvailability.setTextColor(textColor);
            binding.getRoot().setOnClickListener(v -> listener.onBookClick(book));
        }
    }
}
