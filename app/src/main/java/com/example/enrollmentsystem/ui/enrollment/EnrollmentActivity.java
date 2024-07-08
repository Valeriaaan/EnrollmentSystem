package com.example.enrollmentsystem.ui.enrollment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.enrollmentsystem.MainActivity;
import com.example.enrollmentsystem.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EnrollmentActivity extends AppCompatActivity {

    private Fragment currentFragment;
    private Fragment enrollmentDataFragment;
    private Fragment preEnlistedSubjectsFragment;
    private Fragment reviewFragment;
    private ImageView step1, step2, step3;
    private Button nextButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enrollment);

        Toolbar toolbar = findViewById(R.id.enrollmentToolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_arrow_back_24);

        // Initialize fragments
        enrollmentDataFragment = new EnrollmentDataFragment();
        preEnlistedSubjectsFragment = new PreEnlistedSubjectsFragment();
        reviewFragment = new ReviewEnrollmentFragment();

        // Initialize step indicators
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);

        // Initialize buttons
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        // Show the first fragment by default
        currentFragment = enrollmentDataFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .commit();
        updateStepProgressBar();
        updateButtonLabels();

        // Handle Next button click
        nextButton.setOnClickListener(v -> {
            if (currentFragment instanceof EnrollmentDataFragment) {
                // Move to the second fragment
                currentFragment = preEnlistedSubjectsFragment;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, currentFragment)
                        .addToBackStack(null)
                        .commit();
            } else if (currentFragment instanceof PreEnlistedSubjectsFragment) {
                // Show a confirmation dialog for transaction completion
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Confirm Enrollment")
                        .setMessage("Click “Done” to add “Student Name” to the pre-enrollment list.")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            // Show a success dialog for 2 seconds and then navigate to ReviewEnrollmentFragment
                            new MaterialAlertDialogBuilder(this)
                                    .setTitle("Success")
                                    .setMessage("“Student Name” has been pre-enlisted successfully!")
                                    .setPositiveButton("OK", null)
                                    .show();

                            currentFragment = reviewFragment;
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, currentFragment)
                                    .addToBackStack(null)
                                    .commit();
                            updateStepProgressBar();
                            updateButtonLabels();

                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else if (currentFragment instanceof ReviewEnrollmentFragment) {
                // Navigate back to MainActivity
                Intent intent = new Intent(EnrollmentActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            updateStepProgressBar();
            updateButtonLabels();
        });

        // Handle Back button click
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            updateStepProgressBar();
            updateButtonLabels();
        });
    }

    private void updateStepProgressBar() {
        // Reset all steps to inactive
        step1.setBackgroundResource(R.drawable.icon_step_inactive);
        step2.setBackgroundResource(R.drawable.icon_step_inactive);
        step3.setBackgroundResource(R.drawable.icon_step_inactive);

        // Set the current step to active
        if (currentFragment instanceof EnrollmentDataFragment) {
            step1.setBackgroundResource(R.drawable.icon_step_active);
        } else if (currentFragment instanceof PreEnlistedSubjectsFragment) {
            step2.setBackgroundResource(R.drawable.icon_step_active);
        } else if (currentFragment instanceof ReviewEnrollmentFragment) {
            step3.setBackgroundResource(R.drawable.icon_step_active);
        }
    }

    private void updateButtonLabels() {
        if (currentFragment instanceof EnrollmentDataFragment) {
            nextButton.setText("Next");
            backButton.setVisibility(Button.VISIBLE);
        } else if (currentFragment instanceof PreEnlistedSubjectsFragment) {
            nextButton.setText("Finish");
            backButton.setVisibility(Button.VISIBLE);
        } else if (currentFragment instanceof ReviewEnrollmentFragment) {
            nextButton.setText("Done");
            backButton.setVisibility(Button.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                // Show a confirmation dialog
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Confirm Exit")
                        .setMessage("Click \"Confirm\" to go back to home and cancel the process.")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            // Navigate back to MainActivity
                            Intent intent = new Intent(EnrollmentActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (currentFragment instanceof ReviewEnrollmentFragment) {
            // Move back to the second fragment
            currentFragment = preEnlistedSubjectsFragment;
            fm.popBackStack();
        } else if (currentFragment instanceof PreEnlistedSubjectsFragment) {
            // Move back to the first fragment
            currentFragment = enrollmentDataFragment;
            fm.popBackStack();
        } else {
            // For any other case, use default behavior
            super.onBackPressed();
        }
        updateStepProgressBar();
        updateButtonLabels();
    }
}
