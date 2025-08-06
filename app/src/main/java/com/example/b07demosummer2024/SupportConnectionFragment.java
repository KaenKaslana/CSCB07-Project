// SupportConnectionFragment.java
package com.example.b07demosummer2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SupportConnectionFragment extends Fragment {
    private static final String[] CITIES = {
            "Toronto", "Vancouver", "Ottawa", "Calgary", "Montreal"
    };

    private RecyclerView recyclerView;
    private FloatingActionButton exitButton;
    private Spinner spinnerCity;
    private HashMap<String, List<Resource>> map;
    private FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_support_connection,
                container, false
        );

        recyclerView = view.findViewById(R.id.recyclerView);
        spinnerCity  = view.findViewById(R.id.spinnerCity);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        map = ResourceLoader.loadResources(getContext());
        db  = FirebaseDatabase.getInstance();

        // Set up Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                CITIES
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerCity.setAdapter(adapter);

        // When user selects a city, refresh list
        spinnerCity.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View v, int pos, long id) {
                        updateRecycler(CITIES[pos]);
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) { }
                });

        // Determine initial city from database (or default)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = db.getReference(
                            QuestionView.getUserQuestionPath()
                    ).child("Q&A")
                    .child(QuestionView.WarmUpPath)
                    .child("2").child("1");

            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snap) {
                            String city = "Toronto";
                            for (DataSnapshot child : snap.getChildren()) {
                                String val = child.getValue(String.class);
                                if (val != null) {
                                    city = val;
                                }
                                break;
                            }
                            int idx = Arrays.asList(CITIES).indexOf(city);
                            if (idx < 0) idx = 0;
                            spinnerCity.setSelection(idx);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError err) {
                            spinnerCity.setSelection(0);
                        }
                    });
        } else {
            // no user → default to Toronto
            spinnerCity.setSelection(0);
        }

        return view;
    }

    private void updateRecycler(String city) {
        List<Resource> resources = map.get(city);
        recyclerView.setAdapter(
                new ResourceAdapter(getContext(), resources)
        );
    }
}
