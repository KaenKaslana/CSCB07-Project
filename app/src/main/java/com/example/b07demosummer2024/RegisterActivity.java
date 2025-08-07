package com.example.b07demosummer2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button registerButton, goToLoginButton;

    // FirebaseAuth instance
    private FirebaseAuth auth;
    private FloatingActionButton exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);
        exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(v -> {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(browser);
            finishAffinity();
        });

        registerButton.setOnClickListener(v -> registerUser());
        goToLoginButton.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void registerUser() {
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

        // Create Authentication account
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful, get UID
                        String uid = auth.getCurrentUser().getUid();
                        // Build user profile object
                        User user = new User(email, uid);

                        // Write to Realtime Database at Users/{uid}
                        FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(uid)
                                .setValue(user)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(
                                                RegisterActivity.this,
                                                "Registration successful!",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                        // Navigate to PinSetUp
                                        startActivity(new Intent(
                                                RegisterActivity.this, PinSetupActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(
                                                RegisterActivity.this,
                                                "Failed to write user info: "
                                                        + dbTask.getException().getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                });
                    } else {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Registration failed: "
                                        + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}