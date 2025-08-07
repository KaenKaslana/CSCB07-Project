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
 * {DeleteMedicationActivity} displays the user’s list of medications
 * and allows selecting one to delete.  It:
 * 
 *   Verifies the user is logged in.
 *   Loads medications from "/Users/{uid}/medications".
 *   Highlights the tapped item and enables the confirm button.
 *   Deletes the selected medication upon confirmation.
 *
 */
public class DeleteMedicationActivity extends AppCompatActivity implements MedicationAdapter.OnItemClickListener {

    /** RecyclerView showing current medications. */
    private RecyclerView recyclerView;

    /** Adapter binding MedicationInfo items to the list. */
    private MedicationAdapter adapter;

    /** Backing list of MedicationInfo objects. */
    private List<MedicationInfo> medicationList;

    /** Firebase reference to the user’s "medications" node. */
    private DatabaseReference medicationsRef;

    /** Button that, when tapped, deletes the selected medication. */
    private Button confirmDeleteButton;

    /** Holds the medication chosen for deletion. */
    private MedicationInfo selectedMedication;

    /** FirebaseAuth for ensuring the user is authenticated. */
    private FirebaseAuth mAuth;

    /**
     * Initializes UI elements, checks authentication, sets up the database reference,
     * loads the medication list, and hooks up the delete confirmation listener.
     *
     * @param savedInstanceState standard Bundle for activity state
     */
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

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        String uid = currentUser.getUid();
        medicationsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("medications");

        loadMedications();

        confirmDeleteButton.setOnClickListener(v -> {
            if (selectedMedication != null) {
                deleteSelectedMedication();
            } else {
                Toast.makeText(this, "Please select a medication to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Attaches a ValueEventListener to the Firebase reference to fetch and
     * update the medicationList in real time.
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
                Toast.makeText(DeleteMedicationActivity.this, "Failed to load medications.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Callback from {@link MedicationAdapter.OnItemClickListener}.
     * Highlights the tapped medication and enables the delete button.
     *
     * @param position position of the clicked item in the adapter
     */
    @Override
    public void onItemClick(int position) {
        selectedMedication = medicationList.get(position);
        adapter.setSelectedPosition(position);
        confirmDeleteButton.setEnabled(true);
    }

    /**
     * Deletes the currently selected medication from Firebase.  On success,
     * shows a confirmation toast, resets the selection, and reloads the list.
     * On failure, displays the error message.
     */
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
