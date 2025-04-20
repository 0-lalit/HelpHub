package com.example.helphub;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helphub.adapter.GaTaAdapter;
import com.example.helphub.model.GaTa;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminAppointmentsActivity extends AppCompatActivity {
    private static final String TAG = "AdminAppointmentsActivity";
    private MaterialToolbar toolbar;
    private RecyclerView gaTaList;
    private TextInputEditText searchInput;
    private FloatingActionButton addGaTaFab;
    private GaTaAdapter adapter;
    private List<GaTa> gaTaItems;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_admin_appointments);

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
            
            // Initialize Firestore
            db = FirebaseFirestore.getInstance();
            
            initializeViews();
            setupToolbar();
            setupAdminAppointmentManager();
            setupListeners();
            loadGaTaData();
            setupSearch();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing appointments screen", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            toolbar = findViewById(R.id.toolbar);
            gaTaList = findViewById(R.id.gaTaList);
            searchInput = findViewById(R.id.searchInput);
            addGaTaFab = findViewById(R.id.addGaTaFab);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error finding views", Toast.LENGTH_SHORT).show();
            throw e;
        }
    }

    private void setupToolbar() {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Manage GAs/TAs");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar", e);
            Toast.makeText(this, "Error setting up toolbar", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAdminAppointmentManager() {
        try {
            // Get the current admin's ID from Firebase Auth
            String adminId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "default_admin";
        } catch (Exception e) {
            Log.e(TAG, "Error setting up appointment manager", e);
            Toast.makeText(this, "Error loading appointments", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        try {
            addGaTaFab.setOnClickListener(v -> showAddGaTaDialog());
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners", e);
            Toast.makeText(this, "Error setting up buttons", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddGaTaDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_gata, null);
        
        TextInputEditText nameInput = dialogView.findViewById(R.id.nameInput);
        TextInputEditText emailInput = dialogView.findViewById(R.id.emailInput);
        TextInputEditText roleInput = dialogView.findViewById(R.id.roleInput);
        TextInputEditText departmentInput = dialogView.findViewById(R.id.departmentInput);
        TextInputEditText courseInput = dialogView.findViewById(R.id.courseInput);
        TextInputEditText officeHoursInput = dialogView.findViewById(R.id.officeHoursInput);
        TextInputEditText officeLocationInput = dialogView.findViewById(R.id.officeLocationInput);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add GA/TA")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String email = emailInput.getText().toString().trim();
                    String role = roleInput.getText().toString().trim();
                    String department = departmentInput.getText().toString().trim();
                    String course = courseInput.getText().toString().trim();
                    String officeHours = officeHoursInput.getText().toString().trim();
                    String officeLocation = officeLocationInput.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || role.isEmpty() || 
                        department.isEmpty() || course.isEmpty() || 
                        officeHours.isEmpty() || officeLocation.isEmpty()) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    GaTa newGaTa = new GaTa(name, email, role, department, course, officeHours, officeLocation);
                    addGaTaToFirestore(newGaTa);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addGaTaToFirestore(GaTa gaTa) {
        db.collection("ga_ta")
                .add(gaTa)
                .addOnSuccessListener(documentReference -> {
                    gaTa.setId(documentReference.getId());
                    gaTaItems.add(gaTa);
                    adapter.updateList(gaTaItems);
                    Toast.makeText(this, "GA/TA added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding GA/TA: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
    }

    private void loadGaTaData() {
        try {
            gaTaItems = new ArrayList<>();
            adapter = new GaTaAdapter(gaTaItems, true); // true for admin view
            adapter.setOnItemClickListener(gaTa -> {
                // Handle GA/TA item click
                // TODO: Implement item click handling
            });
            gaTaList.setLayoutManager(new LinearLayoutManager(this));
            gaTaList.setAdapter(adapter);

            db.collection("ga_ta")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        gaTaItems.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            GaTa gaTa = document.toObject(GaTa.class);
                            gaTa.setId(document.getId());
                            gaTaItems.add(gaTa);
                        }
                        adapter.updateList(gaTaItems);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading GAs/TAs: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error loading GA/TA data", e);
            Toast.makeText(this, "Error loading GAs/TAs", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSearch() {
        try {
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up search", e);
            Toast.makeText(this, "Error setting up search", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}