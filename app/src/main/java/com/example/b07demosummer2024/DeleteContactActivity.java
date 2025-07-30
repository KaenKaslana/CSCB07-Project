package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class DeleteContactActivity extends AppCompatActivity implements ContactAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<ContactInfo> contactList;
    private DatabaseReference contactsRef;
    private Button confirmDeleteButton;
    private ContactInfo selectedContact; // The contact selected by the user
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_contact);

        recyclerView = findViewById(R.id.deleteContactsRecyclerView);
        confirmDeleteButton = findViewById(R.id.confirmDeleteButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();
        adapter = new ContactAdapter(this, contactList, this);
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
        }
        String uid = currentUser.getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("emergencyContacts");

        loadContacts();

        confirmDeleteButton.setOnClickListener(v -> {
            if (selectedContact != null) {
                deleteSelectedContact();
            } else {
                Toast.makeText(DeleteContactActivity.this, "Please select a contact to delete", Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(DeleteContactActivity.this, "Failed to load contacts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        selectedContact = contactList.get(position);
        adapter.setSelectedPosition(position);  // Highlight selected item
        confirmDeleteButton.setEnabled(true);   // Enable delete button
    }

    private void deleteSelectedContact() {
        if (selectedContact != null && selectedContact.getId() != null) {
            contactsRef.child(selectedContact.getId())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                        selectedContact = null;
                        confirmDeleteButton.setEnabled(false);
                        loadContacts(); // Reload contacts
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
}
