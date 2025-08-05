package com.example.b07demosummer2024;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginModel {

    private FirebaseAuth auth;

    public LoginModel() {
        auth = FirebaseAuth.getInstance();
    }

    public void loginUser(String email, String password, final LoginCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = auth.getCurrentUser();
                        callback.onLoginSuccess(user);
                    } else {
                        callback.onLoginFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    public interface LoginCallback {
        void onLoginSuccess(FirebaseUser user);
        void onLoginFailure(String errorMessage);  
    }
}
