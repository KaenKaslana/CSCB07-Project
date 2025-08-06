package com.example.b07demosummer2024;
//test
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PinUnlockActivity extends AppCompatActivity {

    private EditText pinInput;
    private Button unlockButton;
    private TextView useFirebaseLoginText;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private FloatingActionButton exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_unlock);

        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        pinInput = findViewById(R.id.pinInput);
        unlockButton = findViewById(R.id.unlockButton);
        useFirebaseLoginText = findViewById(R.id.useFirebaseLoginText);
        exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(v -> {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(browser);
            finishAffinity();
        });

        unlockButton.setOnClickListener(v -> unlockWithPin());
        useFirebaseLoginText.setOnClickListener(v -> {
            // Navigate to Firebase login screen
            Intent intent = new Intent(PinUnlockActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void unlockWithPin() {
        String inputPin = pinInput.getText().toString().trim();

        if (TextUtils.isEmpty(inputPin)) {
            pinInput.setError("Please enter PIN");
            pinInput.requestFocus();
            return;
        }

        if (!sharedPreferencesHelper.verifyPin(inputPin)) {
            pinInput.setError("Incorrect PIN");
            pinInput.setText("");
            pinInput.requestFocus();
            Toast.makeText(this, "Incorrect PIN. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // PIN verified, navigate to MainActivity
        Intent intent = new Intent(PinUnlockActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}