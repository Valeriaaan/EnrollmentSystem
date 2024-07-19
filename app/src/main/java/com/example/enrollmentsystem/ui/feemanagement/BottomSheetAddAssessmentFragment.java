package com.example.enrollmentsystem.ui.feemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.enrollmentsystem.R;
import com.google.android.material.button.MaterialButton;

public class BottomSheetAddAssessmentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bottom_sheet_add_assessment, container, false);

        MaterialButton doneButton = rootView.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast message when "Done" button is clicked
                Toast.makeText(requireContext(), "Assessment added successfully!", Toast.LENGTH_SHORT).show();

                // Optionally, you can dismiss the bottom sheet dialog if needed
                dismissBottomSheetDialog();
            }
        });

        return rootView;
    }

    private void dismissBottomSheetDialog() {
        // Dismiss the bottom sheet dialog if it is shown
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
