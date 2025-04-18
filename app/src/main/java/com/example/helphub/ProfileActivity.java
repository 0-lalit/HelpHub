package com.example.helphub;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private ShapeableImageView profileImage;
    private TextView nameText;
    private TextView emailText;
    private MaterialButton editProfileButton;
    private SwitchMaterial notificationSwitch;
    private SwitchMaterial darkModeSwitch;
    private TextView privacyPolicyText;
    private TextView termsOfServiceText;
    private MaterialButton logoutButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupToolbar();
        setupProfile();
        setupListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.profileImage);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        editProfileButton = findViewById(R.id.editProfileButton);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        privacyPolicyText = findViewById(R.id.privacyPolicyText);
        termsOfServiceText = findViewById(R.id.termsOfServiceText);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Set email from Firebase Auth
            emailText.setText(currentUser.getEmail());
            
            // Load user profile from Firestore
            db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        nameText.setText(firstName + " " + lastName);
                    } else {
                        nameText.setText("User");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileActivity", "Error loading user profile", e);
                    nameText.setText("User");
                });
        }
        
        // TODO: Load actual settings
        notificationSwitch.setChecked(true);
        darkModeSwitch.setChecked(false);
    }

    private void setupListeners() {
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save notification preference
            Toast.makeText(this, 
                "Notifications " + (isChecked ? "enabled" : "disabled"), 
                Toast.LENGTH_SHORT).show();
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Implement dark mode
            Toast.makeText(this, 
                "Dark mode " + (isChecked ? "enabled" : "disabled"), 
                Toast.LENGTH_SHORT).show();
        });

        privacyPolicyText.setOnClickListener(v -> {
            // TODO: Show privacy policy
            Toast.makeText(this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show();
        });

        termsOfServiceText.setOnClickListener(v -> {
            // TODO: Show terms of service
            Toast.makeText(this, "Terms of Service clicked", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            // Sign out from Firebase
            mAuth.signOut();
            
            // Show logout message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            // Clear the activity stack and go to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showEditProfileDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_profile);
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        TextInputEditText firstNameInput = dialog.findViewById(R.id.firstNameInput);
        TextInputEditText lastNameInput = dialog.findViewById(R.id.lastNameInput);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancelButton);
        MaterialButton saveButton = dialog.findViewById(R.id.saveButton);

        // Pre-fill values
        String[] nameParts = nameText.getText().toString().trim().split(" ");
        if (nameParts.length >= 2) {
            firstNameInput.setText(nameParts[0]);
            lastNameInput.setText(nameParts[1]);
        } else if (nameParts.length == 1) {
            firstNameInput.setText(nameParts[0]);
            lastNameInput.setText("");
        }

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        saveButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            saveButton.setEnabled(false);
            saveButton.setText("Saving...");

            db.collection("users").document(currentUser.getUid())
                    .update("firstName", firstName, "lastName", lastName)
                    .addOnSuccessListener(aVoid -> {
                        // Update UI
                        nameText.setText(firstName + " " + lastName);
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        saveButton.setEnabled(true);
                        saveButton.setText("Save");
                        Log.e("ProfileActivity", "Firestore update failed", e);
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        dialog.show();
    }



    private void refreshProfile() {
        // Clear the password field for next time
        TextInputEditText passwordInput = findViewById(R.id.passwordInput);
        if (passwordInput != null) {
            passwordInput.setText("");
        }
        
        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Update email directly from Firebase Auth
            emailText.setText(currentUser.getEmail());
            
            // Reload user profile from Firestore
            db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        nameText.setText(firstName + " " + lastName);
                        
                        // Show success message
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        nameText.setText("User");
                        Toast.makeText(this, "Profile updated, but user data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileActivity", "Error loading user profile", e);
                    Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show();
                });
        }
        
        // Show a brief animation to indicate refresh
        View rootView = findViewById(android.R.id.content);
        rootView.animate()
            .alpha(0.8f)
            .setDuration(100)
            .withEndAction(() -> {
                rootView.animate()
                    .alpha(1.0f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 