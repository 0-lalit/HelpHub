package com.example.helphub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView appointmentsList;
    private FloatingActionButton addAppointmentFab;
    private AppointmentManager appointmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        initializeViews();
        setupToolbar();
        setupAppointmentManager();
        setupListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        appointmentsList = findViewById(R.id.appointmentsList);
        addAppointmentFab = findViewById(R.id.addAppointmentFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Appointments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupAppointmentManager() {
        appointmentManager = new AppointmentManager(this, appointmentsList);
    }

    private void setupListeners() {
        addAppointmentFab.setOnClickListener(v -> {
            appointmentManager.showAddAppointmentDialog();
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