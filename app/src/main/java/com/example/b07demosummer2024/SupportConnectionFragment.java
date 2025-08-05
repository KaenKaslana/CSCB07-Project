package com.example.b07demosummer2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.HashMap;
import java.util.List;

public class SupportConnectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton exitButton;
    private HashMap<String, List<Resource>> map;
    private FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support_connection, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        exitButton = view.findViewById(R.id.btn_exit);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        map = ResourceLoader.loadResources(getContext());
        db = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference cityAnsRef = db.getReference(QuestionView.getUserQuestionPath()).child("Q&A").child(QuestionView.WarmUpPath).child("2").child("1");
            cityAnsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userCity = null;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        userCity = child.getValue(String.class);
                        break;
                    }
                    if (userCity == null) {
                        userCity = "Toronto";
                    }
                    List<Resource> resources = map.get(userCity);
                    ResourceAdapter adapter = new ResourceAdapter(getContext(), resources);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    List<Resource> resources = map.get("Toronto");
                    recyclerView.setAdapter(new ResourceAdapter(getContext(), resources));
                }
            });
        } else {
            List<Resource> resources = map.get("Toronto");
            recyclerView.setAdapter(new ResourceAdapter(getContext(), resources));
        }
        exitButton.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")));
            requireActivity().finishAffinity();
        });
        return view;
    }
}
