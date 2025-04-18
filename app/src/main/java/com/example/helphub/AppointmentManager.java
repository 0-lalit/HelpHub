package com.example.helphub;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManager {
    private final Context context;
    private final RecyclerView appointmentsRecyclerView;
    private final AppointmentAdapter adapter;
    private final List<Appointment> appointments;

    public AppointmentManager(Context context, RecyclerView appointmentsRecyclerView) {
        this.context = context;
        this.appointmentsRecyclerView = appointmentsRecyclerView;
        this.appointments = new ArrayList<>();
        this.adapter = new AppointmentAdapter(appointments);
        
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        appointmentsRecyclerView.setAdapter(adapter);
    }

    public void showAddAppointmentDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.appointment_add_dialog, null);
        
        Dialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("Add Appointment")
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
                        addAppointment(new Appointment(name, courseName, day, officeHours));
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        adapter.notifyItemInserted(appointments.size() - 1);
    }

    // Appointment data class
    public static class Appointment {
        public final String name;
        public final String courseName;
        public final String day;
        public final String officeHours;

        public Appointment(String name, String courseName, String day, String officeHours) {
            this.name = name;
            this.courseName = courseName;
            this.day = day;
            this.officeHours = officeHours;
        }
    }

    // RecyclerView Adapter
    private static class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
        private final List<Appointment> appointments;

        public AppointmentAdapter(List<Appointment> appointments) {
            this.appointments = appointments;
        }

        @NonNull
        @Override
        public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.appointment_item, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
            Appointment appointment = appointments.get(position);
            holder.nameText.setText(appointment.name);
            holder.courseNameText.setText(appointment.courseName);
            holder.dayText.setText(appointment.day);
            holder.officeHoursText.setText(appointment.officeHours);
        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        static class AppointmentViewHolder extends RecyclerView.ViewHolder {
            final TextView nameText;
            final TextView courseNameText;
            final TextView dayText;
            final TextView officeHoursText;

            public AppointmentViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.nameText);
                courseNameText = itemView.findViewById(R.id.courseNameText);
                dayText = itemView.findViewById(R.id.dayText);
                officeHoursText = itemView.findViewById(R.id.officeHoursText);
            }
        }
    }
}