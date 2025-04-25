package com.example.helphub;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private CalendarView calendarView;
    private RecyclerView scheduleList;
    private AppointmentAdapter appointmentAdapter;
    private long selectedDateMillis;
    private FirebaseUser currentUser;
    private DatabaseReference appointmentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        initializeViews();
        setupToolbar();
        setupCalendar();
        setupRecyclerView();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        calendarView = findViewById(R.id.calendarView);
        scheduleList = findViewById(R.id.scheduleList);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupCalendar() {
        selectedDateMillis = calendarView.getDate();
        loadAppointmentsForDate(selectedDateMillis);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Convert date to millis
            String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
                selectedDateMillis = date.getTime();
                loadAppointmentsForDate(selectedDateMillis);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to parse date", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        appointmentAdapter = new AppointmentAdapter(new ArrayList<>());
        scheduleList.setLayoutManager(new LinearLayoutManager(this));
        scheduleList.setAdapter(appointmentAdapter);
    }

    private void loadAppointmentsForDate(long dateMillis) {
        if (currentUser == null) return;

        String formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date(dateMillis));

        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Appointment> filteredAppointments = new ArrayList<>();
                for (DataSnapshot apptSnap : snapshot.getChildren()) {
                    Appointment appt = apptSnap.getValue(Appointment.class);
                    if (appt != null &&
                            appt.studentId.equals(currentUser.getUid()) &&
                            appt.date.equals(formattedDate)) {
                        filteredAppointments.add(appt);
                    }
                }

                if (filteredAppointments.isEmpty()) {
                    Toast.makeText(ScheduleActivity.this, "No appointments for this day", Toast.LENGTH_SHORT).show();
                }

                appointmentAdapter.updateData(filteredAppointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScheduleActivity.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
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
