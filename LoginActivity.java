package com.example.healthpriority;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText edUsername, edPassword;
    Button btn;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        edUsername = findViewById(R.id.editTextText); // Make sure this ID matches your layout
        edPassword = findViewById(R.id.editTextTextPassword);
        btn = findViewById(R.id.button);
        tvRegister = findViewById(R.id.text); // "Register" TextView

        btn.setOnClickListener(view -> {
          String username = edUsername.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
           // startActivity(new Intent(LoginActivity.this, HomeActivity.class));

             if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter all the details", Toast.LENGTH_SHORT).show();
            } else {
//                Database db = new Database(getApplicationContext(), "Health_Priority", null, 1);
                Database db = new Database(getApplicationContext());
                if (db.login(username, password)) {
                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish(); // Optional: prevents returning to login screen
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    Log.d("LoginActivity", "Invalid login attempt for user: " + username);
                }
            }
        });

        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

