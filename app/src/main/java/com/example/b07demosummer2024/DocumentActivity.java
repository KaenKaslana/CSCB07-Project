package com.example.b07demosummer2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07demosummer2024.DocumentAdapter;
import com.example.b07demosummer2024.DocumentInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class DocumentActivity extends AppCompatActivity implements DocumentAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private FloatingActionButton uploadButton;
    private DocumentAdapter adapter;
    private List<DocumentInfo> documentList;
    private StorageReference storageRef;
    private DatabaseReference metadataRef;

    // SAF Launcher
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleFileSelection);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        recyclerView = findViewById(R.id.documentsRecyclerView);
        uploadButton = findViewById(R.id.uploadFab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        documentList = new ArrayList<>();
        adapter = new DocumentAdapter(this, documentList, this);
        recyclerView.setAdapter(adapter);

        storageRef = FirebaseStorage.getInstance().getReference("user1/documents");
        metadataRef = FirebaseDatabase.getInstance().getReference("users/user1/documentsMeta");

        loadDocumentsFromFirebase();

        uploadButton.setOnClickListener(v -> openLocalFilePicker());
    }

    private void openLocalFilePicker() {
        filePickerLauncher.launch("*/*"); // Pick any file type, restricted to local
    }

    private void handleFileSelection(Uri fileUri) {
        if (fileUri != null) {
            uploadFileToFirebase(fileUri);
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFileToFirebase(Uri fileUri) {
        if (fileUri == null) {
            Toast.makeText(this, "File URI is null!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique file name
        String fileName = "doc_" + System.currentTimeMillis();
        StorageReference fileRef = FirebaseStorage.getInstance()
                .getReference()
                .child("user1/documents/" + fileName);

        // Debug log for the upload path
        Log.d("UPLOAD_DEBUG", "Uploading to: " + fileRef.toString());
        Toast.makeText(this, "Uploading to: " + fileRef.getPath(), Toast.LENGTH_SHORT).show();

        // Begin upload
        fileRef.putFile(fileUri)
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    Log.d("UPLOAD_DEBUG", "Upload progress: " + progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("UPLOAD_DEBUG", "Upload success!");
                    fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        saveFileMetadata(fileName, downloadUri.toString());
                        Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("UPLOAD_DEBUG", "Upload failed: " + e.getMessage(), e);
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveFileMetadata(String fileName, String downloadUrl) {
        String id = metadataRef.push().getKey();
        if (id != null) {
            DocumentInfo docInfo = new DocumentInfo(id, fileName, downloadUrl);
            metadataRef.child(id).setValue(docInfo);
        }
    }

    private void loadDocumentsFromFirebase() {
        metadataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                documentList.clear();
                for (DataSnapshot docSnapshot : snapshot.getChildren()) {
                    DocumentInfo doc = docSnapshot.getValue(DocumentInfo.class);
                    if (doc != null) {
                        documentList.add(doc);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DocumentActivity.this, "Failed to load documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        DocumentInfo selectedDoc = documentList.get(position);
        Intent intent = new Intent(this, DocumentDetailActivity.class);
        intent.putExtra("docId", selectedDoc.getId());
        intent.putExtra("docName", selectedDoc.getName());
        intent.putExtra("docUrl", selectedDoc.getUrl());
        startActivity(intent);
    }
}


