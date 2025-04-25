package com.example.helphub;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, GaTaAdapter.OnGaTaClickListener {
    private RecyclerView gaTaList;
    private TextInputEditText searchInput;
    private BottomNavigationView bottomNavigation;
    private MaterialToolbar topAppBar;
    private GaTaAdapter adapter;
    private List<GaTaItem> allGaTaItems;
    private List<GaTaItem> filteredGaTaItems;
    private FirebaseAuth mAuth;
    private DatabaseReference gaTaRef;
    private ValueEventListener gaTaListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views and data
        initializeViews();
        setupListeners();
        initializeFirebaseData();
        setupRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.profile) {
            showProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onEditClick(GaTaItem item) {
        // Not available for regular users — no action needed
    }

    @Override
    public void onDeleteClick(GaTaItem item) {
        // Not available for regular users — no action needed
    }


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");


    private void initializeViews() {
        gaTaList = findViewById(R.id.gaTaList);
        searchInput = findViewById(R.id.searchInput);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        topAppBar = findViewById(R.id.topAppBar);
        
        // Set up the toolbar
        setSupportActionBar(topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    private void initializeFirebaseData() {
        allGaTaItems = new ArrayList<>();
        filteredGaTaItems = new ArrayList<>();
        
        // Initialize Firebase Database reference for GA/TA data
        gaTaRef = FirebaseDatabase.getInstance().getReference("gaTas");
        
        // Add real-time listener for GA/TA data
        gaTaListener = gaTaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allGaTaItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        GaTaItem gaTa = snapshot.getValue(GaTaItem.class);
                        if (gaTa != null) {
                            gaTa.setId(snapshot.getKey()); // Store the Firebase key
                            allGaTaItems.add(gaTa);
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, 
                            "Error loading GA/TA data: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                }
                filteredGaTaItems = new ArrayList<>(allGaTaItems);
                if (adapter != null) {
                    adapter.updateItems(filteredGaTaItems);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, 
                    "Failed to load GA/TA data: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isAdmin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        // Check if user is admin (you should implement proper admin checking logic)
        // This is a simplified example
        return currentUser.getEmail() != null && 
               currentUser.getEmail().endsWith("@admin.com");
    }

    public void addNewGaTa(String name, String course, boolean available) {
        if (!isAdmin()) {
            Toast.makeText(this, 
                "Only admins can add new GA/TA entries", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        if (name == null || name.trim().isEmpty() || 
            course == null || course.trim().isEmpty()) {
            Toast.makeText(this, 
                "Name and course cannot be empty", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        GaTaItem newGaTa = new GaTaItem(name.trim(), course.trim(), available, null);
        DatabaseReference newGaTaRef = gaTaRef.push();
        newGaTaRef.setValue(newGaTa)
            .addOnSuccessListener(aVoid -> 
                Toast.makeText(MainActivity.this, 
                    "Successfully added new GA/TA", 
                    Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> 
                Toast.makeText(MainActivity.this, 
                    "Failed to add GA/TA: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the listener when activity is destroyed
        if (gaTaRef != null && gaTaListener != null) {
            gaTaRef.removeEventListener(gaTaListener);
        }
    }

    private void setupListeners() {
        // Set up bottom navigation
        bottomNavigation.setOnItemSelectedListener(this);

        // Set up search functionality with real-time filtering
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterGaTaList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterGaTaList(String query) {
        if (query.isEmpty()) {
            filteredGaTaItems = new ArrayList<>(allGaTaItems);
        } else {
            query = query.toLowerCase();
            filteredGaTaItems = new ArrayList<>();
            for (GaTaItem item : allGaTaItems) {
                if (item.getName().toLowerCase().contains(query) ||
                    item.getCourse().toLowerCase().contains(query)) {
                    filteredGaTaItems.add(item);
                }
            }
        }
        adapter.updateItems(filteredGaTaItems);
    }

    private void setupRecyclerView() {
        gaTaList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GaTaAdapter(filteredGaTaItems, this, false); // false = not admin
        gaTaList.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.home) {
            // Already on home
            return true;
        } else if (itemId == R.id.appointments) {
            startActivity(new Intent(this, AppointmentsActivity.class));
            return true;
        } else if (itemId == R.id.messages) {
            startActivity(new Intent(this, MessagesActivity.class));
            return true;
        } else if (itemId == R.id.schedule) {
            startActivity(new Intent(this, ScheduleActivity.class));
            return true;
        }
        return false;
    }

    @Override
    public void onBookClick(GaTaItem item) {
        // Launch booking activity with the selected GA/TA's information
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("ga_ta_name", item.getName());
        intent.putExtra("ga_ta_course", item.getCourse());
        startActivity(intent);
    }

    @Override
    public void onItemClick(GaTaItem item) {
        // Show GA/TA details
        Intent intent = new Intent(this, GaTaDetailsActivity.class);
        intent.putExtra("ga_ta_name", item.getName());
        intent.putExtra("ga_ta_course", item.getCourse());
        intent.putExtra("ga_ta_available", item.isAvailable());
        startActivity(intent);
    }

    private void showProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}