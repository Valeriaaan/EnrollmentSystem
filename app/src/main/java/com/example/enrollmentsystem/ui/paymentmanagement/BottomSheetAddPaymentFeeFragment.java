package com.example.enrollmentsystem.ui.paymentmanagement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.enrollmentsystem.R;
import com.example.enrollmentsystem.model.PaymentFee;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class BottomSheetAddPaymentFeeFragment extends BottomSheetDialogFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnPaymentFeeAddedListener onPaymentFeeAddedListener;

    public interface OnPaymentFeeAddedListener {
        void onPaymentFeeAdded(PaymentFee paymentFee);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onPaymentFeeAddedListener = (OnPaymentFeeAddedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPaymentFeeAddedListener");
        }
    }

    public BottomSheetAddPaymentFeeFragment() {
        // Required empty public constructor
    }

    public static BottomSheetAddPaymentFeeFragment newInstance(String param1, String param2) {
        BottomSheetAddPaymentFeeFragment fragment = new BottomSheetAddPaymentFeeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bottom_sheet_add_payment_fee, container, false);

        // Find "Done" button in the bottom sheet layout
        Button doneButton = rootView.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to submit data to server
                submitDataToServer(rootView);
            }
        });

        return rootView;
    }

    private void submitDataToServer(View bottomSheetDialog) {
        // Retrieve data from the text input
        TextInputEditText titleEditText = bottomSheetDialog.findViewById(R.id.titleEditText);
        TextInputEditText amountEditText = bottomSheetDialog.findViewById(R.id.amountEditText);
        TextInputEditText descriptionEditText = bottomSheetDialog.findViewById(R.id.descriptionEditText);

        if (titleEditText != null && amountEditText != null && descriptionEditText != null) {
            String title = titleEditText.getText().toString().trim();
            String amount = amountEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            // Validate if all fields are filled
            if (!title.isEmpty() && !amount.isEmpty() && !description.isEmpty()) {
                // Data is valid, proceed to submit
                sendDataToServer(title, amount, description, bottomSheetDialog);
            } else {
                // Show a toast indicating all fields are required
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendDataToServer(String title, String amount, String description, View bottomSheetDialog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your server URL
                    URL url = new URL("http://lesterintheclouds.com/crud-android/addPaymentFee.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Create the data to send
                    String postData = "title=" + title + "&amount=" + amount + "&description=" + description;

                    // Write data to the connection
                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    // Check the response code
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Create a new PaymentFee object
                        PaymentFee newPaymentFee = new PaymentFee(0, title, amount, description);

                        // Notify the listener
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onPaymentFeeAddedListener.onPaymentFeeAdded(newPaymentFee);
                                Toast.makeText(requireContext(), "Payment Fee added successfully", Toast.LENGTH_SHORT).show();
                                BottomSheetAddPaymentFeeFragment.this.dismiss();  // Dismiss bottom sheet after successful submission
                            }
                        });
                    } else {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Failed to add payment fee", Toast.LENGTH_SHORT).show();
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
