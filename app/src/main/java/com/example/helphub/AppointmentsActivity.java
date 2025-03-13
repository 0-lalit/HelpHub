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
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
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

    private void setupRecyclerView() {
        appointmentsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(getSampleAppointments());
        appointmentsList.setAdapter(adapter);
    }

    private void setupListeners() {
        addAppointmentFab.setOnClickListener(v -> {
            // TODO: Implement add appointment functionality
            Toast.makeText(this, "Add appointment clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private List<AppointmentItem> getSampleAppointments() {
        // TODO: Replace with real data from database
        List<AppointmentItem> appointments = new ArrayList<>();
        appointments.add(new AppointmentItem(
            "Lalit Kumar",
            "CS 101 - Introduction to Programming",
            "2024-03-20",
            "14:30",
            "Basic Programming Concepts"
        ));
        appointments.add(new AppointmentItem(
            "Hawaii Smith",
            "CS 303 - Algorithms",
            "2024-03-21",
            "15:00",
            "Graph Algorithms"
        ));
        return appointments;
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