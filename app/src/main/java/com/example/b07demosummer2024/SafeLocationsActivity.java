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

public class SafeLocationsActivity extends AppCompatActivity implements SafeLocationAdapter.OnItemClickListener {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private SafeLocationAdapter adapter;
    private List<SafeLocationInfo> locationList;
    private DatabaseReference locationsRef;

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

    @Override
    public void onItemClick(int position) {
        SafeLocationInfo selectedLocation = locationList.get(position);
        Toast.makeText(this, "Selected: " + selectedLocation.getAddress(), Toast.LENGTH_SHORT).show();
    }
}
