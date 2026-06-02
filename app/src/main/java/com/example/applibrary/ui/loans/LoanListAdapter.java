package com.example.applibrary.ui.loans;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applibrary.R;
import com.example.applibrary.data.remote.dto.LoanDtos;
import com.example.applibrary.databinding.ItemLoanBinding;

import java.util.ArrayList;
import java.util.List;

public class LoanListAdapter extends RecyclerView.Adapter<LoanListAdapter.Holder> {

    public interface OnRenewListener {
        void onRenew(LoanDtos.LoanItem loan);
    }

    private final List<LoanDtos.LoanItem> items = new ArrayList<>();
    private boolean showRenew;
    private final OnRenewListener renewListener;

    public LoanListAdapter(boolean showRenew, OnRenewListener renewListener) {
        this.showRenew = showRenew;
        this.renewListener = renewListener;
    }

    public void setShowRenew(boolean showRenew) {
        this.showRenew = showRenew;
    }

    public void submit(List<LoanDtos.LoanItem> loans) {
        items.clear();
        if (loans != null) items.addAll(loans);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemLoanBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
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
        private final ItemLoanBinding binding;

        Holder(ItemLoanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LoanDtos.LoanItem loan) {
            binding.textTitle.setText(loan.bookTitle != null ? loan.bookTitle : "");
            String author = loan.authorName != null && !loan.authorName.isEmpty()
                    ? loan.authorName + "\n" : "";
            if ("RETURNED".equals(loan.status)) {
                binding.textDates.setText(author + binding.getRoot().getContext().getString(
                        R.string.loan_dates_returned, loan.loanDate, loan.returnedAt));
            } else {
                binding.textDates.setText(author + binding.getRoot().getContext().getString(
                        R.string.loan_dates_active, loan.loanDate, loan.dueDate));
            }
            String status;
            if (loan.overdue) {
                status = binding.getRoot().getContext().getString(R.string.loan_overdue);
            } else if ("RETURNED".equals(loan.status)) {
                status = binding.getRoot().getContext().getString(R.string.loan_returned);
            } else {
                status = binding.getRoot().getContext().getString(R.string.loan_active);
            }
            binding.textStatus.setText(status);

            boolean showBtn = showRenew && loan.canRenew;
            binding.btnRenew.setVisibility(showBtn ? View.VISIBLE : View.GONE);
            binding.btnRenew.setOnClickListener(v -> {
                if (renewListener != null) renewListener.onRenew(loan);
            });
        }
    }
}
