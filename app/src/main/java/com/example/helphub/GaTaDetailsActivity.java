package com.example.helphub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class GaTaDetailsActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private ShapeableImageView profileImage;
    private TextView nameText;
    private TextView courseText;
    private TextView availabilityText;
    private TextView officeHoursText;
    private MaterialButton bookAppointmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ga_ta_details);

        // Get data from intent
        String name = getIntent().getStringExtra("ga_ta_name");
        String course = getIntent().getStringExtra("ga_ta_course");
        boolean available = getIntent().getBooleanExtra("ga_ta_available", false);

        initializeViews();
        setupToolbar(name);
        displayDetails(name, course, available);
        setupListeners(name, course);
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.profileImage);
        nameText = findViewById(R.id.nameText);
        courseText = findViewById(R.id.courseText);
        availabilityText = findViewById(R.id.availabilityText);
        officeHoursText = findViewById(R.id.officeHoursText);
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton);
    }

    private void setupToolbar(String name) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void displayDetails(String name, String course, boolean available) {
        nameText.setText(name);
        courseText.setText(course);
        
        availabilityText.setText(available ? "Available" : "Not Available");
        availabilityText.setTextColor(getColor(available ? 
            android.R.color.holo_green_dark : 
            android.R.color.holo_red_dark));
        
        bookAppointmentButton.setEnabled(available);
        bookAppointmentButton.setText(available ? "Book Appointment" : "Currently Unavailable");
    }

    private void setupListeners(String name, String course) {
        bookAppointmentButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("ga_ta_name", name);
            intent.putExtra("ga_ta_course", course);
            startActivity(intent);
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