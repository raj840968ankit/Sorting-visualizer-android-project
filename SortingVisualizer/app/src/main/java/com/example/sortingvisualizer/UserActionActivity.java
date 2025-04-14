package com.example.sortingvisualizer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserActionActivity extends AppCompatActivity {

    private ListView actionListView;
    private TextView titleText;
    private String[] actions = {"Update Profile", "Logout"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_action);

        String username = getIntent().getStringExtra("username");

        // Initialize Views
        titleText = findViewById(R.id.titleText);
        actionListView = findViewById(R.id.actionListView);

        // Set welcome title
        if (username != null) {
            titleText.setText("Welcome " + username);
        }

        // Set up ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, actions);
        actionListView.setAdapter(adapter);

        actionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Update Profile
                    Intent intent = new Intent(UserActionActivity.this, UpdateProfileActivity.class);
                    intent.putExtra("username", username); // pass username if needed there
                    startActivity(intent);
                } else if (position == 1) {
                    // Logout
                    Intent intent = new Intent(UserActionActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
