package com.example.enrollmentsystem;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.enrollmentsystem.ui.assessment.AssessmentActivity;
import com.example.enrollmentsystem.ui.enrollment.EnrollmentActivity;
import com.example.enrollmentsystem.ui.feemanagement.FeeManagementActivity;
import com.example.enrollmentsystem.ui.registrar.RegistrarActivity;
import com.example.enrollmentsystem.ui.studentledger.StudentLedgerActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.main_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_enrollment) {
                startActivity(new Intent(MainActivity.this, EnrollmentActivity.class));
            } else if (id == R.id.nav_assessment) {
                startActivity(new Intent(MainActivity.this, AssessmentActivity.class));
            } else if (id == R.id.nav_fee_management) {
                startActivity(new Intent(MainActivity.this, FeeManagementActivity.class));
            } else if (id == R.id.nav_ledger) {
                startActivity(new Intent(MainActivity.this, StudentLedgerActivity.class));
            } else if (id == R.id.nav_registrar) {
                startActivity(new Intent(MainActivity.this, RegistrarActivity.class));
            } else if (id == R.id.nav_setup) {
                startActivity(new Intent(MainActivity.this, SetupActivity.class));
            } else if (id == R.id.nav_logout) {
                showLogoutConfirmationDialog();
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Confirm Logout")
            .setMessage("Click “Confirm” to logout of the system.")
            .setPositiveButton("Confirm", (dialog, which) -> {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
