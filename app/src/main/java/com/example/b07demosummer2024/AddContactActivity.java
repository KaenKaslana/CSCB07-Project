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

public class AddContactActivity extends AppCompatActivity {

    private EditText nameInput, relationshipInput, phoneInput, addressInput;
    private Button addContactButton;
    private DatabaseReference contactsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        nameInput = findViewById(R.id.nameInput);
        relationshipInput = findViewById(R.id.relationshipInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        addContactButton = findViewById(R.id.addContactButton);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        String uid = currentUser.getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("emergencyContacts");

        addContactButton.setOnClickListener(v -> addContactToFirebase());
    }

    private void addContactToFirebase() {
        String name = nameInput.getText().toString().trim();
        String relationship = relationshipInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Name and Phone are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique key for each contact
        String contactId = contactsRef.push().getKey();
        ContactInfo contact = new ContactInfo(name, relationship, phone, address);

        contactsRef.child(contactId).setValue(contact)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contact added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after adding
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add contact: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

