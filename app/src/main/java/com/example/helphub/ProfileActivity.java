package com.example.helphub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        // TODO: Load actual user data
        nameText.setText("John Doe");
        emailText.setText("john.doe@example.com");
        
        // TODO: Load actual settings
        notificationSwitch.setChecked(true);
        darkModeSwitch.setChecked(false);
    }

    private void setupListeners() {
        editProfileButton.setOnClickListener(v -> {
            // TODO: Implement edit profile
            Toast.makeText(this, "Edit profile clicked", Toast.LENGTH_SHORT).show();
        });

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
            // TODO: Implement proper logout
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
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