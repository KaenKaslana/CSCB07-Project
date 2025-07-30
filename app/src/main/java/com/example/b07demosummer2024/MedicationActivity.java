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

public class MedicationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private List<MedicationInfo> medicationList;
    private DatabaseReference medicationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        recyclerView = findViewById(R.id.medicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicationList = new ArrayList<>();
        adapter = new MedicationAdapter(this, medicationList, null);
        recyclerView.setAdapter(adapter);

        medicationsRef = FirebaseDatabase.getInstance().getReference("users/user1/medications");

        // Load medications
        loadMedications();

        // Button listeners
        Button addButton = findViewById(R.id.addMedicationButton);
        Button editButton = findViewById(R.id.editMedicationButton);
        Button deleteButton = findViewById(R.id.deleteMedicationButton);

        addButton.setOnClickListener(v -> startActivity(new Intent(this, AddMedicationActivity.class)));
        editButton.setOnClickListener(v -> startActivity(new Intent(this, EditMedicationActivity.class)));
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
                Toast.makeText(MedicationActivity.this, "Failed to load medications.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
