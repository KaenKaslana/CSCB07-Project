package com.example.b07demosummer2024;

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

    private SharedPreferencesHelper sharedPreferencesHelper;
    private EditText pinInput;
    private Button unlockButton;
    private TextView useFirebaseLoginText;
    private FloatingActionButton exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_unlock);

        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        pinInput             = findViewById(R.id.pinInput);
        unlockButton         = findViewById(R.id.unlockButton);
        useFirebaseLoginText = findViewById(R.id.useFirebaseLoginText);
        exitButton           = findViewById(R.id.exitButton);

        // Emergency-Exit FAB
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

        // Unlock logic
        unlockButton.setOnClickListener(v -> {
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
                Toast.makeText(this, "Incorrect PIN. Try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Success → return OK
            setResult(RESULT_OK);
            finish();
        });

        // Fallback to Firebase login
        useFirebaseLoginText.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
