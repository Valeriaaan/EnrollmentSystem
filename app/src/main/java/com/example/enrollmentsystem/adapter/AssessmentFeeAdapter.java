package com.example.enrollmentsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.model.AssessmentFee;
import com.google.android.material.chip.Chip;

import java.util.List;

public class AssessmentFeeAdapter extends RecyclerView.Adapter<AssessmentFeeAdapter.AssessmentFeeViewHolder> {

    private List<AssessmentFee> assessmentFeeList;
    private Context context;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(AssessmentFee assessmentFee);
    }

    public AssessmentFeeAdapter(Context context, List<AssessmentFee> assessmentFeeList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.assessmentFeeList = assessmentFeeList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public AssessmentFeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assessment_fee, parent, false);
        return new AssessmentFeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentFeeViewHolder holder, int position) {
        AssessmentFee assessmentFee = assessmentFeeList.get(position);
        holder.titleTextView.setText(assessmentFee.getAssessmentTitle());
        holder.amountTextView.setText("₱ " + assessmentFee.getAssessmentAmount());
        holder.descriptionTextView.setText(assessmentFee.getAssessmentDescription());

        holder.deleteChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.onDeleteClick(assessmentFee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assessmentFeeList.size();
    }

    public static class AssessmentFeeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, amountTextView, descriptionTextView;
        Chip deleteChip;

        public AssessmentFeeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            deleteChip = itemView.findViewById(R.id.deleteChip);
        }
    }

    public void removeAssessmentFee(AssessmentFee assessmentFee) {
        int position = assessmentFeeList.indexOf(assessmentFee);
        if (position != -1) {
            assessmentFeeList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
