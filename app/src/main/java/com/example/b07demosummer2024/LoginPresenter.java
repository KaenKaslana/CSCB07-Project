package com.example.b07demosummer2024;

import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseUser;

public class LoginPresenter {

    private LoginActivity view;
    private LoginModel model;

    public LoginPresenter(LoginActivity view) {
        this.view = view;
        this.model = new LoginModel();
    }

    public void validateAndLogin(String email, String password) {
        // Validate input
        if (TextUtils.isEmpty(email)) {
            view.showError("Please enter email");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showError("Please enter a valid email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            view.showError("Please enter password");
            return;
        }
        if (password.length() < 6) {
            view.showError("Password must be 6 or more characters");
            return;
        }

        view.hideError();

        model.loginUser(email, password, new LoginModel.LoginCallback() {
            @Override
            public void onLoginSuccess(FirebaseUser user) {
                view.onLoginSuccess();
            }

            @Override
            public void onLoginFailure(String errorMessage) {
                view.onLoginFailure(errorMessage);
            }
        });
    }
}
