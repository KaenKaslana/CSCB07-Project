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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditSafeLocationActivity extends AppCompatActivity
        implements SafeLocationAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SafeLocationAdapter adapter;
    private List<SafeLocationInfo> locationList;
    private DatabaseReference locationsRef;
    private FirebaseAuth mAuth;
    private SafeLocationInfo selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_safe_location);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = user.getUid();
        locationsRef = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(uid)
                .child("safeLocations");

        recyclerView = findViewById(R.id.editSafeLocationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationList = new ArrayList<>();
        adapter = new SafeLocationAdapter(this, locationList, this);
        recyclerView.setAdapter(adapter);

        loadLocations();
    }

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
                Toast.makeText(EditSafeLocationActivity.this,
                        "Failed to load locations.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        selectedLocation = locationList.get(position);
        adapter.setSelectedPosition(position);
        showEditDialog(selectedLocation);
    }

    private void showEditDialog(SafeLocationInfo location) {
        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_edit_safe_location, null);
        EditText editAddress = dialogView.findViewById(R.id.editSafeLocationAddress);
        EditText editNotes   = dialogView.findViewById(R.id.editSafeLocationNotes);

        editAddress.setText(location.getAddress());
        editNotes.setText(location.getNotes());

        new AlertDialog.Builder(this)
                .setTitle("Edit Safe Location")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newAddress = editAddress.getText().toString().trim();
                    String newNotes   = editNotes.getText().toString().trim();
                    if (newAddress.isEmpty()) {
                        Toast.makeText(this,
                                "Address cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    location.setAddress(newAddress);
                    location.setNotes(newNotes);
                    updateLocation(location);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateLocation(SafeLocationInfo location) {
        String id = location.getId();
        if (id == null) {
            Toast.makeText(this,
                    "Location ID missing.", Toast.LENGTH_SHORT).show();
            return;
        }
        locationsRef.child(id)
                .setValue(location)
                .addOnSuccessListener(a ->
                        Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Update failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}
