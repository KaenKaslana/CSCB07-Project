package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

/**
 * Activity that displays the user's saved medications and allows editing them.
 * 
 * Loads a list of {MedicationInfo} objects from Firebase Realtime Database,
 * displays them in a RecyclerView, and opens an edit dialog when an item is clicked.
 * Updates are written back to the database.
 * 
 */
public class EditMedicationActivity extends AppCompatActivity implements MedicationAdapter.OnItemClickListener {

    /** RecyclerView displaying the list of medications for editing. */
    private RecyclerView recyclerView;

    /** Adapter for binding {MedicationInfo} items to the RecyclerView. */
    private MedicationAdapter adapter;

    /** Backing list of medications loaded from Firebase. */
    private List<MedicationInfo> medicationList;

    /** Firebase Database reference pointing to the current user's medications node. */
    private DatabaseReference medicationsRef;

    /** Firebase authentication instance for retrieving the current user. */
    private FirebaseAuth mAuth;

    /** Currently selected medication for editing. */
    private MedicationInfo selectedMedication;

    /**
     * Initializes the activity: checks authentication, sets up Firebase references,
     * configures RecyclerView, and loads medications.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this holds the data it most recently
     *                           supplied; otherwise {@code null}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medication);

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

        recyclerView = findViewById(R.id.editMedicationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicationList = new ArrayList<>();
        adapter = new MedicationAdapter(this, medicationList, this);
        recyclerView.setAdapter(adapter);

        loadMedications();
    }

    /**
     * Loads medications from Firebase and listens for data changes.
     * Updates {#medicationList} and notifies the adapter on change.
     */
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

    /**
     * Called when a medication item is clicked.
     * Saves the selected medication and opens the edit dialog.
     *
     * @param position Index of the clicked item in {@link #medicationList}.
     */
    @Override
    public void onItemClick(int position) {
        selectedMedication = medicationList.get(position);
        adapter.setSelectedPosition(position);
        showEditDialog(selectedMedication);
    }

    /**
     * Displays an AlertDialog to edit the details of the given medication.
     * Validates input and calls {#updateMedication(String, String, String)} on confirmation.
     *
     * @param medication The {MedicationInfo} object to be edited.
     */
    private void showEditDialog(MedicationInfo medication) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_medication, null);
        EditText editName   = dialogView.findViewById(R.id.editMedicationName);
        EditText editDosage = dialogView.findViewById(R.id.editMedicationDosage);

        editName.setText(medication.getName());
        editDosage.setText(medication.getDosage());

        new AlertDialog.Builder(this)
                .setTitle("Edit Medication")
                .setView(dialogView)
                .setPositiveButton("Update", (d, which) -> {
                    String newName   = editName.getText().toString().trim();
                    String newDosage = editDosage.getText().toString().trim();
                    if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newDosage)) {
                        Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateMedication(medication.getId(), newName, newDosage);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Writes the updated medication data back to Firebase under the given ID.
     * Shows a toast on success or failure.
     *
     * @param id     Firebase key of the medication record.
     * @param name   New medication name.
     * @param dosage New medication dosage instructions.
     */
    private void updateMedication(String id, String name, String dosage) {
        MedicationInfo updatedMed = new MedicationInfo(name, dosage);
        medicationsRef.child(id)
                .setValue(updatedMed)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Medication updated!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
