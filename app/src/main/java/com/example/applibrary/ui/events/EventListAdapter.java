package com.example.applibrary.ui.events;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applibrary.R;
import com.example.applibrary.data.remote.dto.EventDtos;
import com.example.applibrary.databinding.ItemEventBinding;

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
            binding.textTitle.setText(item.title);
            binding.textDate.setText(item.startsAt);
            binding.textPlaces.setText(binding.getRoot().getContext().getString(
                    R.string.event_places, item.registeredCount, item.capacity));
            if (item.registeredByMe) {
                binding.btnAction.setText(R.string.event_unregister);
            } else {
                binding.btnAction.setText(R.string.event_register);
            }
            binding.btnAction.setOnClickListener(v -> listener.onAction(item));
        }
    }
}
