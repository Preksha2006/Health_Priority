package com.example.healthpriority;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LabTestDetailsActivity extends AppCompatActivity {
    TextView tvPackageName, tvTotalCost;
    EditText edDetails;
    Button btnAddToCart, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test_details);

        // Initialize views
        tvPackageName = findViewById(R.id.textViewLTPackageName);
        tvTotalCost = findViewById(R.id.textViewTotalCost);
        edDetails = findViewById(R.id.editTextTextLDTextMultiline);
        btnAddToCart = findViewById(R.id.buttonLTAddToCart);
        btnBack = findViewById(R.id.buttonLDBack);

        edDetails.setKeyListener(null); // Make it read-only

        // Get intent data
        Intent intent = getIntent();
        String packageName = intent.getStringExtra("text1");
        String packageDetails = intent.getStringExtra("text2");
        String costString = intent.getStringExtra("text3");

        // Display data
        tvPackageName.setText(packageName);
        edDetails.setText(packageDetails);
        tvTotalCost.setText("Total Cost: ₹" + costString + "/-");

        // Back button
        btnBack.setOnClickListener(v -> startActivity(new Intent(LabTestDetailsActivity.this, LabTestActivity.class)));

        // Add to cart button
        btnAddToCart.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please log in first", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float price = Float.parseFloat(costString);
                Database db = new Database(getApplicationContext());

                if (db.checkCart(username, packageName) == 1) {
                    Toast.makeText(getApplicationContext(), "Product already in cart", Toast.LENGTH_SHORT).show();
                } else {
                    db.addCart(username, packageName, price, "lab");
                    Toast.makeText(getApplicationContext(), "Product added to cart", Toast.LENGTH_SHORT).show();

                    // Navigate to the Cart Activity
                    startActivity(new Intent(LabTestDetailsActivity.this, CartLabActivity.class));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Invalid cost format", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
