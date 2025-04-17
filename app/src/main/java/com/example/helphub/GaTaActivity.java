package com.example.helphub;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class GaTaActivity extends AppCompatActivity {
    private MaterialButton viewAppointmentsButton;
    private MaterialButton manageScheduleButton;
    private MaterialButton viewProfileButton;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ga_ta);

        // Initialize views
        viewAppointmentsButton = findViewById(R.id.viewAppointmentsButton);
        manageScheduleButton = findViewById(R.id.manageScheduleButton);
        viewProfileButton = findViewById(R.id.viewProfileButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Set click listeners
        viewAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement view appointments functionality
                Toast.makeText(GaTaActivity.this, "View Appointments clicked", Toast.LENGTH_SHORT).show();
            }
        });

        manageScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement manage schedule functionality
                Toast.makeText(GaTaActivity.this, "Manage Schedule clicked", Toast.LENGTH_SHORT).show();
            }
        });

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement view profile functionality
                Toast.makeText(GaTaActivity.this, "View Profile clicked", Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement logout functionality
                finish();
            }
        });
    }
} 