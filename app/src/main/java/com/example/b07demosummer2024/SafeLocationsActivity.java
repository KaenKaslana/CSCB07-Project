package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SafeLocationsActivity extends AppCompatActivity implements SafeLocationAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SafeLocationAdapter adapter;
    private List<SafeLocationInfo> locationList;
    private DatabaseReference locationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_locations); // We'll create this XML next

        recyclerView = findViewById(R.id.safeLocationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationList = new ArrayList<>();
        adapter = new SafeLocationAdapter(this, locationList, this);
        recyclerView.setAdapter(adapter);

        // Firebase reference
        locationsRef = FirebaseDatabase.getInstance().getReference("users/user1/safeLocations");

        Button addButton = findViewById(R.id.addLocationButton);
        Button editButton = findViewById(R.id.editLocationButton);
        Button deleteButton = findViewById(R.id.deleteLocationButton);

        // Navigate to Add, Edit, and Delete Activities
        addButton.setOnClickListener(v -> startActivity(new Intent(this, AddSafeLocationActivity.class)));
        editButton.setOnClickListener(v -> startActivity(new Intent(this, EditSafeLocationActivity.class)));
        deleteButton.setOnClickListener(v -> startActivity(new Intent(this, DeleteSafeLocationActivity.class)));

        // Load existing safe locations
        loadLocations();
    }

    private void loadLocations() {
        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationList.clear();
                for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                    SafeLocationInfo location = locationSnapshot.getValue(SafeLocationInfo.class);
                    if (location != null) {
                        location.setId(locationSnapshot.getKey());
                        locationList.add(location);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SafeLocationsActivity.this, "Failed to load safe locations.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        // For now, clicking an item just shows a Toast. We can expand this later if needed.
        SafeLocationInfo selectedLocation = locationList.get(position);
        Toast.makeText(this, "Selected: " + selectedLocation.getAddress(), Toast.LENGTH_SHORT).show();
    }
}

