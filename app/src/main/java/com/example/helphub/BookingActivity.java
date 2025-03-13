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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Get GA/TA information from intent
        gaTaName = getIntent().getStringExtra("ga_ta_name");
        gaTaCourse = getIntent().getStringExtra("ga_ta_course");

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        topicInput = findViewById(R.id.topicInput);
        confirmButton = findViewById(R.id.confirmButton);

        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book Appointment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Set GA/TA info
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
            selectedHour = timePicker.getHour();
            selectedMinute = timePicker.getMinute();
            timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void confirmBooking() {
        String topic = topicInput.getText().toString().trim();
        
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

        // TODO: Save appointment to database
        Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
        finish();
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