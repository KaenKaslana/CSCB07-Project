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

import java.util.HashMap;
import java.util.List;

public class SupportConnectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton exitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_support_connection,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.recyclerView);
        exitButton   = view.findViewById(R.id.btn_exit);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        HashMap<String, List<Resource>> map = ResourceLoader.loadResources(getContext());

        //todo
        String userCity = "Montreal";
        List<Resource> resources = map.get(userCity);
        ResourceAdapter adapter = new ResourceAdapter(getContext(), resources);
        recyclerView.setAdapter(adapter);

        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com")
            );
            startActivity(intent);
            requireActivity().finishAffinity();
        });

        return view;
    }
}
