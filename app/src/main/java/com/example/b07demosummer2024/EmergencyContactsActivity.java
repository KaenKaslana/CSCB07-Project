package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * Activity that displays the user's emergency contacts and provides navigation
 * to add, edit, or delete contacts.
 * 
 * Loads contacts from Firebase Realtime Database into a RecyclerView. Implements
 * {ContactAdapter.OnItemClickListener} to handle item clicks.
 * 
 */
public class EmergencyContactsActivity extends AppCompatActivity implements ContactAdapter.OnItemClickListener {

    /** Firebase authentication instance for retrieving the current user. */
    private FirebaseAuth mAuth;

    /** RecyclerView displaying the list of emergency contacts. */
    private RecyclerView recyclerView;

    /** Adapter for binding {ContactInfo} objects to the RecyclerView. */
    private ContactAdapter adapter;

    /** Backing list of contacts loaded from Firebase. */
    private List<ContactInfo> contactList;

    /** Firebase Database reference pointing to the current user's emergencyContacts node. */
    private DatabaseReference contactsRef;

    /**
     * Initializes the activity: checks user authentication, sets up Firebase
     * references, configures RecyclerView and navigation buttons, and loads contacts.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this holds the data
     *                           it most recently supplied; otherwise {@code null}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contacts_activity);

        // Initialize FirebaseAuth and ensure user is signed in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Build dynamic path: "Users/{uid}/emergencyContacts"
        String uid = user.getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("emergencyContacts");

        recyclerView = findViewById(R.id.emergencyContactsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();
        adapter = new ContactAdapter(this, contactList, this);
        recyclerView.setAdapter(adapter);

        Button addButton    = findViewById(R.id.addContactButton);
        Button editButton   = findViewById(R.id.editContactButton);
        Button deleteButton = findViewById(R.id.deleteContactButton);

        addButton   .setOnClickListener(v -> startActivity(new Intent(this, AddContactActivity.class)));
        editButton  .setOnClickListener(v -> startActivity(new Intent(this, EditContactActivity.class)));
        deleteButton.setOnClickListener(v -> startActivity(new Intent(this, DeleteContactActivity.class)));

        loadContactsFromFirebase();
    }

    /**
     * Loads emergency contacts from Firebase and listens for data changes.
     * Updates {#contactList} and notifies the adapter upon change.
     */
    private void loadContactsFromFirebase() {
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
                Toast.makeText(EmergencyContactsActivity.this,
                        "Failed to load contacts: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles clicks on RecyclerView items by showing a toast with the contact's name.
     *
     * @param position Index of the clicked item in {@link #contactList}.
     */
    @Override
    public void onItemClick(int position) {
        ContactInfo selectedContact = contactList.get(position);
        Toast.makeText(this, "Clicked: " + selectedContact.getName(), Toast.LENGTH_SHORT).show();
    }
}
