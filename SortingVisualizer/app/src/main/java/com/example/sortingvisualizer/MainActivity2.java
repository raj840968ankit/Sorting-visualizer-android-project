package com.example.sortingvisualizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword;
    private Button btnSignup;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);

        dbHelper = new DatabaseHelper(this);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (!validateUsername(username)) {
                    Toast.makeText(MainActivity2.this, "Username must contain letters and digits", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(MainActivity2.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                } else if (!validatePassword(password)) {
                    Toast.makeText(MainActivity2.this, "Password must be at least 6 characters long and include a lowercase letter, digit, and special character", Toast.LENGTH_LONG).show();
                } else {
                    boolean isInserted = dbHelper.insertUser(username, email, password);
                    if (isInserted) {
                        Toast.makeText(MainActivity2.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity2.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity2.this, "Signup Failed! Username may already exist.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean validateUsername(String username) {
        // Must contain at least one letter and one digit
        return username.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$");
    }

    private boolean validatePassword(String password) {
        // Minimum 6 characters, at least one lowercase, one digit, and one special character
        return password.matches("^(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$");
    }
}
