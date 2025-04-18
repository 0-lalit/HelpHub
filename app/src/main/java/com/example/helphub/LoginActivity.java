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

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User already signed in: " + currentUser.getEmail());
            startActivity(new Intent(this, MainActivity.class));
            finish();
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
                            Log.e(TAG, "Password reset failed", task.getException());
                            Toast.makeText(this, "Failed to send reset email: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        signUpText.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    private void performLogin(boolean isAdminLogin) {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        loginButton.setEnabled(false);
        gaTaLoginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        gaTaLoginButton.setText("Logging in...");

        Log.d(TAG, "Attempting to sign in with email: " + email);

        // Sign in with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Sign in successful for user: " + user.getEmail());
                        
                        // Check user type in Firestore
                        db.collection("users").document(user.getUid())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    boolean isAdmin = documentSnapshot.getBoolean("isAdmin");
                                    
                                    // Check if user type matches login type
                                    if (isAdmin != isAdminLogin) {
                                        // User type mismatch
                                        mAuth.signOut();
                                        String errorMessage = isAdmin ? 
                                            "Admin users must use the 'Login as Admin' button" : 
                                            "Regular users must use the regular login button";
                                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                                        loginButton.setEnabled(true);
                                        gaTaLoginButton.setEnabled(true);
                                        loginButton.setText("Login");
                                        gaTaLoginButton.setText("Login as Admin");
                                        return;
                                    }
                                    
                                    // User type matches, proceed with navigation
                                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                                    Intent intent;
                                    if (isAdmin) {
                                        intent = new Intent(this, GaTaActivity.class);
                                    } else {
                                        intent = new Intent(this, MainActivity.class);
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // User document doesn't exist
                                    mAuth.signOut();
                                    Toast.makeText(this, "User profile not found", Toast.LENGTH_LONG).show();
                                    loginButton.setEnabled(true);
                                    gaTaLoginButton.setEnabled(true);
                                    loginButton.setText("Login");
                                    gaTaLoginButton.setText("Login as Admin");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error checking user type", e);
                                mAuth.signOut();
                                Toast.makeText(this, "Error checking user type", Toast.LENGTH_LONG).show();
                                loginButton.setEnabled(true);
                                gaTaLoginButton.setEnabled(true);
                                loginButton.setText("Login");
                                gaTaLoginButton.setText("Login as Admin");
                            });
                    } else {
                        // Sign in failed
                        Exception exception = task.getException();
                        Log.e(TAG, "Sign in failed", exception);
                        
                        String errorMessage;
                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            errorMessage = "No account found with this email";
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMessage = "Invalid password";
                        } else {
                            errorMessage = "Authentication failed: " + exception.getMessage();
                        }
                        
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        loginButton.setEnabled(true);
                        gaTaLoginButton.setEnabled(true);
                        loginButton.setText("Login");
                        gaTaLoginButton.setText("Login as Admin");
                    }
                });
    }
} 