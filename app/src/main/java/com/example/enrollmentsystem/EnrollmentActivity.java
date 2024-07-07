package com.example.enrollmentsystem;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.widget.ImageView;

public class EnrollmentActivity extends AppCompatActivity {

    private Fragment currentFragment;
    private Fragment enrollmentDataFragment;
    private Fragment preEnlistedSubjectsFragment;
    private Fragment reviewFragment;
    private ImageView step1, step2, step3;

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

        // Show the first fragment by default
        currentFragment = enrollmentDataFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .commit();
        updateStepProgressBar();

        // Handle Next button click
        findViewById(R.id.nextButton).setOnClickListener(v -> {
            if (currentFragment instanceof EnrollmentDataFragment) {
                // Move to the second fragment
                currentFragment = preEnlistedSubjectsFragment;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, currentFragment)
                        .addToBackStack(null)
                        .commit();
            } else if (currentFragment instanceof PreEnlistedSubjectsFragment) {
                // Move to the third fragment
                currentFragment = reviewFragment;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, currentFragment)
                        .addToBackStack(null)
                        .commit();
            }
            updateStepProgressBar();
        });

        // Handle Back button click
        findViewById(R.id.backButton).setOnClickListener(v -> {
            onBackPressed();
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            updateStepProgressBar();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
    }
}
