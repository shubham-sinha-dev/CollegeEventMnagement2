package com.example.collegeeventmnagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etConfirmPassword;
    Button btnRegister;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword =
                etConfirmPassword.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            etName.setError("Enter Name");
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Enter Email");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter Password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm Password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password does not match");
            etConfirmPassword.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);

        // Create Firebase account
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    if (authResult.getUser() == null) {
                        btnRegister.setEnabled(true);
                        return;
                    }

                    String userId =
                            authResult.getUser().getUid();

                    // User information
                    Map<String, Object> user = new HashMap<>();

                    user.put("name", name);
                    user.put("email", email);
                    user.put("role", "student");

                    // Save user profile in Firestore
                    db.collection("users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Registration successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent =
                                        new Intent(
                                                RegisterActivity.this,
                                                MainActivity2.class
                                        );

                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {

                                btnRegister.setEnabled(true);

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Profile save failed: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {

                    btnRegister.setEnabled(true);

                    Toast.makeText(
                            RegisterActivity.this,
                            "Registration failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}