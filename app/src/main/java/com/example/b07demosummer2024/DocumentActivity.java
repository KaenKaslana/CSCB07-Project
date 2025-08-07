package com.example.b07demosummer2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * DocumentActivity manages the user interface for viewing, uploading, renaming, and deleting documents.
 * 
 * It implements the following primary functions:
 * 
 *   Display a scrollable list of DocumentInfo items from Firebase.
 *   Allow the user to pick a local file and upload its metadata to Firebase.
 *   Enable selection highlighting for documents in the list.
 *   Present a dialog to rename a selected document and update Firebase.
 *   Delete the selected document metadata from Firebase upon user confirmation.
 * 
 * Implements DocumentAdapter.OnItemClickListener to handle list item taps.
 */
public class DocumentActivity extends AppCompatActivity implements DocumentAdapter.OnItemClickListener {

    private static final int PICK_FILE_REQUEST = 1001;

    private RecyclerView recyclerView;
    private FloatingActionButton uploadButton;
    private FloatingActionButton deleteButton;

    private FloatingActionButton renameButton;

    private DocumentAdapter adapter;
    private List<DocumentInfo> documentList;
    private DatabaseReference documentsRef;

    private DocumentInfo selectedDocument = null;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private FirebaseAuth mAuth;

    /**
     * Called when the activity is first created.
     * 
     * Initializes UI components, checks authentication, sets up Firebase references,
     * loads existing documents, and configures button listeners for upload, delete,
     * and rename operations.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied; otherwise null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        recyclerView = findViewById(R.id.documentsRecyclerView);
        uploadButton = findViewById(R.id.uploadFab);
        deleteButton = findViewById(R.id.deleteFab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        documentList = new ArrayList<>();
        adapter = new DocumentAdapter(this, documentList, this);
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        String uid = currentUser.getUid();
        documentsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("documents");

        // Load existing documents
        loadDocumentsFromFirebase();

        // Upload button - unchanged
        uploadButton.setOnClickListener(v -> openLocalFilePicker());

        // Delete button - delete the selected document
        deleteButton.setOnClickListener(v -> {
            if (selectedDocument != null) {
                deleteSelectedDocument(selectedDocument);
            } else {
                Toast.makeText(this, "Please select a document to delete.", Toast.LENGTH_SHORT).show();
            }
        });

        // Rename button - rename the selected document
        renameButton = findViewById(R.id.renameFab);

        renameButton.setOnClickListener(v -> {
            if (selectedDocument != null) {
                showRenameDialog(selectedDocument);
            } else {
                Toast.makeText(this, "Please select a document to rename.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Displays an AlertDialog with an EditText input to rename the given document.
     * 
     * When "Rename" is tapped, calls {@link #renameDocument(DocumentInfo, String)}.
     *
     * @param document the DocumentInfo object to rename
     */
    private void showRenameDialog(DocumentInfo document) {
        // Create a simple EditText dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Rename Document");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Enter new name");
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                renameDocument(document, newName);
            } else {
                Toast.makeText(this, "Name cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Renames the specified document in Firebase by removing the old key and writing
     * a new DocumentInfo under the new name.
     *
     * @param document the original DocumentInfo to rename
     * @param newName  the new name to assign
     */
    private void renameDocument(DocumentInfo document, String newName) {
        // Remove the old document entry
        documentsRef.child(document.name)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Create a new entry with the updated name
                    DocumentInfo renamedDoc = new DocumentInfo(newName, document.uri);
                    documentsRef.child(newName)
                            .setValue(renamedDoc)
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, "Document renamed successfully.", Toast.LENGTH_SHORT).show();
                                loadDocumentsFromFirebase(); // Refresh list
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Rename failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete old name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /** Opens the device file picker to select a document **/
    private void openLocalFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    /**
     * Receives the chosen file URI and calls {@link #uploadFileToFirebase(Uri)}.
     *
     * @param requestCode the original request code
     * @param resultCode  the result code indicating success or cancellation
     * @param data        the Intent containing the chosen file's URI
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                uploadFileToFirebase(fileUri);
            } else {
                Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Uploads file metadata (name + URI) to Firebase Realtime Database **/
    private void uploadFileToFirebase(Uri fileUri) {
        if (fileUri == null) {
            Toast.makeText(this, "File URI is null!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "doc_" + System.currentTimeMillis();
        DocumentInfo docInfo = new DocumentInfo(fileName, fileUri.toString());

        documentsRef.child(fileName)
                .setValue(docInfo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "File metadata uploaded!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("UPLOAD_DEBUG", "Upload failed: " + e.getMessage(), e);
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /** Loads documents from Firebase and updates the RecyclerView **/
    private void loadDocumentsFromFirebase() {
        documentsRef.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(DocumentActivity.this,
                        "Failed to read data: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Called when a document list item is tapped.
     * Highlights the item and stores it for rename/delete operations.
     *
     * @param position the adapter position of the tapped item
     */
    @Override
    public void onItemClick(int position) {
        selectedPosition = position;
        selectedDocument = documentList.get(position);
        adapter.setSelectedPosition(position);  // Highlight selection
        Toast.makeText(this, "Selected: " + selectedDocument.name, Toast.LENGTH_SHORT).show();
    }

    /** Deletes the selected document metadata **/
    private void deleteSelectedDocument(DocumentInfo document) {
        documentsRef.child(document.name)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Document deleted successfully.", Toast.LENGTH_SHORT).show();
                    documentList.remove(selectedPosition);
                    adapter.notifyItemRemoved(selectedPosition);
                    selectedDocument = null;
                    selectedPosition = RecyclerView.NO_POSITION;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}



