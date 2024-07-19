package com.example.enrollmentsystem.ui.feemanagement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.adapter.AssessmentFeeAdapter;
import com.example.enrollmentsystem.model.AssessmentFee;
import com.example.enrollmentsystem.model.SubFee;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AssessmentMangementFragment extends Fragment {

    private RecyclerView recyclerView;
    private AssessmentFeeAdapter adapter;
    private List<AssessmentFee> assessmentFeeList;

    private OnAssessmentFeeAddedListener onAssessmentFeeAddedListener;
    private List<String> spinnerDataList;

    public interface OnAssessmentFeeAddedListener {
        void onAssessmentFeeAdded(AssessmentFee assessmentFee);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onAssessmentFeeAddedListener = (OnAssessmentFeeAddedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnAssessmentFeeAddedListener");
        }
    }

    public AssessmentMangementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_assessment_mangement, container, false);

        FloatingActionButton fab = rootView.findViewById(R.id.addFeeButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.assessmentFeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        assessmentFeeList = new ArrayList<>();
        adapter = new AssessmentFeeAdapter(requireContext(), assessmentFeeList, new AssessmentFeeAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(AssessmentFee assessmentFee) {
                showDeleteConfirmationDialog(assessmentFee);
            }
        });
        recyclerView.setAdapter(adapter);

        fetchDataFromServer();

        return rootView;
    }

    private void showBottomSheetDialog() {
        // Create an instance of the bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_add_assessment, null);

        // Populate the Spinner with data from the server
        Spinner mySpinner = bottomSheetView.findViewById(R.id.subFeeSpinner);
        fetchSpinnerData(mySpinner);

        // Find "Done" button in the bottom sheet layout
        Button doneButton = bottomSheetView.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to submit data to server
                submitDataToServer(bottomSheetDialog);
            }
        });

        // Set the inflated view to the bottom sheet dialog
        bottomSheetDialog.setContentView(bottomSheetView);

        // Show the bottom sheet dialog
        bottomSheetDialog.show();
    }

    private void fetchSpinnerData(Spinner spinner) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your server URL
                    URL url = new URL("http://lesterintheclouds.com/crud-android/getAssessmentFee.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON response
                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<SubFee> spinnerItems = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String title = jsonObject.getString("title");
                        spinnerItems.add(new SubFee(id, title));
                    }

                    // Update Spinner on the UI thread
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<SubFee> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerItems);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        }
                    });

                    conn.disconnect();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void submitDataToServer(BottomSheetDialog bottomSheetDialog) {
        // Retrieve data from the text input
        TextInputEditText titleEditText = bottomSheetDialog.findViewById(R.id.titleEditText);
        TextInputEditText amountEditText = bottomSheetDialog.findViewById(R.id.amountEditText);
        TextInputEditText descriptionEditText = bottomSheetDialog.findViewById(R.id.descriptionEditText);
        Spinner subFeeSpinner = bottomSheetDialog.findViewById(R.id.subFeeSpinner);

        if (titleEditText != null && amountEditText != null && descriptionEditText != null && subFeeSpinner != null) {
            String title = titleEditText.getText().toString().trim();
            String amount = amountEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            // Get the selected SpinnerItem
            SubFee selectedItem = (SubFee) subFeeSpinner.getSelectedItem();
            int selectedItemId = selectedItem.getSubFeeId();

            // Validate if all fields are filled
            if (!title.isEmpty() && !amount.isEmpty() && !description.isEmpty()) {
                // Data is valid, proceed to submit
                sendDataToServer(title, amount, description, selectedItemId, bottomSheetDialog);
            } else {
                // Show a toast indicating all fields are required
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void sendDataToServer(String title, String amount, String description, int selectedItemId, BottomSheetDialog bottomSheetDialog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your server URL
                    URL url = new URL("http://lesterintheclouds.com/crud-android/addAssessmentFee.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Create the data to send
                    String postData = "&title=" + title + "&amount=" + amount + "&description=" + description + "&subFee=" + selectedItemId;

                    // Write data to the connection
                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    // Check the response code
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Request successful
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Add new AssessmentFee to the list
                                AssessmentFee newAssessmentFee = new AssessmentFee(0, title, amount, description);
                                assessmentFeeList.add(newAssessmentFee);
                                adapter.notifyItemInserted(assessmentFeeList.size() - 1);
                                onAssessmentFeeAddedListener.onAssessmentFeeAdded(newAssessmentFee);
                                Toast.makeText(requireContext(), "Assessment Fee added successfully", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();  // Dismiss bottom sheet after successful submission
                            }
                        });
                    } else {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Failed to add assessment fee", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    conn.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }


    private void fetchDataFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your server URL
                    URL url = new URL("http://lesterintheclouds.com/crud-android/getAssessmentFee.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON response
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String title = jsonObject.getString("title");
                        String amount = jsonObject.getString("amount");
                        String description = jsonObject.getString("description");
                        assessmentFeeList.add(new AssessmentFee(id, title, amount, description));
                    }

                    // Update RecyclerView
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                    conn.disconnect();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showDeleteConfirmationDialog(AssessmentFee assessmentFee) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Assessment Fee")
                .setMessage("Click 'Confirm' to delete this assessment fee.")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // Perform the delete operation
                    deleteAssessmentFee(assessmentFee);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAssessmentFee(AssessmentFee assessmentFee) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your server URL
                    URL url = new URL("http://lesterintheclouds.com/crud-android/deleteAssessmentFee.php?id=" + assessmentFee.getAssessmentId());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    // Check the response
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Remove the item from the list
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                assessmentFeeList.remove(assessmentFee);
                                adapter.notifyDataSetChanged();

                                new MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Success")
                                        .setMessage("Assessment Fee '" + assessmentFee.getAssessmentTitle() + "' has been deleted successfully!")
                                        .setPositiveButton("Done", null)
                                        .show();
                            }
                        });
                    } else {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Failed to delete Assessment Fee", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    conn.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }
}
