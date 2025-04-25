package com.example.helphub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private MaterialButton gaTaLoginButton;
    private TextView forgotPasswordText;
    private TextView signUpText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupListeners();

        // Auto redirect if already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserTypeAndRedirect(currentUser.getUid());
        }
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        gaTaLoginButton = findViewById(R.id.gaTaLoginButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        signUpText = findViewById(R.id.signUpText);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> performLogin(false));
        gaTaLoginButton.setOnClickListener(v -> performLogin(true));

        forgotPasswordText.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        signUpText.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );
    }

    private void performLogin(boolean isAdminLogin) {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);
        gaTaLoginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            db.collection("users").document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        if (snapshot.exists()) {
                                            boolean isAdmin = snapshot.getBoolean("isAdmin") != null && snapshot.getBoolean("isAdmin");
                                            if (isAdmin != isAdminLogin) {
                                                mAuth.signOut();
                                                Toast.makeText(this, isAdmin
                                                        ? "Admins must log in using 'Login as Admin'"
                                                        : "Students must use the regular login", Toast.LENGTH_LONG).show();
                                                resetButtons();
                                            } else {
                                                launchHomeScreen(isAdmin);
                                            }
                                        } else {
                                            mAuth.signOut();
                                            Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show();
                                            resetButtons();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        mAuth.signOut();
                                        Toast.makeText(this, "Failed to retrieve user type", Toast.LENGTH_SHORT).show();
                                        resetButtons();
                                    });
                        }
                    } else {
                        Exception ex = task.getException();
                        if (ex instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(this, "No account found with this email", Toast.LENGTH_LONG).show();
                        } else if (ex instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Authentication failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        resetButtons();
                    }
                });
    }

    private void checkUserTypeAndRedirect(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        boolean isAdmin = snapshot.getBoolean("isAdmin") != null && snapshot.getBoolean("isAdmin");
                        launchHomeScreen(isAdmin);
                    } else {
                        mAuth.signOut();
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    mAuth.signOut();
                    Toast.makeText(this, "Error checking user type", Toast.LENGTH_SHORT).show();
                });
    }

    private void launchHomeScreen(boolean isAdmin) {
        Intent intent = new Intent(this, isAdmin ? AdminHomeActivity.class : MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void resetButtons() {
        loginButton.setEnabled(true);
        gaTaLoginButton.setEnabled(true);
        loginButton.setText("Login");
        gaTaLoginButton.setText("Login as Admin");
    }
}
