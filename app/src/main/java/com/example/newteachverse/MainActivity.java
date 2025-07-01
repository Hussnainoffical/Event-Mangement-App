package com.example.newteachverse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    MaterialSwitch switchAdmin;
    Button btnLogin;
    TextView tvNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        boolean isAdmin = prefs.getBoolean("is_admin", false);
        if (isLoggedIn) {
            if (isAdmin) {
                startActivity(new Intent(this, AdminHomeActivity.class));
            } else {
                int studentId = prefs.getInt("studentId", -1);
                Intent i = new Intent(this, StudentHomeActivity.class);
                i.putExtra("studentId", studentId);
                startActivity(i);
            }
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        EventDatabaseHelper db = new EventDatabaseHelper(this);

        if (db.getStudentIdByEmail("student@test.com") == -1) {
            db.addStudent("Default Student", "student@test.com", "student123");
        }

        if (!db.isAdminExists("admin@test.com")) {
            db.addAdmin("Default Admin", "admin@test.com", "admin123");
        }

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            boolean isAdminChecked = switchAdmin.isChecked();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isAdminChecked) {
                if (db.isValidAdmin(email, password)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("is_logged_in", true);
                    editor.putString("email", email);
                    editor.putBoolean("is_admin", true);
                    editor.apply();
                    startActivity(new Intent(this, AdminHomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
                }
            } else {
                int studentId = db.getStudentIdByEmail(email);
                if (studentId != -1 && db.isValidStudent(email, password)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("is_logged_in", true);
                    editor.putString("email", email);
                    editor.putBoolean("is_admin", false);
                    editor.putInt("studentId", studentId);
                    editor.apply();
                    Intent i = new Intent(this, StudentHomeActivity.class);
                    i.putExtra("studentId", studentId);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, "Invalid student credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (tvNewUser == null) {
            tvNewUser = new TextView(this);
            tvNewUser.setText("New User? Register here");
            ((android.widget.LinearLayout) findViewById(R.id.main)).addView(tvNewUser);
        }
        tvNewUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, registrationpage.class);
            startActivity(intent);
        });
    }

    public void init() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        switchAdmin = findViewById(R.id.switchAdmin);
        btnLogin = findViewById(R.id.btnLogin);
        try {
            tvNewUser = findViewById(R.id.tvNewUser);
        } catch (Exception e) {
            tvNewUser = null;
        }
    }
}

