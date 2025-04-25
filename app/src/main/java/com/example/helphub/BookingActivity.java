package com.example.helphub;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextInputEditText dateInput;
    private TextInputEditText timeInput;
    private TextInputEditText topicInput;
    private MaterialButton confirmButton;

    private String gaTaName;
    private String gaTaCourse;
    private Date selectedDate;
    private int selectedHour = -1;
    private int selectedMinute = -1;

    private DatabaseReference appointmentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Retrieve GA/TA info from intent
        gaTaName   = getIntent().getStringExtra("ga_ta_name");
        gaTaCourse = getIntent().getStringExtra("ga_ta_course");

        // Reference to appointments node
        appointmentsRef = FirebaseDatabase.getInstance()
                .getReference("appointments");

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        toolbar      = findViewById(R.id.toolbar);
        dateInput    = findViewById(R.id.dateInput);
        timeInput    = findViewById(R.id.timeInput);
        topicInput   = findViewById(R.id.topicInput);
        confirmButton= findViewById(R.id.confirmButton);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book Appointment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setSubtitle(gaTaName + " - " + gaTaCourse);
    }

    private void setupListeners() {
        dateInput.setOnClickListener(v -> showDatePicker());
        timeInput.setOnClickListener(v -> showTimePicker());
        confirmButton.setOnClickListener(v -> confirmBooking());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = new Date(selection);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            dateInput.setText(sdf.format(selectedDate));
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            selectedHour   = timePicker.getHour();
            selectedMinute = timePicker.getMinute();
            timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void confirmBooking() {
        // Ensure user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Read inputs
        String uid    = currentUser.getUid();
        String email  = currentUser.getEmail();
        String date   = dateInput.getText().toString();
        String time   = timeInput.getText().toString();
        String topic  = topicInput.getText().toString().trim();  // ← Now captured!

        // Validate inputs
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedHour == -1 || selectedMinute == -1) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (topic.isEmpty()) {
            Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show();
            return;
        }

        // Push to Firebase
        String key = appointmentsRef.push().getKey();
        Appointment appt = new Appointment(
                key, uid, email, gaTaName, gaTaCourse, date, time, topic
        );

        appointmentsRef.child(key)
                .setValue(appt)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to book appointment: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
