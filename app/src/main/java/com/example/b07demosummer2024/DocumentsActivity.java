package com.example.b07demosummer2024;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DocumentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v ->
                Toast.makeText(DocumentsActivity.this, "Upload feature coming soon!", Toast.LENGTH_SHORT).show()
        );
    }
}