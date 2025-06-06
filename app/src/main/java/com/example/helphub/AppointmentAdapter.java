package com.example.helphub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private List<Appointment> appointments;

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void updateData(List<Appointment> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.ViewHolder holder, int position) {
        Appointment appt = appointments.get(position);
        holder.name.setText(appt.gaTaName + " - " + appt.gaTaCourse);
        holder.date.setText(appt.date);
        holder.time.setText(appt.time);
        holder.topic.setText(appt.topic);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, time, topic;

        ViewHolder(View itemView) {
            super(itemView);
            name  = itemView.findViewById(R.id.ga_ta_name);
            date  = itemView.findViewById(R.id.appointment_date);
            time  = itemView.findViewById(R.id.appointment_time);
            topic = itemView.findViewById(R.id.appointment_topic);
        }
    }
}
