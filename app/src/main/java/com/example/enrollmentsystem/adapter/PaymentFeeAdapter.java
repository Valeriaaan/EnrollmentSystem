package com.example.enrollmentsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.model.PaymentFee;
import com.google.android.material.chip.Chip;

import java.util.List;

public class PaymentFeeAdapter extends RecyclerView.Adapter<PaymentFeeAdapter.PaymentFeeViewHolder> {

    private List<PaymentFee> paymentFeeList;
    private Context context;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(PaymentFee paymentFee);
    }

    public PaymentFeeAdapter(Context context, List<PaymentFee> paymentFeeList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.paymentFeeList = paymentFeeList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public PaymentFeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_fee, parent, false);
        return new PaymentFeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentFeeViewHolder holder, int position) {
        PaymentFee paymentFee = paymentFeeList.get(position);
        holder.titleTextView.setText(paymentFee.getPaymentTitle());
        holder.amountTextView.setText("₱ " + paymentFee.getPaymentAmount());
        holder.descriptionTextView.setText(paymentFee.getPaymentDescription());

        holder.deleteChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.onDeleteClick(paymentFee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentFeeList.size();
    }

    public static class PaymentFeeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, amountTextView, descriptionTextView;
        Chip deleteChip;

        public PaymentFeeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            deleteChip = itemView.findViewById(R.id.deleteChip);
        }
    }

    public void removePaymentFee(PaymentFee paymentFee) {
        int position = paymentFeeList.indexOf(paymentFee);
        if (position != -1) {
            paymentFeeList.remove(position);
            notifyItemRemoved(position);
        }
    }
}