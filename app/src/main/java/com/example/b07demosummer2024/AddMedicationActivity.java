package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * {AddMedicationActivity} provides a form for the user to add a medication entry
 * (name + dosage) into Firebase Realtime Database under the current user’s profile.
 * 
 * When opened, the activity checks that a user is logged in; if not, it shows a prompt
 * and closes. On successful submission, it generates a unique key and writes a
 * {MedicationInfo} object to:
 * 
 * /Users/{uid}/medications/{medicationId}
 * 
 */

public class AddMedicationActivity extends AppCompatActivity {

    /** Text field for entering the medication’s name (required) and dosage (required). */
    private EditText nameInput, dosageInput;
    
    /** Button to confirm and upload the medication entry. */
    private Button addButton;

    /** Reference to the current user’s "medications" node in the database. */
    private DatabaseReference medicationsRef;

    /** FirebaseAuth instance used to verify and fetch the current user. */
    private FirebaseAuth mAuth;

    /**
     * Initializes the UI, ensures a user is logged in, sets up the
     * Firebase Database reference, and installs the click listener
     * for the confirmation button.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains
     *                           the data it most recently supplied; otherwise it is null.
     */
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        nameInput = findViewById(R.id.inputMedicationName);
        dosageInput = findViewById(R.id.inputMedicationDosage);
        addButton = findViewById(R.id.addMedicationConfirmButton);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        String uid = currentUser.getUid();
        medicationsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("medications");

        addButton.setOnClickListener(v -> addMedication());
    }

    /**
     * Reads and validates the name and dosage fields, then creates a
     * {MedicationInfo} object and writes it to Firebase under
     * a unique key. If either field is empty, shows a toast and aborts.
     * On success, shows confirmation toast and finishes the activity.
     * On failure, shows the exception message.
     */
    
    private void addMedication() {
        String name = nameInput.getText().toString().trim();
        String dosage = dosageInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dosage)) {
            Toast.makeText(this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = medicationsRef.push().getKey();
        MedicationInfo medication = new MedicationInfo(name, dosage);
        medicationsRef.child(id).setValue(medication)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Medication added!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
