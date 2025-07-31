package com.example.b07demosummer2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SafeLocationAdapter extends RecyclerView.Adapter<SafeLocationAdapter.ViewHolder> {

    private Context context;
    private List<SafeLocationInfo> locationList;
    private OnItemClickListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION; // Tracks selected item

    // Constructor
    public SafeLocationAdapter(Context context, List<SafeLocationInfo> locationList, OnItemClickListener listener) {
        this.context = context;
        this.locationList = locationList;
        this.listener = listener;
    }

    // Interface for item click callback
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method to set selected item and refresh UI
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.safe_location_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SafeLocationInfo location = locationList.get(position);
        holder.address.setText("Address: " + location.getAddress());
        holder.notes.setText("Notes: " + location.getNotes());

        // Apply border highlight for selected item
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.contact_selected_border);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.contact_normal_border);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView address, notes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.locationAddress);
            notes = itemView.findViewById(R.id.locationNotes);
        }
    }
}


