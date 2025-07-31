package com.example.b07demosummer2024;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChangeReminderActivity extends AppCompatActivity {
    private RecyclerView remindersRecyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private DatabaseReference remindersRef;

    private List <String>  reminderKeys;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_reminder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        String uid = currentUser.getUid();
        remindersRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("reminder");

        //accessing the recyclerview for the reminders and setting the layout
        remindersRecyclerView = findViewById(R.id.remindersRecyclerView);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //empty list for reminders and their keys
        reminderList = new ArrayList<>();
        reminderKeys = new ArrayList<>();
        //adapter takes list, keys and the database reference to the reminder node
        adapter = new ReminderAdapter(reminderList, reminderKeys,remindersRef);
        remindersRecyclerView.setAdapter(adapter);

        loadReminders();

    }

    private void loadReminders() {
        remindersRef.addValueEventListener(new ValueEventListener() {
            @Override
            //whenever a reminder is added/removed list is cleared, keys cleared and we loop through the database again to populate
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();
                reminderKeys.clear();//clear the existing list/keys and make new one
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    Reminder reminder = reminderSnapshot.getValue(Reminder.class);//converts to java
                    if (reminder != null) {
                        reminderList.add(reminder);
                        reminderKeys.add(reminderSnapshot.getKey());
                    }
                }
                adapter.notifyDataSetChanged();//updates our recyclerview
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChangeReminderActivity.this, "Failed to load reminders", Toast.LENGTH_SHORT).show();

            }
        });
    }
}