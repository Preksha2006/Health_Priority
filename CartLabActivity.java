package com.example.healthpriority;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CartLabActivity extends AppCompatActivity {

    private TextView tvTotal;
    private ListView lst;
    private Button dateButton, timeButton, btnCheckout, btnBack;
    private ArrayList<HashMap<String, String>> list;
    private SimpleAdapter sa;

    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_lab);

        // Initialize views
        tvTotal = findViewById(R.id.textViewCartTotalCost);
        btnCheckout = findViewById(R.id.buttonCheckout);
        btnBack = findViewById(R.id.buttonCartBack);
        lst = findViewById(R.id.listViewCart);
        dateButton = findViewById(R.id.buttonCartDatePicker);
        timeButton = findViewById(R.id.buttonCartTimePicker);

        // Get username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // Load cart data
        Database db = new Database(getApplicationContext());
        ArrayList<String> dbData = db.getCartData(username, "lab");

        float totalAmount = 0;
        list = new ArrayList<>();

        if (dbData != null && !dbData.isEmpty()) {
            for (String data : dbData) {
                String[] strData = data.split("\\$");
                String product = strData[0];
                String price = strData[1];
                totalAmount += Float.parseFloat(price);

                HashMap<String, String> item = new HashMap<>();
                item.put("line1", product);
                item.put("line2", "");
                item.put("line3", "");
                item.put("line4", "");
                item.put("line5", "Cost: ₹" + price);
                list.add(item);
            }

            sa = new SimpleAdapter(this, list, R.layout.multi_lines,
                    new String[]{"line1", "line2", "line3", "line4", "line5"},
                    new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e});
            lst.setAdapter(sa);
        } else {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
        }

        tvTotal.setText("Total Cost: ₹" + totalAmount);

        // Date Picker
        dateButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CartLabActivity.this,
                    (view, year, monthOfYear, dayOfMonth) ->
                            dateButton.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year),
                    year, month, day);
            datePickerDialog.show();
        });

        // Time Picker
        timeButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    CartLabActivity.this,
                    (view, hourOfDay, minute) ->
                            timeButton.setText(String.format("%02d:%02d", hourOfDay, minute)),
                    hour, minute, true);
            timePickerDialog.show();
        });

        // Checkout Button
        btnCheckout.setOnClickListener(view -> {
            if (dbData != null && !dbData.isEmpty()) {
                Intent it = new Intent(CartLabActivity.this, LabTestBookActivity.class);
                it.putExtra("price", tvTotal.getText().toString());
                it.putExtra("date", dateButton.getText().toString());
                it.putExtra("time", timeButton.getText().toString());
                startActivity(it);
            } else {
                Toast.makeText(CartLabActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });

        // Back Button
        btnBack.setOnClickListener(view ->
                startActivity(new Intent(CartLabActivity.this, LabTestActivity.class)));
    }
}
