package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, goToRegisterButton;
    private TextView errorMessageTextView;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterButton = findViewById(R.id.goToRegisterButton);
        errorMessageTextView = findViewById(R.id.errorMessage);

        presenter = new LoginPresenter(this);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            presenter.validateAndLogin(email, password); // Calling the Presenter
        });

        goToRegisterButton.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    public void showError(String message) {
        errorMessageTextView.setText(message);
    }

    public void hideError() {
        errorMessageTextView.setText("");
    }

    public void onLoginSuccess() {
        // Navigate to MainActivity or any other logic on successful login
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onLoginFailure(String message) {
        new MaterialAlertDialogBuilder(LoginActivity.this)
                .setTitle("Login Failed")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
