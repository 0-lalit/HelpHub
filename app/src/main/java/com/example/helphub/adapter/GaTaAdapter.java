package com.example.helphub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.helphub.R;
import com.example.helphub.model.GaTa;
import java.util.ArrayList;
import java.util.List;

public class GaTaAdapter extends RecyclerView.Adapter<GaTaAdapter.GaTaViewHolder> implements Filterable {
    private List<GaTa> gaTaList;
    private List<GaTa> gaTaListFull;
    private OnItemClickListener listener;
    private boolean isAdminView;

    public interface OnItemClickListener {
        void onItemClick(GaTa gaTa);
    }

    public GaTaAdapter(List<GaTa> gaTaList, boolean isAdminView) {
        this.gaTaList = gaTaList != null ? gaTaList : new ArrayList<>();
        this.gaTaListFull = new ArrayList<>(this.gaTaList);
        this.isAdminView = isAdminView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GaTaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gata, parent, false);
        return new GaTaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GaTaViewHolder holder, int position) {
        GaTa gaTa = gaTaList.get(position);
        holder.nameTextView.setText(gaTa.getName());
        holder.roleTextView.setText(gaTa.getRole());
        holder.courseTextView.setText(gaTa.getCourse());
        holder.officeHoursTextView.setText(gaTa.getOfficeHours());
        holder.officeLocationTextView.setText(gaTa.getOfficeLocation());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(gaTa);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gaTaList.size();
    }

    public void updateList(List<GaTa> newList) {
        this.gaTaList = newList != null ? newList : new ArrayList<>();
        this.gaTaListFull = new ArrayList<>(this.gaTaList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<GaTa> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(gaTaListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (GaTa gaTa : gaTaListFull) {
                        if (gaTa.getName().toLowerCase().contains(filterPattern) ||
                            gaTa.getCourse().toLowerCase().contains(filterPattern) ||
                            gaTa.getRole().toLowerCase().contains(filterPattern)) {
                            filteredList.add(gaTa);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                gaTaList.clear();
                gaTaList.addAll((List<GaTa>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    static class GaTaViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView roleTextView;
        TextView courseTextView;
        TextView officeHoursTextView;
        TextView officeLocationTextView;

        GaTaViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.gata_name);
            roleTextView = itemView.findViewById(R.id.gata_role);
            courseTextView = itemView.findViewById(R.id.gata_course);
            officeHoursTextView = itemView.findViewById(R.id.gata_office_hours);
            officeLocationTextView = itemView.findViewById(R.id.gata_office_location);
        }
    }
} 