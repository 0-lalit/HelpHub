package com.example.helphub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class GaTaAdapter extends RecyclerView.Adapter<GaTaAdapter.GaTaViewHolder> {
    private List<GaTaItem> items;
    private OnGaTaClickListener listener;
    private boolean isAdmin;

    public interface OnGaTaClickListener {
        void onBookClick(GaTaItem item);
        void onItemClick(GaTaItem item);
        void onEditClick(GaTaItem item);
        void onDeleteClick(GaTaItem item);
    }

    public GaTaAdapter(List<GaTaItem> items, OnGaTaClickListener listener, boolean isAdmin) {
        this.items = items;
        this.listener = listener;
        this.isAdmin = isAdmin;
    }

    public void updateItems(List<GaTaItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GaTaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ga_ta, parent, false);
        return new GaTaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GaTaViewHolder holder, int position) {
        GaTaItem item = items.get(position);
        holder.bind(item, listener, isAdmin);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class GaTaViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView profileImage;
        private TextView name;
        private TextView course;
        private View availabilityIndicator;
        private MaterialButton bookButton;
        private MaterialButton editButton;
        private MaterialButton deleteButton;

        public GaTaViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.name);
            course = itemView.findViewById(R.id.course);
            availabilityIndicator = itemView.findViewById(R.id.availabilityIndicator);
            bookButton = itemView.findViewById(R.id.bookButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(GaTaItem item, OnGaTaClickListener listener, boolean isAdmin) {
            name.setText(item.getName());
            course.setText(item.getCourse());

            int colorRes = item.isAvailable() ?
                    android.R.color.holo_green_light :
                    android.R.color.holo_red_light;
            availabilityIndicator.setBackgroundResource(colorRes);

            if (isAdmin) {
                // Admin View
                bookButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);

                editButton.setOnClickListener(v -> listener.onEditClick(item));
                deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
            } else {
                // Student View
                bookButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);

                bookButton.setEnabled(item.isAvailable());
                bookButton.setText(item.isAvailable() ? "Book Appointment" : "Unavailable");
                bookButton.setOnClickListener(v -> {
                    if (item.isAvailable()) {
                        listener.onBookClick(item);
                    }
                });
            }

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
