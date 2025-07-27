package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class EditSafeLocationActivity extends AppCompatActivity implements SafeLocationAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SafeLocationAdapter adapter;
    private List<SafeLocationInfo> locationList;
    private DatabaseReference locationsRef;
    private SafeLocationInfo selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_safe_location);

        recyclerView = findViewById(R.id.editSafeLocationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationList = new ArrayList<>();
        adapter = new SafeLocationAdapter(this, locationList, this);
        recyclerView.setAdapter(adapter);

        locationsRef = FirebaseDatabase.getInstance().getReference("users/user1/safeLocations");

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
                Toast.makeText(EditSafeLocationActivity.this, "Failed to load locations.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        selectedLocation = locationList.get(position);
        adapter.setSelectedPosition(position); // highlight selection
        showEditDialog(selectedLocation);       // open edit dialog
    }

    private void showEditDialog(SafeLocationInfo location) {
        // Inflate custom dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_safe_location, null);
        EditText editAddress = dialogView.findViewById(R.id.editSafeLocationAddress);
        EditText editNotes = dialogView.findViewById(R.id.editSafeLocationNotes);

        // Pre-fill fields
        editAddress.setText(location.getAddress());
        editNotes.setText(location.getNotes());

        // Build dialog
        new AlertDialog.Builder(this)
                .setTitle("Edit Safe Location")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newAddress = editAddress.getText().toString().trim();
                    String newNotes = editNotes.getText().toString().trim();

                    if (!newAddress.isEmpty()) {
                        location.setAddress(newAddress);
                        location.setNotes(newNotes);
                        updateLocation(location);
                    } else {
                        Toast.makeText(this, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateLocation(SafeLocationInfo location) {
        if (location.getId() != null) {
            locationsRef.child(location.getId()).setValue(location)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
