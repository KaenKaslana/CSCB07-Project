package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, goToRegisterButton;

    // FirebaseAuth instance
    private FirebaseAuth auth;

    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterButton = findViewById(R.id.goToRegisterButton);

        loginButton.setOnClickListener(v -> loginUser());
        goToRegisterButton.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Basic validation
        if (email.isEmpty()) {
            emailInput.setError("Please enter email");
            emailInput.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Please enter password");
            passwordInput.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return;
        }

        // Sign in with FirebaseAuth
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        if (!sharedPreferencesHelper.isPinSet()) {
                            // Prompt to create PIN if not set
                            Intent intent = new Intent(LoginActivity.this, PinSetupActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // PIN is set, proceed to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Login failed
                        new MaterialAlertDialogBuilder(LoginActivity.this)
                                .setTitle("Login Failed")
                                .setMessage(task.getException() != null ? task.getException().getMessage() : "Unknown error")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
    }
}