package com.example.applibrary.ui.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applibrary.R;
import com.example.applibrary.data.remote.dto.EventDtos;
import com.example.applibrary.databinding.ItemEventBinding;
import com.example.applibrary.ui.util.ListCardUi;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.Holder> {

    public interface OnEventActionListener {
        void onAction(EventDtos.EventItem item);
    }

    private final OnEventActionListener listener;
    private List<EventDtos.EventItem> items = new ArrayList<>();

    public EventListAdapter(OnEventActionListener listener) {
        this.listener = listener;
    }

    public void submit(List<EventDtos.EventItem> list) {
        items = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemEventBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemEventBinding binding;

        Holder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(EventDtos.EventItem item) {
            var context = binding.getRoot().getContext();
            binding.textTitle.setText(item.title != null ? item.title : "");
            binding.textType.setText(context.getString(ListCardUi.eventTypeLabelRes(item.type)));
            binding.textDate.setText(ListCardUi.formatEventDate(item.startsAt));

            int accentColor = ContextCompat.getColor(context, ListCardUi.eventAccentColorRes(item.type));
            binding.headerAccent.setBackgroundColor(accentColor);

            if (item.description != null && !item.description.isBlank()) {
                binding.textDescription.setVisibility(View.VISIBLE);
                binding.textDescription.setText(item.description);
            } else {
                binding.textDescription.setVisibility(View.GONE);
            }

            boolean full = item.capacity > 0 && item.registeredCount >= item.capacity;
            if (full && !item.registeredByMe) {
                binding.textPlaces.setText(R.string.event_places_full);
            } else {
                binding.textPlaces.setText(context.getString(
                        R.string.event_places, item.registeredCount, item.capacity));
            }

            int progress = ListCardUi.eventProgressPercent(item.registeredCount, item.capacity);
            if (item.capacity > 0) {
                binding.progressPlaces.setVisibility(View.VISIBLE);
                binding.progressPlaces.setMax(100);
                binding.progressPlaces.setProgressCompat(progress, false);
            } else {
                binding.progressPlaces.setVisibility(View.GONE);
            }

            binding.textRegisteredBadge.setVisibility(item.registeredByMe ? View.VISIBLE : View.GONE);

            MaterialButton btn = binding.btnAction;
            if (item.registeredByMe) {
                btn.setText(R.string.event_unregister);
                btn.setEnabled(true);
            } else {
                btn.setText(R.string.event_register);
                btn.setEnabled(!full);
            }
            btn.setOnClickListener(v -> {
                if (item.id != null) {
                    listener.onAction(item);
                }
            });
        }
    }
}
