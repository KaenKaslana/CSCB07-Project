package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PinSetupActivity extends AppCompatActivity {

    private EditText pinInput, confirmPinInput;
    private Button submitButton;

    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_setup);

        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        pinInput = findViewById(R.id.pinInput);
        confirmPinInput = findViewById(R.id.confirmPinInput);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> setupPin());
    }

    private void setupPin() {
        String pin = pinInput.getText().toString().trim();
        String confirmPin = confirmPinInput.getText().toString().trim();

        if (TextUtils.isEmpty(pin)) {
            pinInput.setError("Please enter PIN");
            pinInput.requestFocus();
            return;
        }

        if (pin.length() != 4 && pin.length() != 6) {
            pinInput.setError("PIN must be 4 or 6 digits");
            pinInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPin)) {
            confirmPinInput.setError("Please confirm PIN");
            confirmPinInput.requestFocus();
            return;
        }

        if (!pin.equals(confirmPin)) {
            confirmPinInput.setError("PINs do not match");
            confirmPinInput.requestFocus();
            return;
        }

        sharedPreferencesHelper.savePin(pin);
        Toast.makeText(this, "PIN setup successful", Toast.LENGTH_SHORT).show();

        // Navigate to MainActivity
        Intent intent = new Intent(PinSetupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}