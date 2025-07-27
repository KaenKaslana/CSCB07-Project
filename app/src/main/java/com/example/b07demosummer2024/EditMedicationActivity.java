package com.example.b07demosummer2024;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

public class EditMedicationActivity extends AppCompatActivity implements MedicationAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private List<MedicationInfo> medicationList;
    private DatabaseReference medicationsRef;
    private MedicationInfo selectedMedication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medication);

        recyclerView = findViewById(R.id.editMedicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicationList = new ArrayList<>();
        adapter = new MedicationAdapter(this, medicationList, this);
        recyclerView.setAdapter(adapter);

        medicationsRef = FirebaseDatabase.getInstance().getReference("users/user1/medications");

        loadMedications();
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
                Toast.makeText(EditMedicationActivity.this, "Failed to load medications.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        selectedMedication = medicationList.get(position);
        adapter.setSelectedPosition(position);
        showEditDialog(selectedMedication);
    }

    private void showEditDialog(MedicationInfo medication) {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_medication, null);
        EditText editName = dialogView.findViewById(R.id.editMedicationName);
        EditText editDosage = dialogView.findViewById(R.id.editMedicationDosage);

        // Populate fields with existing data
        editName.setText(medication.getName());
        editDosage.setText(medication.getDosage());

        // Build dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Medication")
                .setView(dialogView)
                .setPositiveButton("Update", (d, which) -> {
                    String newName = editName.getText().toString().trim();
                    String newDosage = editDosage.getText().toString().trim();

                    if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newDosage)) {
                        Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    updateMedication(medication.getId(), newName, newDosage);
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void updateMedication(String id, String name, String dosage) {
        MedicationInfo updatedMed = new MedicationInfo(name, dosage);
        medicationsRef.child(id)
                .setValue(updatedMed)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Medication updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

