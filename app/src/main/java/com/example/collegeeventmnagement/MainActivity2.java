package com.example.collegeeventmnagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity2 extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private static final String PREF_NAME = "LoginData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        // FIND VIEWS
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // FIREBASE
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // TEST ACCOUNT
        etEmail.setText("test@gmail.com");
        etPassword.setText("123456");

        // REGISTER BUTTON
        tvRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity2.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        // LOGIN BUTTON
        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email =
                etEmail.getText().toString().trim();

        String password =
                etPassword.getText().toString().trim();

        // EMAIL CHECK
        if (email.isEmpty()) {

            etEmail.setError("Email required");
            etEmail.requestFocus();
            return;
        }

        // PASSWORD CHECK
        if (password.isEmpty()) {

            etPassword.setError("Password required");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        // FIREBASE LOGIN
        auth.signInWithEmailAndPassword(
                        email,
                        password
                )
                .addOnSuccessListener(authResult -> {

                    FirebaseUser currentUser =
                            auth.getCurrentUser();

                    if (currentUser == null) {

                        btnLogin.setEnabled(true);

                        Toast.makeText(
                                MainActivity2.this,
                                "Login failed.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    String uid =
                            currentUser.getUid();

                    // FIRESTORE USER DATA
                    db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {

                                btnLogin.setEnabled(true);

                                if (!documentSnapshot.exists()) {

                                    Toast.makeText(
                                            MainActivity2.this,
                                            "User data not found in Firestore.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                String role =
                                        documentSnapshot.getString("role");

                                if (role == null) {
                                    role = "user";
                                }

                                // SAVE LOGIN INFORMATION
                                SharedPreferences preferences =
                                        getSharedPreferences(
                                                PREF_NAME,
                                                MODE_PRIVATE
                                        );

                                preferences.edit()
                                        .putBoolean(
                                                "isLoggedIn",
                                                true
                                        )
                                        .putString(
                                                "email",
                                                email
                                        )
                                        .putString(
                                                "role",
                                                role
                                        )
                                        .apply();

                                // ADMIN
                                if (role.equalsIgnoreCase("admin")) {

                                    Intent intent =
                                            new Intent(
                                                    MainActivity2.this,
                                                    AdminActivity.class
                                            );

                                    intent.setFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );

                                    startActivity(intent);
                                    finish();

                                } else {

                                    // STUDENT / NORMAL USER
                                    Intent intent =
                                            new Intent(
                                                    MainActivity2.this,
                                                    HomeActivity.class
                                            );

                                    intent.setFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );

                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(e -> {

                                btnLogin.setEnabled(true);

                                Toast.makeText(
                                        MainActivity2.this,
                                        "Unable to get user data: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {

                    btnLogin.setEnabled(true);

                    Toast.makeText(
                            MainActivity2.this,
                            "Login failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}