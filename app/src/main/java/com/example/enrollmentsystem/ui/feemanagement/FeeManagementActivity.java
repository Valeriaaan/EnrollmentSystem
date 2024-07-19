package com.example.enrollmentsystem.ui.feemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.enrollmentsystem.MainActivity;
import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.model.AssessmentFee;
import com.example.enrollmentsystem.model.DiscountFee;
import com.google.android.material.tabs.TabLayout;

public class FeeManagementActivity extends AppCompatActivity
        implements DiscountManagementFragment.OnDiscountFeeAddedListener,
        AssessmentMangementFragment.OnAssessmentFeeAddedListener {

    private Fragment assessmentManagementFragment;
    private Fragment discountManagementFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fee_management);

        Toolbar toolbar = findViewById(R.id.feeManagementToolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_arrow_back_24);

        assessmentManagementFragment = new AssessmentMangementFragment();
        discountManagementFragment = new DiscountManagementFragment();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fee_management_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = assessmentManagementFragment;
                        break;
                    case 1:
                        selectedFragment = discountManagementFragment;
                        break;
                }
                if (selectedFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, selectedFragment);
                    transaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });

        // Set the initial fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, assessmentManagementFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to MainActivity
            Intent intent = new Intent(FeeManagementActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDiscountFeeAdded(DiscountFee discountFee) {
        // Implement this method if you need to handle actions when a discount fee is added
        // For example, updating UI elements or performing additional operations
    }


    @Override
    public void onAssessmentFeeAdded(AssessmentFee assessmentFee) {

    }
}
