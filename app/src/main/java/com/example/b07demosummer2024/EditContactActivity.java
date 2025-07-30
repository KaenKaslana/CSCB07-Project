package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditContactActivity extends AppCompatActivity implements ContactAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<ContactInfo> contactList;
    private DatabaseReference contactsRef;
    private ContactInfo selectedContact; // The selected contact for editing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        recyclerView = findViewById(R.id.editContactsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();
        adapter = new ContactAdapter(this, contactList, this);
        recyclerView.setAdapter(adapter);

        contactsRef = FirebaseDatabase.getInstance().getReference("users/user1/emergencyContacts");

        loadContacts();
    }

    private void loadContacts() {
        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    ContactInfo contact = contactSnapshot.getValue(ContactInfo.class);
                    if (contact != null) {
                        contact.setId(contactSnapshot.getKey());
                        contactList.add(contact);
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

    @Override
    public void onItemClick(int position) {
        selectedContact = contactList.get(position);
        adapter.setSelectedPosition(position); // Highlight selected contact
        showEditDialog(selectedContact);
    }

    private void showEditDialog(ContactInfo contact) {
        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_contact, null);
        EditText nameInput = dialogView.findViewById(R.id.dialogEditName);
        EditText relationshipInput = dialogView.findViewById(R.id.dialogEditRelationship);
        EditText phoneInput = dialogView.findViewById(R.id.dialogEditPhone);
        EditText addressInput = dialogView.findViewById(R.id.dialogEditAddress);

        // Pre-fill current contact data
        nameInput.setText(contact.getName());
        relationshipInput.setText(contact.getRelationship());
        phoneInput.setText(contact.getPhone());
        addressInput.setText(contact.getAddress());

        // Create dialog
        new AlertDialog.Builder(this)
                .setTitle("Edit Contact")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    String newRelationship = relationshipInput.getText().toString().trim();
                    String newPhone = phoneInput.getText().toString().trim();
                    String newAddress = addressInput.getText().toString().trim();

                    if (newName.isEmpty() || newPhone.isEmpty()) {
                        Toast.makeText(EditContactActivity.this,
                                "Name and phone cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    contact.setName(newName);
                    contact.setRelationship(newRelationship);
                    contact.setPhone(newPhone);
                    contact.setAddress(newAddress);

                    updateContactInFirebase(contact);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateContactInFirebase(ContactInfo contact) {
        if (contact.getId() == null) {
            Toast.makeText(this, "Contact ID missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        contactsRef.child(contact.getId()).setValue(contact)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}