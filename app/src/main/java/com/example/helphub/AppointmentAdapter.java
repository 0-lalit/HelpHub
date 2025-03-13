package com.example.helphub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<AppointmentItem> appointments;

    public AppointmentAdapter(List<AppointmentItem> appointments) {
        this.appointments = appointments;
    }

    public void updateAppointments(List<AppointmentItem> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentItem item = appointments.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private TextView gaTaNameText;
        private TextView courseText;
        private TextView dateTimeText;
        private TextView topicText;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            gaTaNameText = itemView.findViewById(R.id.gaTaNameText);
            courseText = itemView.findViewById(R.id.courseText);
            dateTimeText = itemView.findViewById(R.id.dateTimeText);
            topicText = itemView.findViewById(R.id.topicText);
        }

        public void bind(AppointmentItem item) {
            gaTaNameText.setText(item.getGaTaName());
            courseText.setText(item.getCourse());
            dateTimeText.setText(String.format("%s at %s", item.getDate(), item.getTime()));
            topicText.setText(item.getTopic());
        }
    }
} 