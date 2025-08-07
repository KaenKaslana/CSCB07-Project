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

/**
 * {DeleteContactActivity} presents a list of the user’s emergency contacts
 * and allows the user to select one and delete it from Firebase Realtime Database.
 * 
 * Upon launch it:
 * 
 *   Verifies the user is signed in.
 *   Loads all contacts under "/Users/{uid}/emergencyContacts".
 *   Highlights a contact when tapped and enables the “Confirm Delete” button.
 *   Deletes the selected contact when confirmation is tapped.
 * 
 */
public class DeleteContactActivity extends AppCompatActivity implements ContactAdapter.OnItemClickListener {

    /** RecyclerView displaying the list of contacts. */
    private RecyclerView recyclerView;

    /** Adapter used to bind contact data and handle item clicks. */
    private ContactAdapter adapter;

    /** Backing list of contacts retrieved from Firebase. */
    private List<ContactInfo> contactList;

    /** Firebase Realtime Database reference to the user’s emergencyContacts node. */
    private DatabaseReference contactsRef;

    /** Button that, when tapped, deletes the selected contact. */
    private Button confirmDeleteButton;

    /** Holds the currently selected contact for deletion. */
    private ContactInfo selectedContact;

    /** FirebaseAuth instance used to verify the signed-in user. */
    private FirebaseAuth mAuth;

    /**
     * Sets up the UI, checks for authentication, initializes Firebase references,
     * loads the contact list, and installs click handlers.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most recently supplied; otherwise null.
     */
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

    /**
     * Retrieves the user’s emergency contacts from Firebase and populates the RecyclerView.
     * Sets a ValueEventListener to keep the list in sync with database changes.
     */
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

    /**
     * Callback from {ContactAdapter.OnItemClickListener}.
     * Highlights the tapped contact and enables the delete button.
     *
     * @param position the adapter position of the clicked contact
     */
    @Override
    public void onItemClick(int position) {
        selectedContact = contactList.get(position);
        adapter.setSelectedPosition(position);  // Highlight selected item
        confirmDeleteButton.setEnabled(true);   // Enable delete button
    }

    /**
     * Deletes the currently selected contact from Firebase.
     * On success, shows confirmation, clears selection, disables the button,
     * and reloads the list.  On failure, shows the error message.
     */
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
