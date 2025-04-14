package com.example.sortingvisualizer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword;
    private Button btnUpdate;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        dbHelper = new DatabaseHelper(this);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});

        if (cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);

            int result = db.update("users", values, "email=?", new String[]{email});
            db.close();

            if (result > 0) {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update Failed! No user found with this email.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Update Failed! No user found with this email.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}