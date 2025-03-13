package com.example.helphub;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView messagesList;
    private TextInputLayout messageInputLayout;
    private TextInputEditText messageInput;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        initializeViews();
        setupToolbar();
        setupMessagesList();
        setupMessageInput();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        messagesList = findViewById(R.id.messagesList);
        messageInputLayout = findViewById(R.id.messageInputLayout);
        messageInput = findViewById(R.id.messageInput);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupMessagesList() {
        messagesList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(getSampleMessages());
        messagesList.setAdapter(adapter);
    }

    private void setupMessageInput() {
        messageInputLayout.setEndIconOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageInput.setText("");
            }
        });
    }

    private void sendMessage(String message) {
        // TODO: Implement actual message sending
        Toast.makeText(this, "Sending: " + message, Toast.LENGTH_SHORT).show();
    }

    private List<MessageItem> getSampleMessages() {
        // TODO: Replace with real messages from database
        List<MessageItem> messages = new ArrayList<>();
        messages.add(new MessageItem(
            "Lalit Kumar",
            "Hello! How can I help you with programming today?",
            "10:30 AM",
            false
        ));
        messages.add(new MessageItem(
            "You",
            "Hi, I have a question about arrays in Java",
            "10:31 AM",
            true
        ));
        messages.add(new MessageItem(
            "Lalit Kumar",
            "Sure, what would you like to know?",
            "10:32 AM",
            false
        ));
        return messages;
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