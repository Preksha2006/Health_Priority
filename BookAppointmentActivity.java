package com.example.healthpriority;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    EditText ed1, ed2, ed3, ed4;
    TextView tv;
    Button dateButton, timeButton, btnBook, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        tv = findViewById(R.id.textView2);
        ed1 = findViewById(R.id.editTextAppFullName);
        ed2 = findViewById(R.id.editTextAppAddress);
        ed3 = findViewById(R.id.editTextAppContactNumber);
        ed4 = findViewById(R.id.editTextAppFees);
        dateButton = findViewById(R.id.buttonAppDate);
        timeButton = findViewById(R.id.buttonAppTime);
        btnBook = findViewById(R.id.buttonBookAppointment);
        btnBack = findViewById(R.id.buttonAppBack);

        // Disable editing
        ed1.setKeyListener(null);
        ed2.setKeyListener(null);
        ed3.setKeyListener(null);
        ed4.setKeyListener(null);

        // Get data from Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("text1");
        String fullname = intent.getStringExtra("text2");
        String address = intent.getStringExtra("text3");
        String contact = intent.getStringExtra("text4");
        String fees = intent.getStringExtra("text5");

        if (title != null) tv.setText(title);
        if (fullname != null) ed1.setText(fullname);
        if (address != null) ed2.setText(address);
        if (contact != null) ed3.setText(contact);
        if (fees != null) ed4.setText("Cons Fees: " + fees + "/-");

        // Set default date and time when activity is created
        setDefaultDateAndTime();

        // Date Picker
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(BookAppointmentActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                String dateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                dateButton.setText(dateString);
                            }
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() + 86400000); // Only allow future dates
                datePickerDialog.show();
            }
        });

        // Time Picker
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(BookAppointmentActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                                String timeString = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                                timeButton.setText(timeString);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookAppointmentActivity.this, FindDoctorActivity.class));
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", null);

                // Validate that username exists
                if (username == null || username.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No user logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Extracting fees safely from ed4 text
                String feeString = ed4.getText().toString().replaceAll("[^\\d.]+", ""); // removes "Cons Fees:" and "/-"
                float feeValue = 0;

                if (feeString.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Invalid fee format", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    feeValue = Float.parseFloat(feeString);
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Invalid fee format", Toast.LENGTH_SHORT).show();
                    return;
                }

                String date = dateButton.getText().toString();
                String time = timeButton.getText().toString();

                Database db = new Database(getApplicationContext());

                // Check if appointment already exists
                if (db.checkAppointmentExists(username, title + "=>" + fullname, address, contact, date, time) == 1) {
                    Toast.makeText(getApplicationContext(), "Appointment already booked", Toast.LENGTH_LONG).show();
                } else {
                    // Add the appointment
                    db.addOrder(username, title + "=>" + fullname, address, contact, 0, date, time, feeValue, "appointment");
                    Toast.makeText(getApplicationContext(), "Appointment booked successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(BookAppointmentActivity.this, HomeActivity.class));
                }
            }
        });
    }

    // Method to set default date and time
    private void setDefaultDateAndTime() {
        Calendar calendar = Calendar.getInstance();

        // Set default date (e.g., current date)
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = day + "/" + (month + 1) + "/" + year;
        dateButton.setText(currentDate);

        // Set default time (e.g., current time)
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String currentTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        timeButton.setText(currentTime);
    }
}
