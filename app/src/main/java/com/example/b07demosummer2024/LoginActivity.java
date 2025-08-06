package com.example.b07demosummer2024;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button  loginButton, goToRegisterButton;
    private TextView errorMessageTextView;
    private FloatingActionButton exitButton;
    private LoginPresenter    presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterButton = findViewById(R.id.goToRegisterButton);
        errorMessageTextView = findViewById(R.id.errorMessage);
        exitButton = findViewById(R.id.exitButton);

        // Emergency‐Exit FAB
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

        presenter = new LoginPresenter(this);

        loginButton.setOnClickListener(v -> {
            String email    = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            presenter.validateAndLogin(email, password);
        });

        goToRegisterButton.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    public void showError(String msg) {
        errorMessageTextView.setText(msg);
    }

    public void hideError() {
        errorMessageTextView.setText("");
    }

    public void onLoginSuccess() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("skipPin", true);                        // skip PIN
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public void onLoginFailure(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Login Failed")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
