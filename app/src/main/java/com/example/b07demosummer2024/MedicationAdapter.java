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
 * RecyclerView.Adapter for displaying a list of medications.
 * 
 * Binds {MedicationInfo} data to item views and handles selection highlighting
 * and click events via {OnItemClickListener}.
 * 
 */
public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {

    /** Context used for inflating item layouts. */
    private Context context;

    /** List of medications to display. */
    private List<MedicationInfo> medicationList;

    /** Listener for item click events. */
    private OnItemClickListener listener;

    /** Currently selected item position, or {RecyclerView#NO_POSITION} if none. */
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * Constructs a MedicationAdapter.
     *
     * @param context        Context for layout inflation.
     * @param medicationList List of {MedicationInfo} objects to display.
     * @param listener       {OnItemClickListener} to handle item clicks, or null.
     */
    public MedicationAdapter(Context context, List<MedicationInfo> medicationList, OnItemClickListener listener) {
        this.context = context;
        this.medicationList = medicationList;
        this.listener = listener;
    }

    /**
     * Updates the selected item position and refreshes the affected rows.
     *
     * @param position New selected position.
     */
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(position);
    }

    /**
     * Inflates the item view and returns a new ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.medication_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the item view and sets up click and highlight behavior.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicationInfo med = medicationList.get(position);
        holder.name.setText("Name: " + med.getName());
        holder.dosage.setText("Dosage: " + med.getDosage());

        // Highlight selected item
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.contact_selected_border);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.contact_normal_border);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    /**
     * Returns the total number of items.
     */
    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    /**
     * ViewHolder that holds references to item subviews.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, dosage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.medicationName);
            dosage = itemView.findViewById(R.id.medicationDosage);
        }
    }
}

