package com.example.b07demosummer2024;

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

public class DeleteMedicationActivity extends AppCompatActivity implements MedicationAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private List<MedicationInfo> medicationList;
    private DatabaseReference medicationsRef;
    private Button confirmDeleteButton;
    private MedicationInfo selectedMedication;  // Selected medication for deletion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_medication);

        recyclerView = findViewById(R.id.deleteMedicationsRecyclerView);
        confirmDeleteButton = findViewById(R.id.confirmDeleteMedicationButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicationList = new ArrayList<>();
        adapter = new MedicationAdapter(this, medicationList, this);
        recyclerView.setAdapter(adapter);

        medicationsRef = FirebaseDatabase.getInstance().getReference("users/user1/medications");

        loadMedications();

        confirmDeleteButton.setOnClickListener(v -> {
            if (selectedMedication != null) {
                deleteSelectedMedication();
            } else {
                Toast.makeText(this, "Please select a medication to delete", Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(DeleteMedicationActivity.this, "Failed to load medications.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        selectedMedication = medicationList.get(position);
        adapter.setSelectedPosition(position);
        confirmDeleteButton.setEnabled(true);
    }

    private void deleteSelectedMedication() {
        if (selectedMedication != null && selectedMedication.getId() != null) {
            medicationsRef.child(selectedMedication.getId())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Medication deleted successfully", Toast.LENGTH_SHORT).show();
                        selectedMedication = null;
                        confirmDeleteButton.setEnabled(false);
                        loadMedications(); // Reload list
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
}
