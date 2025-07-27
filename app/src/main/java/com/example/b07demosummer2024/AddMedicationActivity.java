package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMedicationActivity extends AppCompatActivity {

    private EditText nameInput, dosageInput;
    private Button addButton;
    private DatabaseReference medicationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        nameInput = findViewById(R.id.inputMedicationName);
        dosageInput = findViewById(R.id.inputMedicationDosage);
        addButton = findViewById(R.id.addMedicationConfirmButton);

        medicationsRef = FirebaseDatabase.getInstance().getReference("users/user1/medications");

        addButton.setOnClickListener(v -> addMedication());
    }

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
