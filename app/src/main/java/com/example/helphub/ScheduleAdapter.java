package com.example.helphub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<AppointmentItem> items;

    public ScheduleAdapter(List<AppointmentItem> items) {
        this.items = items;
    }

    public void updateAppointments(List<AppointmentItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        AppointmentItem item = items.get(position);
        holder.name.setText(item.getName() + " - " + item.getCourse());
        holder.date.setText(item.getDate());
        holder.time.setText(item.getTime());
        holder.topic.setText(item.getTopic());
    }

    @Override
    public int getItemCount() {
        return items.size();
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
