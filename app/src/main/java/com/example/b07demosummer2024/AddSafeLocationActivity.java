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
 * {AddSafeLocationActivity} displays a form that allows the user
 * to add a new “safe location” (address + optional notes) into Firebase
 * Realtime Database under their own profile.
 * 
 * On launch, it checks for an authenticated user; if none is present,
 * it shows a prompt and closes.  Upon successful submission, it pushes
 * a {SafeLocationInfo} object to:
 * 
 * /Users/{uid}/safeLocations/{locationId}
 * 
 * and then finishes the activity.
 * 
 */

public class AddSafeLocationActivity extends AppCompatActivity {

    /** Input field for the location’s address (required) and additional notes for this address. */
    private EditText addressInput, notesInput;

    /** Button to confirm adding the new safe location. */
    private Button addButton;

    /** Reference to the “safeLocations” node under the current user. */
    private DatabaseReference locationsRef;

    /** FirebaseAuth instance for checking the current signed-in user. */
    private FirebaseAuth mAuth;

    /**
     * Called when the activity is first created.  Sets up the UI bindings,
     * verifies that a user is logged in, initializes the database reference,
     * and installs the click listener for the “Add” button.
     *
     * @param savedInstanceState If the activity is being re-initialized
     *                           after previously being shut down, this Bundle
     *                           contains the data it most recently supplied;
     *                           otherwise it is null.
     */
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_safe_location);

        addressInput = findViewById(R.id.addLocationAddress);
        notesInput = findViewById(R.id.addLocationNotes);
        addButton = findViewById(R.id.addLocationConfirmButton);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        String uid = currentUser.getUid();
        locationsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("safeLocations");

        addButton.setOnClickListener(v -> addSafeLocation());
    }

    /**
     * Reads and trims the address and notes fields, validates that the address
     * is non-empty, then creates a {SafeLocationInfo} object and writes
     * it under a unique key in Firebase.  Shows a toast on success and closes
     * the activity; on failure, shows an error toast.
     */
    
    private void addSafeLocation() {
        String address = addressInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = locationsRef.push().getKey();
        SafeLocationInfo newLocation = new SafeLocationInfo(address, notes);
        locationsRef.child(id).setValue(newLocation)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Location added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
