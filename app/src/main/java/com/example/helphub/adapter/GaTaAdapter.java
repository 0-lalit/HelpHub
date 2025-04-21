package com.example.helphub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.helphub.R;
import com.example.helphub.model.GaTa;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;
import java.util.List;

public class GaTaAdapter extends RecyclerView.Adapter<GaTaAdapter.GaTaViewHolder> {
    private List<GaTa> gaTaList;
    private OnItemClickListener listener;
    private boolean isAdminView;

    public interface OnItemClickListener {
        void onItemClick(GaTa gaTa);
        void onDeleteClick(GaTa gaTa);
        void onToggleAvailabilityClick(GaTa gaTa);
    }

    public GaTaAdapter(List<GaTa> gaTaList, boolean isAdminView) {
        this.gaTaList = gaTaList != null ? gaTaList : new ArrayList<>();
        this.isAdminView = isAdminView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GaTaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_ga_ta, parent, false);
        return new GaTaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GaTaViewHolder holder, int position) {
        GaTa gaTa = gaTaList.get(position);
        holder.name.setText(gaTa.getName());
        holder.course.setText(gaTa.getCourse());
        
        // Set availability indicator color
        int colorRes = gaTa.isAvailable() ? 
            android.R.color.holo_green_light : 
            android.R.color.holo_red_light;
        holder.availabilityIndicator.setBackgroundResource(colorRes);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(gaTa);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(gaTa);
            }
        });

        holder.toggleAvailabilityButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleAvailabilityClick(gaTa);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gaTaList.size();
    }

    public void updateList(List<GaTa> newList) {
        this.gaTaList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class GaTaViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView profileImage;
        TextView name;
        TextView course;
        View availabilityIndicator;
        MaterialButton deleteButton;
        MaterialButton toggleAvailabilityButton;

        public GaTaViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.name);
            course = itemView.findViewById(R.id.course);
            availabilityIndicator = itemView.findViewById(R.id.availabilityIndicator);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            toggleAvailabilityButton = itemView.findViewById(R.id.toggleAvailabilityButton);
        }
    }
} 