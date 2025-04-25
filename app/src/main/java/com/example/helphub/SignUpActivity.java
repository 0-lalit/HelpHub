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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private TextInputEditText firstNameInput;
    private TextInputEditText lastNameInput;
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
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
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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

        signUpButton.setEnabled(false);
        gaTaSignUpButton.setEnabled(false);
        signUpButton.setText("Creating account...");
        gaTaSignUpButton.setText("Creating account...");

        Log.d(TAG, "Attempting to create account with email: " + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            boolean isAdmin = isGaTaSignUp;

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("firstName", firstName);
                            userData.put("lastName", lastName);
                            userData.put("email", email);
                            userData.put("isAdmin", isAdmin);
                            userData.put("createdAt", new Date());

                            db.collection("users").document(user.getUid())
                                    .set(userData);

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(user.getUid())
                                    .setValue(userData);

                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            Intent intent = isAdmin ? new Intent(this, AdminHomeActivity.class)
                                    : new Intent(this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Exception exception = task.getException();
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