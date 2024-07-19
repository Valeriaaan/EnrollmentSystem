package com.example.enrollmentsystem.ui.paymentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enrollmentsystem.MainActivity;
import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.adapter.PaymentFeeAdapter;
import com.example.enrollmentsystem.model.PaymentFee;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PaymentManagementActivity extends AppCompatActivity implements BottomSheetAddPaymentFeeFragment.OnPaymentFeeAddedListener {

    private RecyclerView recyclerView;
    private PaymentFeeAdapter adapter;
    private List<PaymentFee> paymentFeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_management);

        Toolbar toolbar = findViewById(R.id.paymentManagementToolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_arrow_back_24);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.paymentFeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        paymentFeeList = new ArrayList<>();
        adapter = new PaymentFeeAdapter(this, paymentFeeList, new PaymentFeeAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(PaymentFee paymentFee) {
                showDeleteConfirmationDialog(paymentFee);
            }
        });
        recyclerView.setAdapter(adapter);

        // Fetch data from server
        fetchDataFromServer();

        FloatingActionButton addFeeButton = findViewById(R.id.addFeeButton);
        addFeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetAddPaymentFeeFragment bottomSheetFragment = BottomSheetAddPaymentFeeFragment.newInstance("param1", "param2");
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to MainActivity
            Intent intent = new Intent(PaymentManagementActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPaymentFeeAdded(PaymentFee paymentFee) {
        paymentFeeList.add(paymentFee);
        adapter.notifyItemInserted(paymentFeeList.size() - 1);
    }

    private void fetchDataFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your server URL
                    URL url = new URL("http://lesterintheclouds.com/crud-android/getPaymentFee.php");
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
                        paymentFeeList.add(new PaymentFee(id, title, amount, description));
                    }

                    // Update RecyclerView
                    runOnUiThread(new Runnable() {
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

    private void showDeleteConfirmationDialog(PaymentFee paymentFee) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Payment Fee")
                .setMessage("Click 'Confirm' to delete this payment fee.")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // Perform the delete operation
                    deletePaymentFee(paymentFee);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePaymentFee(PaymentFee paymentFee) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your server URL
                    URL url = new URL("http://lesterintheclouds.com/crud-android/deletePaymentFee.php?id=" + paymentFee.getPaymentId());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    // Check the response
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Remove the item from the adapter
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.removePaymentFee(paymentFee);

                                new MaterialAlertDialogBuilder(PaymentManagementActivity.this)
                                        .setTitle("Success")
                                        .setMessage("Payment Fee '" + paymentFee.getPaymentTitle() + "' has been deleted successfully!")
                                        .setPositiveButton("Done", null)
                                        .show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(recyclerView, "Failed to delete Payment Fee", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }

                    conn.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
