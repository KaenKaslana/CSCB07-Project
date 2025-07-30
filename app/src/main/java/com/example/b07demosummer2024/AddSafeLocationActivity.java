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

public class AddSafeLocationActivity extends AppCompatActivity {

    private EditText addressInput, notesInput;
    private Button addButton;
    private DatabaseReference locationsRef;
    private FirebaseAuth mAuth;

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
