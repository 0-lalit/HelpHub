package com.example.helphub;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminAppointmentManager {
    private final Context context;
    private final RecyclerView appointmentsRecyclerView;
    private final AdminAppointmentAdapter adapter;
    private final List<AdminAppointment> appointments;
    private final String adminId; // To identify which admin's appointments to show

    public AdminAppointmentManager(Context context, RecyclerView appointmentsRecyclerView, String adminId) {
        this.context = context;
        this.appointmentsRecyclerView = appointmentsRecyclerView;
        this.adminId = adminId;
        this.appointments = new ArrayList<>();
        this.adapter = new AdminAppointmentAdapter(appointments);
        
        setupRecyclerView();
        loadAdminAppointments();
    }

    private void setupRecyclerView() {
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        appointmentsRecyclerView.setAdapter(adapter);
    }

    private void loadAdminAppointments() {
        // In a real app, this would load from a database based on adminId
        // For now, we'll add some sample appointments for the admin
        addAppointment(new AdminAppointment(
            "Student: John Smith",
            "CS 101 - Introduction to Programming",
            "2024-03-20",
            "14:30",
            "Topic: Basic Programming Concepts"
        ));
        addAppointment(new AdminAppointment(
            "Student: Sarah Johnson",
            "CS 303 - Algorithms",
            "2024-03-21",
            "15:00",
            "Topic: Graph Algorithms"
        ));
        addAppointment(new AdminAppointment(
            "Student: Michael Brown",
            "CS 202 - Data Structures",
            "2024-03-22",
            "10:00",
            "Topic: Binary Trees"
        ));
        
        // Notify adapter that data has changed
        adapter.notifyDataSetChanged();
    }

    public void showAddAppointmentDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.appointment_add_dialog, null);
        
        Dialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("Add Office Hours")
                .setView(dialogView)
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    TextInputEditText nameInput = dialogView.findViewById(R.id.nameInput);
                    TextInputEditText courseNameInput = dialogView.findViewById(R.id.courseNameInput);
                    TextInputEditText dayInput = dialogView.findViewById(R.id.dayInput);
                    TextInputEditText officeHoursInput = dialogView.findViewById(R.id.officeHoursInput);

                    String name = nameInput.getText().toString();
                    String courseName = courseNameInput.getText().toString();
                    String day = dayInput.getText().toString();
                    String officeHours = officeHoursInput.getText().toString();

                    if (!name.isEmpty() && !courseName.isEmpty() && !day.isEmpty() && !officeHours.isEmpty()) {
                        // Add office hours for the admin
                        Toast.makeText(context, "Office hours added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void addAppointment(AdminAppointment appointment) {
        appointments.add(appointment);
        adapter.notifyItemInserted(appointments.size() - 1);
    }

    // Admin Appointment data class
    public static class AdminAppointment {
        public final String studentName;
        public final String courseName;
        public final String date;
        public final String time;
        public final String topic;

        public AdminAppointment(String studentName, String courseName, String date, String time, String topic) {
            this.studentName = studentName;
            this.courseName = courseName;
            this.date = date;
            this.time = time;
            this.topic = topic;
        }
    }

    // RecyclerView Adapter for Admin Appointments
    private static class AdminAppointmentAdapter extends RecyclerView.Adapter<AdminAppointmentAdapter.AdminAppointmentViewHolder> {
        private final List<AdminAppointment> appointments;

        public AdminAppointmentAdapter(List<AdminAppointment> appointments) {
            this.appointments = appointments;
        }

        @NonNull
        @Override
        public AdminAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.appointment_item, parent, false);
            return new AdminAppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdminAppointmentViewHolder holder, int position) {
            AdminAppointment appointment = appointments.get(position);
            holder.nameText.setText(appointment.studentName);
            holder.courseNameText.setText(appointment.courseName);
            holder.dayText.setText(appointment.date);
            holder.timingText.setText(appointment.time);
            holder.officeHoursText.setText(appointment.topic);
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        static class AdminAppointmentViewHolder extends RecyclerView.ViewHolder {
            final TextView nameText;
            final TextView courseNameText;
            final TextView dayText;
            final TextView timingText;
            final TextView officeHoursText;

            public AdminAppointmentViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.nameText);
                courseNameText = itemView.findViewById(R.id.courseNameText);
                dayText = itemView.findViewById(R.id.dayText);
                timingText = itemView.findViewById(R.id.timingText);
                officeHoursText = itemView.findViewById(R.id.officeHoursText);
            }
        }
    }
}