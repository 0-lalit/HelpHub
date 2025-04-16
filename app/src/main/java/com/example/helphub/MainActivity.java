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
        initializeSampleData();
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

    private void initializeSampleData() {
        allGaTaItems = new ArrayList<>();
        allGaTaItems.add(new GaTaItem("Lalit Kumar", "CS 101 - Introduction to Programming", true, null));
        allGaTaItems.add(new GaTaItem("Orchard Johnson", "CS 202 - Data Structures", false, null));
        allGaTaItems.add(new GaTaItem("Hawaii Smith", "CS 303 - Algorithms", true, null));
        allGaTaItems.add(new GaTaItem("Alex Thompson", "CS 401 - Software Engineering", true, null));
        allGaTaItems.add(new GaTaItem("Maria Garcia", "CS 205 - Database Systems", false, null));
        
        filteredGaTaItems = new ArrayList<>(allGaTaItems);
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
        adapter = new GaTaAdapter(filteredGaTaItems, this);
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