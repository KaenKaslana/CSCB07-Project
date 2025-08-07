package com.example.b07demosummer2024;

import android.content.Intent;
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
 * Activity that displays the user's saved safe locations and provides
 * navigation to add, edit, or delete entries.
 * 
 * Loads {SafeLocationInfo} objects from Firebase Realtime Database,
 * binds them to a RecyclerView via {SafeLocationAdapter}, and
 * implements {SafeLocationAdapter.OnItemClickListener} to handle
 * item selections.
 * 
 */
public class SafeLocationsActivity extends AppCompatActivity implements SafeLocationAdapter.OnItemClickListener {

    /** FirebaseAuth instance for checking user authentication. */
    private FirebaseAuth mAuth;

    /** RecyclerView for displaying the list of safe locations. */
    private RecyclerView recyclerView;

    /** Adapter for binding {@link SafeLocationInfo} items to the RecyclerView. */
    private SafeLocationAdapter adapter;

    /** Backing list of location models loaded from Firebase. */
    private List<SafeLocationInfo> locationList;

    /** Firebase Database reference pointing to the user's safeLocations node. */
    private DatabaseReference locationsRef;

    /**
     * Called when the activity is first created. Initializes FirebaseAuth,
     * verifies the signed-in user, configures the RecyclerView and action
     * buttons, and begins loading data from Firebase.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this contains
     *                           the data it most recently supplied; otherwise {@code null}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_locations);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String uid = user.getUid();
        locationsRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("safeLocations");

        recyclerView = findViewById(R.id.safeLocationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationList = new ArrayList<>();
        adapter = new SafeLocationAdapter(this, locationList, this);
        recyclerView.setAdapter(adapter);

        Button addButton = findViewById(R.id.addLocationButton);
        Button editButton = findViewById(R.id.editLocationButton);
        Button deleteButton = findViewById(R.id.deleteLocationButton);

        addButton.setOnClickListener(v -> startActivity(new Intent(this, AddSafeLocationActivity.class)));
        editButton.setOnClickListener(v -> startActivity(new Intent(this, EditSafeLocationActivity.class)));
        deleteButton.setOnClickListener(v -> startActivity(new Intent(this, DeleteSafeLocationActivity.class)));

        loadLocations();
    }

    /**
     * Attaches a ValueEventListener to the Firebase reference to load
     * all safe locations, update the local list, and refresh the adapter
     * whenever data changes.
     */
    private void loadLocations() {
        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SafeLocationInfo loc = ds.getValue(SafeLocationInfo.class);
                    if (loc != null) {
                        loc.setId(ds.getKey());
                        locationList.add(loc);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SafeLocationsActivity.this,
                        "Failed to load safe locations.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Callback invoked when a RecyclerView item is clicked.
     * Displays a toast showing the selected location's address.
     *
     * @param position Index of the clicked item in {@link #locationList}.
     */
    @Override
    public void onItemClick(int position) {
        SafeLocationInfo selectedLocation = locationList.get(position);
        Toast.makeText(this, "Selected: " + selectedLocation.getAddress(), Toast.LENGTH_SHORT).show();
    }
}
