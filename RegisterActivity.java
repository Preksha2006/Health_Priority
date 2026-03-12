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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText edUsername, edEmail, edPassword, edConfirm;
    Button btn;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize views
        edUsername = findViewById(R.id.editTextText2);              // username
        edEmail = findViewById(R.id.editTextRegUsername);           // email
        edPassword = findViewById(R.id.editTextRegPassword2);       // password
        edConfirm = findViewById(R.id.editTextRegConfirmPassword);  // confirm password
        btn = findViewById(R.id.buttonRegister);                    // register button
        tv = findViewById(R.id.textView2);                          // "Already have account" text

        // Handle "Already have an account" click
        tv.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Handle registration button click
        btn.setOnClickListener(view -> {
            String username = edUsername.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            String password = edPassword.getText().toString();
            String confirm = edConfirm.getText().toString();

            // Create database instance
            Database db = new Database(getApplicationContext());

            // Check for empty fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter all the details", Toast.LENGTH_SHORT).show();
            }
            // Check if passwords match
            else if (!password.equals(confirm)) {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            // Validate password
            else if (!isValid(password)) {
                Toast.makeText(getApplicationContext(), "Password must be at least 8 characters, with letters, digits, and a special character.", Toast.LENGTH_LONG).show();
            }
            // Proceed with registration
            else {
                // Attempt to register the user
                if (db.register(username, email, password)) {
                    // Save username to SharedPreferences after successful registration
                    SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);  // Save username in SharedPreferences
                    editor.apply();  // Apply the changes

                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                    // Navigate to login screen
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Registration failed, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Validate password strength
    public static boolean isValid(String password) {
        boolean hasLetter = false, hasDigit = false, hasSpecial = false;

        if (password.length() < 8) return false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if ("!@#$%^&*()-_=+[{]}|;:',<.>/?".indexOf(c) >= 0) hasSpecial = true;
        }

        return hasLetter && hasDigit && hasSpecial;
    }
}
