package com.example.b07demosummer2024;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityResultLauncher<Intent> pinLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase + AppCheck
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(
                        PlayIntegrityAppCheckProviderFactory.getInstance()
                );

        setContentView(R.layout.activity_main);

        // Notification channel (8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(
                    "reminder_channel",
                    getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            chan.setDescription(getString(R.string.channel_description));
            ((NotificationManager)getSystemService(NotificationManager.class))
                    .createNotificationChannel(chan);
        }

        // Emergency-Exit FAB
        FloatingActionButton exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com")
            ));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finishAffinity();
            }
        });

        // Prepare PIN-unlock launcher
        pinLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadFragment(new HomeFragment());
                    } else {
                        finishAffinity();
                    }
                }
        );

        // Decide whether to skip PIN
        boolean skipPin = getIntent().getBooleanExtra("skipPin", false);
        if (savedInstanceState == null) {
            if (skipPin) {
                loadFragment(new HomeFragment());
            } else {
                pinLauncher.launch(new Intent(this, PinUnlockActivity.class));
            }
        }
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment_container, frag);
        tx.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
