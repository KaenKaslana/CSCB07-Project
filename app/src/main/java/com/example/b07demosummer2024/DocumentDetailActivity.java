package com.example.b07demosummer2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DocumentDetailActivity extends AppCompatActivity {

    private TextView documentNameText;
    private Button openButton, deleteButton;
    private String docId, docName, docUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        documentNameText = findViewById(R.id.documentNameText);
        openButton = findViewById(R.id.openDocumentButton);
        deleteButton = findViewById(R.id.deleteDocumentButton);

        // Get document details from Intent
        Intent intent = getIntent();
        docId = intent.getStringExtra("docId");
        docName = intent.getStringExtra("docName");
        docUrl = intent.getStringExtra("docUrl");

        documentNameText.setText(docName);

        openButton.setOnClickListener(v -> openDocument());
        deleteButton.setOnClickListener(v -> deleteDocument());
    }

    /**
     * Open the document using native Android apps or browser.
     */
    private void openDocument() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(docUrl), "*/*"); // Allow any file type
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open Document"));
        } catch (Exception e) {
            Toast.makeText(this, "No app found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Delete the document from Firebase Storage and remove metadata.
     */
    private void deleteDocument() {
        if (docUrl == null || docId == null) {
            Toast.makeText(this, "Invalid document data.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete from Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(docUrl);
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove metadata from Realtime Database
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance()
                            .getReference("users/user1/documentsMeta")
                            .child(docId);
                    databaseRef.removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Document deleted.", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to delete metadata: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete document: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}


