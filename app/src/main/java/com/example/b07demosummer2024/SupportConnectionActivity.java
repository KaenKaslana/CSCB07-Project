package com.example.b07demosummer2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

public class SupportConnectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_connection);

        recyclerView = findViewById(R.id.recyclerView);
        exitButton = findViewById(R.id.btn_exit);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        HashMap<String, List<Resource>> map = ResourceLoader.loadResources(this);

        //change later
        String userCity = "Montreal";

        List<Resource> resources = map.get(userCity);

        ResourceAdapter adapter = new ResourceAdapter(this, resources);
        recyclerView.setAdapter(adapter);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                startActivity(intent);
                finishAffinity();
            }
        });
    }
}
