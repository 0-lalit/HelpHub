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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton signUpButton;
    private MaterialButton gaTaSignUpButton;
    private TextView loginText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isGaTaSignUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signUpButton = findViewById(R.id.signUpButton);
        gaTaSignUpButton = findViewById(R.id.gaTaSignUpButton);
        loginText = findViewById(R.id.loginText);
    }

    private void setupListeners() {
        signUpButton.setOnClickListener(v -> {
            Log.d(TAG, "Regular signup button clicked");
            isGaTaSignUp = false;
            performSignUp();
        });
        
        gaTaSignUpButton.setOnClickListener(v -> {
            Log.d(TAG, "GA/TA signup button clicked");
            isGaTaSignUp = true;
            performSignUp();
        });
        
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void performSignUp() {
        Log.d(TAG, "Starting signup process. isGaTaSignUp: " + isGaTaSignUp);
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        signUpButton.setEnabled(false);
        gaTaSignUpButton.setEnabled(false);
        signUpButton.setText("Creating account...");
        gaTaSignUpButton.setText("Creating account...");

        Log.d(TAG, "Attempting to create account with email: " + email);

        // Create user with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success
                        Log.d(TAG, "createUserWithEmail:success");
                        
                        // Create user document in Firestore
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "User created successfully with UID: " + user.getUid());
                            
                            // First navigate to the appropriate activity
                            Log.d(TAG, "Navigating to " + (isGaTaSignUp ? "GaTaActivity" : "MainActivity"));
                            Intent intent;
                            if (isGaTaSignUp) {
                                intent = new Intent(this, GaTaActivity.class);
                            } else {
                                intent = new Intent(this, MainActivity.class);
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            
                            // Then update Firestore in the background
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", email);
                            userData.put("isAdmin", isGaTaSignUp);
                            userData.put("createdAt", new Date());
                            
                            Log.d(TAG, "Attempting to create Firestore document for user: " + user.getUid());
                            db.collection("users").document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Firestore document created successfully");
                                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                    
                                    // Send email verification
                                    Log.d(TAG, "Sending email verification");
                                    user.sendEmailVerification()
                                        .addOnCompleteListener(emailTask -> {
                                            if (emailTask.isSuccessful()) {
                                                Log.d(TAG, "Verification email sent successfully");
                                                Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e(TAG, "Failed to send verification email", emailTask.getException());
                                            }
                                        });
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error creating user document", e);
                                    // Don't show error to user since we've already navigated away
                                });
                            
                            // Finish this activity after navigation
                            finish();
                        } else {
                            Log.e(TAG, "User is null after successful creation");
                            Toast.makeText(this, "Error creating user account", Toast.LENGTH_LONG).show();
                            signUpButton.setEnabled(true);
                            gaTaSignUpButton.setEnabled(true);
                            signUpButton.setText("Sign Up");
                            gaTaSignUpButton.setText("Sign Up as Admin");
                        }
                    } else {
                        // Sign up failed
                        Exception exception = task.getException();
                        Log.e(TAG, "createUserWithEmail:failure", exception);
                        
                        String errorMessage;
                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            errorMessage = "Email already in use";
                        } else if (exception instanceof FirebaseAuthWeakPasswordException) {
                            errorMessage = "Password is too weak";
                        } else {
                            errorMessage = "Sign up failed: " + exception.getMessage();
                        }
                        
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        signUpButton.setEnabled(true);
                        gaTaSignUpButton.setEnabled(true);
                        signUpButton.setText("Sign Up");
                        gaTaSignUpButton.setText("Sign Up as Admin");
                    }
                });
    }
} 