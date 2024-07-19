package com.example.enrollmentsystem.ui.enrollment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.enrollmentsystem.MainActivity;
import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.model.Student;
import com.example.enrollmentsystem.ui.enrollment.EnrollmentDataFragment;
import com.example.enrollmentsystem.ui.enrollment.PreEnlistedSubjectsFragment;
import com.example.enrollmentsystem.ui.enrollment.ReviewEnrollmentFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class EnrollmentActivity extends AppCompatActivity implements EnrollmentDataFragment.OnStudentSelectedListener {

    private Fragment currentFragment;
    private EnrollmentDataFragment enrollmentDataFragment;
    private Fragment preEnlistedSubjectsFragment;
    private Fragment reviewFragment;
    private ImageView step1, step2, step3;
    private Button nextButton, backButton;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayList<String> studentList;
    private HashMap<String, Student> studentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        Toolbar toolbar = findViewById(R.id.enrollmentToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_arrow_back_24);

        enrollmentDataFragment = new EnrollmentDataFragment();
        preEnlistedSubjectsFragment = new PreEnlistedSubjectsFragment();
        reviewFragment = new ReviewEnrollmentFragment();

        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);

        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        currentFragment = enrollmentDataFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .commit();
        updateStepProgressBar();
        updateButtonLabels();

        autoCompleteTextView = findViewById(R.id.useridTextView);
        studentList = new ArrayList<>();
        studentMap = new HashMap<>();
        fetchDataFromServer();

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            Student selectedStudent = studentMap.get(selectedItem);
            if (selectedStudent != null) {
                onStudentSelected(selectedStudent);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentFragment instanceof EnrollmentDataFragment) {
                currentFragment = preEnlistedSubjectsFragment;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, currentFragment)
                        .addToBackStack(null)
                        .commit();
            } else if (currentFragment instanceof PreEnlistedSubjectsFragment) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Confirm Enrollment")
                        .setMessage("Click “Done” to add the student to the pre-enrollment list.")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            new MaterialAlertDialogBuilder(this)
                                    .setTitle("Success")
                                    .setMessage("The student has been pre-enlisted successfully!")
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
                Intent intent = new Intent(EnrollmentActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            updateStepProgressBar();
            updateButtonLabels();
        });

        backButton.setOnClickListener(v -> onBackPressed());

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            updateStepProgressBar();
            updateButtonLabels();
        });
    }

    private void fetchDataFromServer() {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost/enrollmentsystem/getStudents.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String studentID = jsonObject.getString("studentID");
                    String department = jsonObject.getString("department");
                    String level = jsonObject.getString("level");
                    String course = jsonObject.getString("course");
                    String curriculum = jsonObject.getString("curriculum");
                    String name = jsonObject.getString("name");

                    Student student = new Student(studentID, department, level, course, curriculum, name);
                    studentList.add(name + " (" + studentID + ")");
                    studentMap.put(name + " (" + studentID + ")", student);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EnrollmentActivity.this,
                            android.R.layout.simple_dropdown_item_1line, studentList);
                    autoCompleteTextView.setAdapter(adapter);
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> Toast.makeText(EnrollmentActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
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
            backButton.setVisibility(Button.GONE);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Confirm Exit")
                        .setMessage("Click \"Confirm\" to go back to home and cancel the process.")
                        .setPositiveButton("Confirm", (dialog, which) -> {
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
            currentFragment = preEnlistedSubjectsFragment;
            fm.popBackStack();
        } else if (currentFragment instanceof PreEnlistedSubjectsFragment) {
            currentFragment = enrollmentDataFragment;
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
        updateStepProgressBar();
        updateButtonLabels();
    }

    @Override
    public void onStudentSelected(Student student) {
        if (currentFragment instanceof EnrollmentDataFragment) {
            ((EnrollmentDataFragment) currentFragment).updateViewsWithStudentData(student);
        }
    }

}
