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

public class MedicationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private List<MedicationInfo> medicationList;
    private DatabaseReference medicationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = user.getUid();
        medicationsRef = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(uid)
                .child("medications");

        recyclerView = findViewById(R.id.medicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicationList = new ArrayList<>();
        adapter = new MedicationAdapter(this, medicationList, null);
        recyclerView.setAdapter(adapter);

        loadMedications();

        Button addButton    = findViewById(R.id.addMedicationButton);
        Button editButton   = findViewById(R.id.editMedicationButton);
        Button deleteButton = findViewById(R.id.deleteMedicationButton);

        addButton   .setOnClickListener(v -> startActivity(new Intent(this, AddMedicationActivity.class)));
        editButton  .setOnClickListener(v -> startActivity(new Intent(this, EditMedicationActivity.class)));
        deleteButton.setOnClickListener(v -> startActivity(new Intent(this, DeleteMedicationActivity.class)));
    }

    private void loadMedications() {
        medicationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medicationList.clear();
                for (DataSnapshot medSnapshot : snapshot.getChildren()) {
                    MedicationInfo med = medSnapshot.getValue(MedicationInfo.class);
                    if (med != null) {
                        med.setId(medSnapshot.getKey());
                        medicationList.add(med);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MedicationActivity.this,
                        "Failed to load medications: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
