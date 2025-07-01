package com.example.newteachverse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class registrationpage extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvAlreadyHaveAccount, tvDisplayInfo;

    private EventDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationpage);

        etUsername = findViewById(R.id.etusernameenterrp);
        etEmail = findViewById(R.id.etemailenterrp);
        etPassword = findViewById(R.id.etpasswordenterrp);
        etConfirmPassword = findViewById(R.id.etconfrompasswordenterrp);
        btnRegister = findViewById(R.id.btregisterrp);
        tvAlreadyHaveAccount = findViewById(R.id.tvalreadyhaveanacrp);

        tvDisplayInfo = new TextView(this);
        ((android.widget.LinearLayout) findViewById(R.id.main)).addView(tvDisplayInfo);

        dbHelper = new EventDatabaseHelper(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        tvAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser() {
        String name = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        int existingId = dbHelper.getStudentIdByEmail(email);
        if (existingId != -1) {
            Toast.makeText(this, "This email is already registered.", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.addStudent(name, email, password);
        if (result != -1) {
            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            clearFields();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(registrationpage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 1000);
        } else {
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        etUsername.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }
}