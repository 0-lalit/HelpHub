package com.example.helphub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity implements GaTaAdapter.OnGaTaClickListener {
    private RecyclerView gaTaList;
    private MaterialButton addGaTaButton, logoutButton;
    private GaTaAdapter adapter;
    private List<GaTaItem> items = new ArrayList<>();
    private DatabaseReference gaTaRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        gaTaList = findViewById(R.id.adminGaTaList);
        addGaTaButton = findViewById(R.id.addGaTaButton);
        logoutButton = findViewById(R.id.logoutButton);
        gaTaRef = FirebaseDatabase.getInstance().getReference("gaTas");

        adapter = new GaTaAdapter(items, this, true);
        gaTaList.setLayoutManager(new LinearLayoutManager(this));
        gaTaList.setAdapter(adapter);

        loadGaTas();

        addGaTaButton.setOnClickListener(v -> showAddDialog());

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadGaTas() {
        gaTaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    GaTaItem item = snap.getValue(GaTaItem.class);
                    if (item != null) {
                        item.setId(snap.getKey());
                        items.add(item);
                    }
                }
                adapter.updateItems(items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminHomeActivity.this, "Failed to load GA/TAs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_gata, null);
        EditText nameInput = view.findViewById(R.id.nameInput);
        EditText courseInput = view.findViewById(R.id.courseInput);
        CheckBox availabilityCheckbox = view.findViewById(R.id.availabilityCheckBox);

        new AlertDialog.Builder(this)
                .setTitle("Add GA/TA")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String course = courseInput.getText().toString().trim();
                    boolean available = availabilityCheckbox.isChecked();
                    if (!name.isEmpty() && !course.isEmpty()) {
                        String key = gaTaRef.push().getKey();
                        GaTaItem newItem = new GaTaItem(name, course, available, null);
                        newItem.setId(key);
                        gaTaRef.child(key).setValue(newItem);
                    } else {
                        Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(GaTaItem item) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_gata, null);
        EditText nameInput = view.findViewById(R.id.nameInput);
        EditText courseInput = view.findViewById(R.id.courseInput);
        CheckBox availabilityCheckbox = view.findViewById(R.id.availabilityCheckBox);

        nameInput.setText(item.getName());
        courseInput.setText(item.getCourse());
        availabilityCheckbox.setChecked(item.isAvailable());

        new AlertDialog.Builder(this)
                .setTitle("Edit GA/TA")
                .setView(view)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    String newCourse = courseInput.getText().toString().trim();
                    boolean newAvailable = availabilityCheckbox.isChecked();

                    if (!newName.isEmpty() && !newCourse.isEmpty()) {
                        item.setName(newName);
                        item.setCourse(newCourse);
                        item.setAvailable(newAvailable);
                        gaTaRef.child(item.getId()).setValue(item);
                    } else {
                        Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBookClick(GaTaItem item) {}

    @Override
    public void onItemClick(GaTaItem item) {
        showEditDialog(item);
    }

    @Override
    public void onEditClick(GaTaItem item) {
        showEditDialog(item);
    }

    @Override
    public void onDeleteClick(GaTaItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete GA/TA")
                .setMessage("Are you sure you want to delete this GA/TA?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (item.getId() != null) {
                        gaTaRef.child(item.getId()).removeValue();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
