package com.example.helphub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class GaTaActivity extends AppCompatActivity {
    private static final String TAG = "GaTaActivity";
    private MaterialButton viewAppointmentsButton;
    private MaterialButton manageScheduleButton;
    private MaterialButton viewProfileButton;
    private MaterialButton logoutButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_ga_ta);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        initializeViews();
        setupListeners();
        
        Log.d(TAG, "GaTaActivity initialized successfully");
    }
    
    private void initializeViews() {
        try {
            viewAppointmentsButton = findViewById(R.id.viewAppointmentsButton);
            manageScheduleButton = findViewById(R.id.manageScheduleButton);
            viewProfileButton = findViewById(R.id.viewProfileButton);
            logoutButton = findViewById(R.id.logoutButton);
            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error initializing UI", Toast.LENGTH_LONG).show();
        }
    }

    private void setupListeners() {
        viewAppointmentsButton.setOnClickListener(v -> {
            Log.d(TAG, "View Appointments button clicked");
            Intent intent = new Intent(this, AppointmentsActivity.class);
            startActivity(intent);
        });

        manageScheduleButton.setOnClickListener(v -> {
            Log.d(TAG, "Manage Schedule button clicked");
            Toast.makeText(this, "Manage Schedule clicked", Toast.LENGTH_SHORT).show();
        });

        viewProfileButton.setOnClickListener(v -> {
            Log.d(TAG, "View Profile button clicked");
            Toast.makeText(this, "View Profile clicked", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            Log.d(TAG, "Logout button clicked");
            performLogout();
        });
    }
    
    private void performLogout() {
        // Show loading state
        logoutButton.setEnabled(false);
        logoutButton.setText("Logging out...");
        
        // Sign out from Firebase
        mAuth.signOut();
        
        // Show success message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate to login screen and clear the back stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 