package com.example.enrollmentsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.model.DiscountFee;
import com.google.android.material.chip.Chip;

import java.util.List;

public class DiscountFeeAdapter extends RecyclerView.Adapter<DiscountFeeAdapter.DiscountFeeViewHolder> {

    private List<DiscountFee> discountFeeList;
    private Context context;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(DiscountFee discountFee);
    }

    public DiscountFeeAdapter(Context context, List<DiscountFee> discountFeeList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.discountFeeList = discountFeeList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public DiscountFeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discount_fee, parent, false);
        return new DiscountFeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountFeeViewHolder holder, int position) {
        DiscountFee discountFee = discountFeeList.get(position);
        holder.codeTextView.setText(discountFee.getDiscountCode());
        holder.titleTextView.setText(discountFee.getDiscountTitle());
        holder.amountTextView.setText("₱ " + discountFee.getDiscountAmount());
        holder.descriptionTextView.setText(discountFee.getDiscountDescription());

        holder.deleteChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.onDeleteClick(discountFee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return discountFeeList.size();
    }

    public static class DiscountFeeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, amountTextView, descriptionTextView, codeTextView;
        Chip deleteChip;

        public DiscountFeeViewHolder(@NonNull View itemView) {
            super(itemView);
            codeTextView = itemView.findViewById(R.id.codeTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            deleteChip = itemView.findViewById(R.id.deleteChip);
        }
    }

    public void removeDiscountFee(DiscountFee discountFee) {
        int position = discountFeeList.indexOf(discountFee);
        if (position != -1) {
            discountFeeList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
