package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * Activity that allows the user to view and edit their saved emergency contacts.
 * 
 * Displays a RecyclerView of {@link ContactInfo} items and opens an edit dialog when
 * an item is clicked. Changes are persisted to Firebase Realtime Database.
 * 
 */
public class EditContactActivity extends AppCompatActivity implements ContactAdapter.OnItemClickListener {

    /** RecyclerView displaying the list of contacts for editing. */
    private RecyclerView recyclerView;

    /** Adapter for binding {ContactInfo} objects to the RecyclerView. */
    private ContactAdapter adapter;

    /** Backing list of contacts loaded from Firebase. */
    private List<ContactInfo> contactList;

    /** Firebase Database reference pointing to the current user's emergency contacts. */
    private DatabaseReference contactsRef;

    /** Firebase authentication instance for retrieving the current user. */
    private FirebaseAuth mAuth;

    /** Currently selected contact for editing. */
    private ContactInfo selectedContact;

    /**
     * Initializes the activity: checks authentication, sets up Firebase references,
     * configures RecyclerView, and loads contacts.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this holds the data it most recently
     *                           supplied; otherwise {@code null}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = user.getUid();
        contactsRef = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(uid)
                .child("emergencyContacts");

        recyclerView = findViewById(R.id.editContactsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        adapter = new ContactAdapter(this, contactList, this);
        recyclerView.setAdapter(adapter);

        loadContacts();
    }

    /**
     * Loads the contacts from Firebase and listens for changes. Updates the
     * {contactList} and notifies the adapter on data change.
     */
    private void loadContacts() {
        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ContactInfo c = ds.getValue(ContactInfo.class);
                    if (c != null) {
                        c.setId(ds.getKey());
                        contactList.add(c);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditContactActivity.this, "Failed to load contacts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Called when a contact item is clicked in the RecyclerView.
     * Saves the selected contact and opens the edit dialog.
     *
     * @param position Index of the clicked item in {@link #contactList}.
     */
    @Override
    public void onItemClick(int position) {
        selectedContact = contactList.get(position);
        adapter.setSelectedPosition(position);
        showEditDialog(selectedContact);
    }

    /**
     * Displays an AlertDialog to edit the details of the given contact.
     * Validates input before invoking {#updateContactInFirebase(ContactInfo)}.
     *
     * @param contact The {ContactInfo} object to be edited.
     */
    private void showEditDialog(ContactInfo contact) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_edit_contact, null);
        EditText nameEt = v.findViewById(R.id.dialogEditName);
        EditText relEt  = v.findViewById(R.id.dialogEditRelationship);
        EditText phoneEt= v.findViewById(R.id.dialogEditPhone);
        EditText addrEt = v.findViewById(R.id.dialogEditAddress);

        nameEt.setText(contact.getName());
        relEt.setText(contact.getRelationship());
        phoneEt.setText(contact.getPhone());
        addrEt.setText(contact.getAddress());

        new AlertDialog.Builder(this)
                .setTitle("Edit Contact")
                .setView(v)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newName = nameEt.getText().toString().trim();
                    String newRel  = relEt.getText().toString().trim();
                    String newPhone= phoneEt.getText().toString().trim();
                    String newAddr = addrEt.getText().toString().trim();
                    if (newName.isEmpty() || newPhone.isEmpty()) {
                        Toast.makeText(this, "Name and phone cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    contact.setName(newName);
                    contact.setRelationship(newRel);
                    contact.setPhone(newPhone);
                    contact.setAddress(newAddr);
                    updateContactInFirebase(contact);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Persists the updated contact details to Firebase under its unique ID.
     * Shows a toast on success or failure.
     *
     * @param contact The updated {ContactInfo} to write.
     */
    private void updateContactInFirebase(ContactInfo contact) {
        String id = contact.getId();
        if (id == null) {
            Toast.makeText(this, "Contact ID missing.", Toast.LENGTH_SHORT).show();
            return;
        }
        contactsRef.child(id)
                .setValue(contact)
                .addOnSuccessListener(a ->
                        Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
