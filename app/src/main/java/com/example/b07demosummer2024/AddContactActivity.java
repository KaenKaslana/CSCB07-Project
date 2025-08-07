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
 * {AddContactActivity} presents a form for adding a new emergency contact.
 * 
 * Users must be signed in; otherwise they are redirected back. Upon submission,
 * the activity validates the required fields (name and phone), generates a unique
 * key, and writes a {ContactInfo} object into the
 * "/Users/{uid}/emergencyContacts/{contactId}" node in Firebase Realtime Database.
 * 
 */

public class AddContactActivity extends AppCompatActivity {

    /** Input field for the contact’s name (required), relationship (required), phone number (required) and address (required). */
    private EditText nameInput, relationshipInput, phoneInput, addressInput;
    
    /** Button that triggers adding the contact to Firebase. */
    private Button addContactButton;
    
    /** Reference to the “emergencyContacts” node under the current user. */
    private DatabaseReference contactsRef;
    
    /** The FirebaseAuth instance used to obtain the current user’s UID. */
    private FirebaseAuth mAuth;

    /**
     * Initializes the form UI, checks for a signed-in user, and
     * sets up the Firebase Database reference and button listener.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains
     *                           the data it most recently supplied. Otherwise it is null.
     */
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // UI widgets
        nameInput = findViewById(R.id.nameInput);
        relationshipInput = findViewById(R.id.relationshipInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        addContactButton = findViewById(R.id.addContactButton);

        // Ensure a user is signed in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        
        // Build database path: /Users/{uid}/emergencyContacts
        String uid = currentUser.getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("emergencyContacts");

        // When clicked, attempt to add the contact
        addContactButton.setOnClickListener(v -> addContactToFirebase());
    }


    /**
     * Reads form values, validates required fields, creates a {ContactInfo}
     * object, and writes it under a unique key in Firebase Realtime Database.
     * <p>
     * If the name or phone is missing, shows an error toast and aborts.
     * On success, shows confirmation and closes the activity.
     * On failure, shows the error message.
     * </p>
     */
    
    private void addContactToFirebase() {
        String name = nameInput.getText().toString().trim();
        String relationship = relationshipInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Name and Phone are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique key for each contact
        String contactId = contactsRef.push().getKey();
        ContactInfo contact = new ContactInfo(name, relationship, phone, address);

        // Write the ContactInfo object to the database
        contactsRef.child(contactId).setValue(contact)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contact added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after adding
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add contact: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

