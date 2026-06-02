package com.example.applibrary.ui.catalog;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applibrary.R;
import com.example.applibrary.data.remote.dto.CatalogDtos;
import com.example.applibrary.databinding.ItemBookBinding;
import com.example.applibrary.ui.util.ListCardUi;

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
            binding.textTitle.setText(book.title != null ? book.title : "");
            binding.textAuthor.setText(book.authorName != null ? book.authorName : "");
            ListCardUi.bindBookCoverImage(
                    binding.imageCover,
                    binding.coverContainer,
                    binding.textCoverInitial,
                    binding.textYear,
                    book.coverImageUrl,
                    book.title,
                    book.authorName,
                    book.publicationYear);

            boolean available = book.availableCount > 0;
            String avail = available
                    ? binding.getRoot().getContext().getString(
                    R.string.copies_available, book.availableCount)
                    : binding.getRoot().getContext().getString(R.string.copies_unavailable);
            binding.textAvailability.setText(avail);
            binding.textAvailability.setBackgroundResource(available
                    ? R.drawable.bg_chip_available
                    : R.drawable.bg_chip_unavailable);
            int textColor = ContextCompat.getColor(binding.getRoot().getContext(), available
                    ? R.color.library_on_tertiary_container
                    : R.color.library_on_surface_variant);
            binding.textAvailability.setTextColor(textColor);
            binding.getRoot().setOnClickListener(v -> {
                if (book.id != null) {
                    listener.onBookClick(book);
                }
            });
        }
    }
}
