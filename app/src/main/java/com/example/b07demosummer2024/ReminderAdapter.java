package com.example.b07demosummer2024;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminderList;
    private List<String> reminderKeys;
    private DatabaseReference remindersRef;

    // Constructor - pass in the data
    public ReminderAdapter(List<Reminder> reminderList, List<String> reminderKeys, DatabaseReference remindersRef ) {
        this.reminderList = reminderList;
        this.reminderKeys = reminderKeys;
        this.remindersRef = remindersRef;
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        // Declare all of the user interface elements (the type of reminder, time, day or date, edit button, delete button)
        TextView frequencyText, timeText, dayOrDate;
        ImageButton deleteButton;
        ImageButton editButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Connecting the data to the item xml views
            frequencyText = itemView.findViewById(R.id.frequencyText);
            timeText = itemView.findViewById(R.id.timeText);
            dayOrDate = itemView.findViewById(R.id.dayOrDate);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }

    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }//creates new row

    @Override
    //populates
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);

        // Always show frequency and time
        holder.frequencyText.setText(reminder.getFrequency().toUpperCase() + " REMINDER");
        holder.timeText.setText("Time: " + reminder.getTime());

        // Conditionally show day/date
        if ("weekly" .equals(reminder.getFrequency())) {
            holder.dayOrDate.setText("Day: " + reminder.getDay());
        } else if ("monthly" .equals(reminder.getFrequency())) {
            holder.dayOrDate.setText("Date: " + reminder.getDate());
        } else {
            holder.dayOrDate.setText(""); // Hide for daily
        }

        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                String key = reminderKeys.get(adapterPosition);
                WorkManager.getInstance(v.getContext()).cancelAllWorkByTag(key);
                remindersRef.child(key).removeValue().addOnSuccessListener(aVoid -> {
                            Toast.makeText(v.getContext(), "Reminder deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(v.getContext(), "Failed to delete reminder", Toast.LENGTH_SHORT).show();
                        });
            }
        });//if delete button is clicked, we find the key and then use removeValue() to delete, which will call the load() to change the recyclerview

        holder.editButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                String key = reminderKeys.get(adapterPosition);
                Reminder reminderCurr = reminderList.get(adapterPosition);

                Intent intent = new Intent(v.getContext(), CreateSchedule.class);//if edit button clicked, take us to CreateSchedule under edit mode
                intent.putExtra("EDIT_MODE", true);
                intent.putExtra("REMINDER_KEY", key);
                intent.putExtra("REMINDER_DATA", reminderCurr); // Pass the ENTIRE object, had to implement serializable in reminder class

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }


}




