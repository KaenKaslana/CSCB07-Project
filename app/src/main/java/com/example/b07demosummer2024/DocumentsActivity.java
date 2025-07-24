package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DocumentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        // Find buttons by ID
        Button selectFileButton = findViewById(R.id.selectFileButton);
        Button uploadFileButton = findViewById(R.id.uploadFileButton);

        // Listener for "Select File"
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DocumentsActivity.this, "Select File feature clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener for "Upload File"
        uploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DocumentsActivity.this, "Upload feature clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}