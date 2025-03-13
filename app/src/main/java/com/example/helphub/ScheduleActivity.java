package com.example.helphub;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private CalendarView calendarView;
    private RecyclerView scheduleList;
    private FloatingActionButton addScheduleButton;
    private AppointmentAdapter appointmentAdapter;
    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        initializeViews();
        setupToolbar();
        setupCalendar();
        setupScheduleList();
        setupAddButton();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        calendarView = findViewById(R.id.calendarView);
        scheduleList = findViewById(R.id.scheduleList);
        addScheduleButton = findViewById(R.id.addScheduleButton);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupCalendar() {
        selectedDate = calendarView.getDate();
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Update selected date and refresh schedule list
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            selectedDate = view.getDate();
            Toast.makeText(this, "Selected: " + sdf.format(new Date(selectedDate)), Toast.LENGTH_SHORT).show();
            refreshScheduleList();
        });
    }

    private void setupScheduleList() {
        scheduleList.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(getSampleAppointments());
        scheduleList.setAdapter(appointmentAdapter);
    }

    private void setupAddButton() {
        addScheduleButton.setOnClickListener(v -> {
            // TODO: Implement add schedule functionality
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            String dateStr = sdf.format(new Date(selectedDate));
            Toast.makeText(this, "Add schedule for " + dateStr, Toast.LENGTH_SHORT).show();
        });
    }

    private void refreshScheduleList() {
        // TODO: Load appointments for selected date
        appointmentAdapter.updateAppointments(getSampleAppointments());
    }

    private List<AppointmentItem> getSampleAppointments() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date(selectedDate));
        
        List<AppointmentItem> appointments = new ArrayList<>();
        appointments.add(new AppointmentItem(
            "John Smith",
            "CS 101",
            currentDate,
            "10:00 AM",
            "Office Hours"
        ));
        appointments.add(new AppointmentItem(
            "Sarah Wilson",
            "CS 202",
            currentDate,
            "2:00 PM",
            "Project Review"
        ));
        appointments.add(new AppointmentItem(
            "Michael Brown",
            "CS 303",
            currentDate,
            "4:30 PM",
            "Algorithm Discussion"
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