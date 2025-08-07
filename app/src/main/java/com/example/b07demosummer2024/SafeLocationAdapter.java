package com.example.b07demosummer2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView.Adapter for displaying a list of safe locations.
 * 
 * Binds {SafeLocationInfo} data to item views and manages selection highlighting
 * and click events through {OnItemClickListener}.
 */
public class SafeLocationAdapter extends RecyclerView.Adapter<SafeLocationAdapter.ViewHolder> {

    /** Context used for inflating views. */
    private Context context;

    /** List of safe locations to display. */
    private List<SafeLocationInfo> locationList;

    /** Listener for handling item click events. */
    private OnItemClickListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION; // Tracks selected item

    /**
     * Constructs a new SafeLocationAdapter.
     *
     * @param context       Context for inflating layout resources.
     * @param locationList  Data set of {@link SafeLocationInfo} to display.
     * @param listener      Listener to receive click events, may be null.
     */
    public SafeLocationAdapter(Context context, List<SafeLocationInfo> locationList, OnItemClickListener listener) {
        this.context = context;
        this.locationList = locationList;
        this.listener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when an item is clicked.
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * Updates the selected item position, refreshing the old and new items.
     *
     * @param position New position to select.
     */
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    /**
     * Inflates the item view and creates a ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.safe_location_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the item view and handles selection highlighting and clicks.
     */
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

    /**
     * Returns the total number of items in the data set.
     */
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


