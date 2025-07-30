package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button documentsButton = findViewById(R.id.documentsButton);
        documentsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, DocumentActivity.class);
            startActivity(intent);
        });

        Button emergencyContactsButton = findViewById(R.id.contactsButton);
        emergencyContactsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, EmergencyContactsActivity.class);
            startActivity(intent);
        });

        Button safeLocationsButton = findViewById(R.id.locationsButton);
        safeLocationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, SafeLocationsActivity.class);
            startActivity(intent);
        });

        Button medicationsButton = findViewById(R.id.medicationsButton);
        medicationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, MedicationActivity.class);
            startActivity(intent);
        });
    }
}