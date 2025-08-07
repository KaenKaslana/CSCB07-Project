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

/**
 * {DeleteSafeLocationActivity} displays a list of the user’s saved safe locations
 * and allows the user to select one and delete it from Firebase Realtime Database.
 *
 * Functionality:
 * 
 *   Checks that the user is signed in; if not, it prompts and exits.
 *   Loads all entries under "/Users/{uid}/safeLocations" into a RecyclerView.
 *   Highlights a tapped location and enables the confirm‐delete Button.
 *   Removes the selected location from Firebase when confirmed.
 * 
 */
public class DeleteSafeLocationActivity extends AppCompatActivity implements SafeLocationAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SafeLocationAdapter adapter;
    private List<SafeLocationInfo> locationList;
    private DatabaseReference locationsRef;
    private Button confirmDeleteButton;
    private SafeLocationInfo selectedLocation; // Selected location
    private FirebaseAuth mAuth;

    /**
     * Called when the activity is first created.
     * Sets up the UI, checks authentication, initializes Firebase references,
     * loads the list of locations, and configures the delete‐confirmation listener.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied; otherwise null.
     */
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

    /**
     * Attaches a {ValueEventListener} to the Firebase reference to
     * fetch all safe locations and update the RecyclerView whenever data changes.
     */
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

    /**
     * Callback from {SafeLocationAdapter.OnItemClickListener}.
     * Highlights the clicked item, stores it for deletion,
     * and enables the confirm delete button.
     *
     * @param position The adapter position of the clicked location.
     */
    @Override
    public void onItemClick(int position) {
        selectedLocation = locationList.get(position);
        adapter.setSelectedPosition(position);  // Highlight selected item
        confirmDeleteButton.setEnabled(true);   // Enable delete button
    }

    /**
     * Deletes the user’s currently selected location from Firebase.
     * On success, shows a toast, resets selection, and reloads the list.
     * On failure, displays the error message.
     */
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


