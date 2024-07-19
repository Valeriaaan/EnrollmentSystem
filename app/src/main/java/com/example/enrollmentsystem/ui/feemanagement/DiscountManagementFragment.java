package com.example.enrollmentsystem.ui.feemanagement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.adapter.DiscountFeeAdapter;
import com.example.enrollmentsystem.model.DiscountFee;
import com.example.enrollmentsystem.model.PaymentFee;
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

public class DiscountManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private DiscountFeeAdapter adapter;
    private List<DiscountFee> discountFeeList;

    private OnDiscountFeeAddedListener onDiscountFeeAddedListener;

    public interface OnDiscountFeeAddedListener {
        void onDiscountFeeAdded(DiscountFee discountFee);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onDiscountFeeAddedListener = (OnDiscountFeeAddedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDiscountFeeAddedListener");
        }
    }

    public DiscountManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discount_management, container, false);

        FloatingActionButton fab = rootView.findViewById(R.id.addFeeButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.discountFeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        discountFeeList = new ArrayList<>();
        adapter = new DiscountFeeAdapter(requireContext(), discountFeeList, new DiscountFeeAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(DiscountFee discountFee) {
                showDeleteConfirmationDialog(discountFee);
            }
        });
        recyclerView.setAdapter(adapter);

        fetchDataFromServer();

        return rootView;
    }

    private void showBottomSheetDialog() {
        // Create an instance of the bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_add_discount, null);

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

    private void submitDataToServer(BottomSheetDialog bottomSheetDialog) {
        // Retrieve data from the text input
        TextInputEditText codeEditText = bottomSheetDialog.findViewById(R.id.codeEditText);
        TextInputEditText titleEditText = bottomSheetDialog.findViewById(R.id.titleEditText);
        TextInputEditText amountEditText = bottomSheetDialog.findViewById(R.id.amountEditText);
        TextInputEditText descriptionEditText = bottomSheetDialog.findViewById(R.id.descriptionEditText);

        if (codeEditText != null && titleEditText != null && amountEditText != null && descriptionEditText != null) {
            String code = codeEditText.getText().toString().trim();
            String title = titleEditText.getText().toString().trim();
            String amount = amountEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            // Validate if all fields are filled
            if (!code.isEmpty() && !title.isEmpty() && !amount.isEmpty() && !description.isEmpty()) {
                // Data is valid, proceed to submit
                sendDataToServer(code, title, amount, description, bottomSheetDialog);
            } else {
                // Show a toast indicating all fields are required
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendDataToServer(String code, String title, String amount, String description, BottomSheetDialog bottomSheetDialog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your server URL
                    URL url = new URL("http://lesterintheclouds.com/crud-android/addDiscountFee.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Create the data to send
                    String postData = "&code=" + code + "&title=" + title + "&amount=" + amount + "&description=" + description;

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
                                // Add new DiscountFee to the list
                                DiscountFee newDiscountFee = new DiscountFee(0, code, title, amount, description);
                                discountFeeList.add(newDiscountFee);
                                adapter.notifyItemInserted(discountFeeList.size() - 1);
                                onDiscountFeeAddedListener.onDiscountFeeAdded(newDiscountFee);
                                Toast.makeText(requireContext(), "Discount Fee added successfully", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();  // Dismiss bottom sheet after successful submission
                            }
                        });
                    } else {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Failed to add discount fee", Toast.LENGTH_SHORT).show();
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
                    URL url = new URL("http://lesterintheclouds.com/crud-android/getDiscountFee.php");
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
                        String code = jsonObject.getString("code");
                        String title = jsonObject.getString("title");
                        String amount = jsonObject.getString("amount");
                        String description = jsonObject.getString("description");
                        discountFeeList.add(new DiscountFee(id, code, title, amount, description));
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

    private void showDeleteConfirmationDialog(DiscountFee discountFee) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Discount Fee")
                .setMessage("Click 'Confirm' to delete this discount fee.")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // Perform the delete operation
                    deleteDiscountFee(discountFee);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteDiscountFee(DiscountFee discountFee) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your server URL
                    URL url = new URL("http://lesterintheclouds.com/crud-android/deletePaymentFee.php?id=" + discountFee.getDiscountId());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    // Check the response
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Remove the item from the list
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                discountFeeList.remove(discountFee);
                                adapter.notifyDataSetChanged();

                                new MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Success")
                                        .setMessage("Discount Fee '" + discountFee.getDiscountTitle() + "' has been deleted successfully!")
                                        .setPositiveButton("Done", null)
                                        .show();
                            }
                        });
                    } else {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Failed to delete Discount Fee", Toast.LENGTH_SHORT).show();
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
