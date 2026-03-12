package com.example.healthpriority;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LabTestBookActivity extends AppCompatActivity {

    EditText edname, edaddress, edcontact, edpincode;
    Button btnBooking;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test_book);

        // Bind views
        edname = findViewById(R.id.editTextLTBFullName);
        edaddress = findViewById(R.id.editTextLTBAddress);
        edcontact = findViewById(R.id.editTextLTBContact);
        edpincode = findViewById(R.id.editTextLTBPincode);
        btnBooking = findViewById(R.id.buttonLTBBooking);

        // Get data from Intent
        Intent intent = getIntent();
        String priceStr = intent.getStringExtra("price"); // Price as String (e.g., "500")
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");

        // Log the received data for debugging
        if (priceStr != null) {
            Log.d("LabTestBook", "Received price: " + priceStr);  // For debugging
        } else {
            Log.d("LabTestBook", "Price is null");  // For debugging
        }

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Input values
                String name = edname.getText().toString().trim();
                String address = edaddress.getText().toString().trim();
                String contact = edcontact.getText().toString().trim();
                String pincodeStr = edpincode.getText().toString().trim();

                // Validation: Ensure all fields are filled
                if (name.isEmpty() || address.isEmpty() || contact.isEmpty() || pincodeStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate Contact number (Assuming it's a 10-digit number)
                if (contact.length() != 10 || !contact.matches("\\d+")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid 10-digit contact number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate Pincode (Assuming it's a 6-digit number)
                if (pincodeStr.length() != 6 || !pincodeStr.matches("\\d+")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid 6-digit pincode", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse the price and handle errors
                try {
                    int pincode = Integer.parseInt(pincodeStr);
                    float cost = 0;

                    if (priceStr != null) {
                        String cleanedPriceStr = priceStr.replace("Total Cost:", "")
                                .replace("₹", "")
                                .trim();
                        cost = Float.parseFloat(cleanedPriceStr);
                    } else {
                        Toast.makeText(getApplicationContext(), "Price is invalid or missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Log the parsed data for debugging
                    Log.d("LabTestBook", "Parsed Pincode: " + pincode);  // For debugging
                    Log.d("LabTestBook", "Parsed Cost: " + cost);  // For debugging

                    // Get username from SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    String username = sharedPreferences.getString("username", "");

                    // Check if username is valid
                    if (username.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Insert into DB
                    Database db = new Database(getApplicationContext());
                    db.addOrder(username, name, address, contact, pincode, date, time, cost, "lab");
                    db.removeCart(username, "lab");

                    Toast.makeText(getApplicationContext(), "Your Booking is Done Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LabTestBookActivity.this, HomeActivity.class));
                    finish();  // Finish the current activity

                } catch (NumberFormatException e) {
                    Log.d("LabTestBook", "Error in parsing number: " + e.toString());
                    Toast.makeText(getApplicationContext(), "Invalid input. Please check fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
