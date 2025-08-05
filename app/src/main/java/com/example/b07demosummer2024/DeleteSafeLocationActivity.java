package com.example.b07demosummer2024;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class DeleteSafeLocationActivity extends AppCompatActivity implements SafeLocationAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SafeLocationAdapter adapter;
    private List<SafeLocationInfo> locationList;
    private DatabaseReference locationsRef;
    private Button confirmDeleteButton;
    private SafeLocationInfo selectedLocation; // Selected location
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_safe_location);

        recyclerView = findViewById(R.id.deleteSafeLocationRecyclerView);
        confirmDeleteButton = findViewById(R.id.confirmDeleteButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationList = new ArrayList<>();
        adapter = new SafeLocationAdapter(this, locationList, this);
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        String uid = currentUser.getUid();
        locationsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("safeLocations");

        loadLocations();

        confirmDeleteButton.setOnClickListener(v -> {
            if (selectedLocation != null) {
                deleteSelectedLocation();
            } else {
                Toast.makeText(DeleteSafeLocationActivity.this, "Please select a location to delete", Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(DeleteSafeLocationActivity.this, "Failed to load locations.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        selectedLocation = locationList.get(position);
        adapter.setSelectedPosition(position);  // Highlight selected item
        confirmDeleteButton.setEnabled(true);   // Enable delete button
    }

    private void deleteSelectedLocation() {
        if (selectedLocation != null && selectedLocation.getId() != null) {
            locationsRef.child(selectedLocation.getId())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Location deleted successfully", Toast.LENGTH_SHORT).show();
                        selectedLocation = null;
                        confirmDeleteButton.setEnabled(false);
                        loadLocations(); // Reload list
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
}


