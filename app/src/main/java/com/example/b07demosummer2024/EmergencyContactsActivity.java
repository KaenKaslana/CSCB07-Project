package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class EmergencyContactsActivity extends AppCompatActivity implements ContactAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<ContactInfo> contactList;
    private DatabaseReference contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contacts_activity);

        // Firebase reference: contacts under "users/user1/emergencyContacts"
        contactsRef = FirebaseDatabase.getInstance().getReference("users/user1/emergencyContacts");

        recyclerView = findViewById(R.id.emergencyContactsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();
        adapter = new ContactAdapter(this, contactList,this);
        recyclerView.setAdapter(adapter);

        Button addButton = findViewById(R.id.addContactButton);
        Button editButton = findViewById(R.id.editContactButton);
        Button deleteButton = findViewById(R.id.deleteContactButton);

        addButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AddContactActivity.class));
        });

        editButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EditContactActivity.class));
        });

        deleteButton.setOnClickListener(v -> {
            startActivity(new Intent(this, DeleteContactActivity.class));
        });

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
        // Example: Show a toast or navigate to details
        Toast.makeText(this, "Clicked: " + selectedContact.getName(), Toast.LENGTH_SHORT).show();
    }
}

