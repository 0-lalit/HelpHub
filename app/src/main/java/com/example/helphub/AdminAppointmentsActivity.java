package com.example.helphub;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helphub.adapter.GaTaAdapter;
import com.example.helphub.model.GaTa;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminAppointmentsActivity extends AppCompatActivity implements GaTaAdapter.OnItemClickListener {
    private static final String TAG = "AdminAppointmentsActivity";
    private MaterialToolbar toolbar;
    private RecyclerView gaTaList;
    private TextInputEditText searchInput;
    private FloatingActionButton addGaTaFab;
    private FloatingActionButton editGaTaFab;
    private FloatingActionButton deleteGaTaFab;
    private FloatingActionButton refreshFab;
    private GaTaAdapter adapter;
    private List<GaTa> gaTaItems;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private GaTa selectedGaTa;

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
            setupRecyclerView();
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
            editGaTaFab = findViewById(R.id.editGaTaFab);
            deleteGaTaFab = findViewById(R.id.deleteGaTaFab);
            refreshFab = findViewById(R.id.refreshFab);
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

    private void setupRecyclerView() {
        try {
            gaTaItems = new ArrayList<>();
            adapter = new GaTaAdapter(gaTaItems, true);
            adapter.setOnItemClickListener(this);
            gaTaList.setLayoutManager(new LinearLayoutManager(this));
            gaTaList.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up recycler view", e);
            Toast.makeText(this, "Error setting up list", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        addGaTaFab.setOnClickListener(v -> showAddGaTaDialog());
        editGaTaFab.setOnClickListener(v -> showEditGaTaDialog());
        deleteGaTaFab.setOnClickListener(v -> showDeleteConfirmationDialog());
        refreshFab.setOnClickListener(v -> loadGaTaData());
    }

    private void loadGaTaData() {
        try {
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
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                List<GaTa> filteredList = new ArrayList<>();
                
                for (GaTa gaTa : gaTaItems) {
                    if (gaTa.getName().toLowerCase().contains(query) ||
                        gaTa.getCourse().toLowerCase().contains(query)) {
                        filteredList.add(gaTa);
                    }
                }
                
                adapter.updateList(filteredList);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onItemClick(GaTa gaTa) {
        selectedGaTa = gaTa;
        showGaTaDetailsDialog(gaTa);
        updateFabVisibility();
    }

    @Override
    public void onDeleteClick(GaTa gaTa) {
        selectedGaTa = gaTa;
        showDeleteConfirmationDialog();
    }

    @Override
    public void onToggleAvailabilityClick(GaTa gaTa) {
        gaTa.setAvailable(!gaTa.isAvailable());
        updateGaTaInFirestore(gaTa);
    }

    private void updateFabVisibility() {
        if (selectedGaTa != null) {
            addGaTaFab.setVisibility(View.GONE);
            editGaTaFab.setVisibility(View.VISIBLE);
            deleteGaTaFab.setVisibility(View.VISIBLE);
        } else {
            addGaTaFab.setVisibility(View.VISIBLE);
            editGaTaFab.setVisibility(View.GONE);
            deleteGaTaFab.setVisibility(View.GONE);
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

    private void showGaTaDetailsDialog(GaTa gaTa) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_gata_details, null);
        
        // Initialize views
        TextInputEditText nameText = dialogView.findViewById(R.id.nameText);
        TextInputEditText emailText = dialogView.findViewById(R.id.emailText);
        TextInputEditText roleText = dialogView.findViewById(R.id.roleText);
        TextInputEditText departmentText = dialogView.findViewById(R.id.departmentText);
        TextInputEditText courseText = dialogView.findViewById(R.id.courseText);
        TextInputEditText officeHoursText = dialogView.findViewById(R.id.officeHoursText);
        TextInputEditText officeLocationText = dialogView.findViewById(R.id.officeLocationText);
        MaterialButton editButton = dialogView.findViewById(R.id.editButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Set values
        nameText.setText(gaTa.getName());
        emailText.setText(gaTa.getEmail());
        roleText.setText(gaTa.getRole());
        departmentText.setText(gaTa.getDepartment());
        courseText.setText(gaTa.getCourse());
        officeHoursText.setText(gaTa.getOfficeHours());
        officeLocationText.setText(gaTa.getOfficeLocation());

        // Create dialog
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle("GA/TA Details")
                .setView(dialogView)
                .setCancelable(false);

        AlertDialog dialog = dialogBuilder.create();

        // Set button click listeners
        editButton.setOnClickListener(v -> {
            dialog.dismiss();
            showEditGaTaDialog();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showEditGaTaDialog() {
        if (selectedGaTa == null) {
            Toast.makeText(this, "Please select a GA/TA to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_gata_details, null);

        // Initialize views
        TextInputEditText nameText = dialogView.findViewById(R.id.nameText);
        TextInputEditText emailText = dialogView.findViewById(R.id.emailText);
        TextInputEditText roleText = dialogView.findViewById(R.id.roleText);
        TextInputEditText departmentText = dialogView.findViewById(R.id.departmentText);
        TextInputEditText courseText = dialogView.findViewById(R.id.courseText);
        TextInputEditText officeHoursText = dialogView.findViewById(R.id.officeHoursText);
        TextInputEditText officeLocationText = dialogView.findViewById(R.id.officeLocationText);
        MaterialButton editButton = dialogView.findViewById(R.id.editButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Enable editing
        nameText.setEnabled(true);
        emailText.setEnabled(true);
        roleText.setEnabled(true);
        departmentText.setEnabled(true);
        courseText.setEnabled(true);
        officeHoursText.setEnabled(true);
        officeLocationText.setEnabled(true);

        // Set values
        nameText.setText(selectedGaTa.getName());
        emailText.setText(selectedGaTa.getEmail());
        roleText.setText(selectedGaTa.getRole());
        departmentText.setText(selectedGaTa.getDepartment());
        courseText.setText(selectedGaTa.getCourse());
        officeHoursText.setText(selectedGaTa.getOfficeHours());
        officeLocationText.setText(selectedGaTa.getOfficeLocation());

        // Create dialog
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle("Edit GA/TA")
                .setView(dialogView)
                .setCancelable(false);

        AlertDialog dialog = dialogBuilder.create();

        // Set button click listeners
        editButton.setText("Save");
        editButton.setOnClickListener(v -> {
            String name = nameText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String role = roleText.getText().toString().trim();
            String department = departmentText.getText().toString().trim();
            String course = courseText.getText().toString().trim();
            String officeHours = officeHoursText.getText().toString().trim();
            String officeLocation = officeLocationText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || role.isEmpty() ||
                department.isEmpty() || course.isEmpty() ||
                officeHours.isEmpty() || officeLocation.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedGaTa.setName(name);
            selectedGaTa.setEmail(email);
            selectedGaTa.setRole(role);
            selectedGaTa.setDepartment(department);
            selectedGaTa.setCourse(course);
            selectedGaTa.setOfficeHours(officeHours);
            selectedGaTa.setOfficeLocation(officeLocation);

            updateGaTaInFirestore(selectedGaTa);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmationDialog() {
        if (selectedGaTa == null) {
            Toast.makeText(this, "Please select a GA/TA to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete GA/TA")
                .setMessage("Are you sure you want to delete " + selectedGaTa.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteGaTaFromFirestore(selectedGaTa))
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

    private void updateGaTaInFirestore(GaTa gaTa) {
        db.collection("ga_ta")
                .document(gaTa.getId())
                .set(gaTa)
                .addOnSuccessListener(aVoid -> {
                    adapter.updateList(gaTaItems);
                    Toast.makeText(this, "GA/TA updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating GA/TA: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteGaTaFromFirestore(GaTa gaTa) {
        db.collection("ga_ta")
                .document(gaTa.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    gaTaItems.remove(gaTa);
                    adapter.updateList(gaTaItems);
                    selectedGaTa = null;
                    updateFabVisibility();
                    Toast.makeText(this, "GA/TA deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting GA/TA: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            loadGaTaData();
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