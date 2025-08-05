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

public class EmergencyContactsActivity extends AppCompatActivity implements ContactAdapter.OnItemClickListener {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<ContactInfo> contactList;
    private DatabaseReference contactsRef;

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

    @Override
    public void onItemClick(int position) {
        ContactInfo selectedContact = contactList.get(position);
        Toast.makeText(this, "Clicked: " + selectedContact.getName(), Toast.LENGTH_SHORT).show();
    }
}
