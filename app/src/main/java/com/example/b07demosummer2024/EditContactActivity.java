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

public class EditContactActivity extends AppCompatActivity implements ContactAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<ContactInfo> contactList;
    private DatabaseReference contactsRef;
    private FirebaseAuth mAuth;
    private ContactInfo selectedContact;

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

    @Override
    public void onItemClick(int position) {
        selectedContact = contactList.get(position);
        adapter.setSelectedPosition(position);
        showEditDialog(selectedContact);
    }

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
