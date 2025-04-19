package com.example.helphub;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class AdminAppointmentsActivity extends AppCompatActivity {
    private static final String TAG = "AdminAppointmentsActivity";
    private MaterialToolbar toolbar;
    private RecyclerView appointmentsList;
    private FloatingActionButton addAppointmentFab;
    private AdminAppointmentManager adminAppointmentManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_admin_appointments);

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
            
            initializeViews();
            setupToolbar();
            setupAdminAppointmentManager();
            setupListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing appointments screen", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            toolbar = findViewById(R.id.toolbar);
            appointmentsList = findViewById(R.id.appointmentsList);
            addAppointmentFab = findViewById(R.id.addAppointmentFab);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error finding views", Toast.LENGTH_SHORT).show();
            throw e;
        }
    }

    private void setupToolbar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("My Appointments");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar", e);
            Toast.makeText(this, "Error setting up toolbar", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAdminAppointmentManager() {
        try {
            // Get the current admin's ID from Firebase Auth
            String adminId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "default_admin";
            adminAppointmentManager = new AdminAppointmentManager(this, appointmentsList, adminId);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up appointment manager", e);
            Toast.makeText(this, "Error loading appointments", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        try {
            addAppointmentFab.setOnClickListener(v -> {
                // This will show a dialog to add office hours
                adminAppointmentManager.showAddAppointmentDialog();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners", e);
            Toast.makeText(this, "Error setting up buttons", Toast.LENGTH_SHORT).show();
        }
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