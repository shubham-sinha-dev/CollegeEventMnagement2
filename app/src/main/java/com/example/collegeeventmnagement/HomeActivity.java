package com.example.collegeeventmnagement ;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private Button btnEvents;
    private Button btnCreateEvent;
    private Button btnMyEvents;
    private Button btnNotifications;
    private Button btnLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // =========================
        // FIND BUTTONS
        // =========================

        btnEvents =
                findViewById(R.id.btnEvents);

        btnCreateEvent =
                findViewById(R.id.btnCreateEvent);

        btnMyEvents =
                findViewById(R.id.btnMyEvents);

        btnNotifications =
                findViewById(R.id.btnNotifications);

        btnLogout =
                findViewById(R.id.btnLogout);

        // =========================
        // FIREBASE
        // =========================

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        // =========================
        // HIDE CREATE EVENT
        // FOR STUDENT
        // =========================

        btnCreateEvent.setVisibility(
                View.GONE
        );

        checkUserRole();

        // =========================
        // VIEW EVENTS
        // =========================

        btnEvents.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            EventsActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // MY REGISTERED EVENTS
        // =========================

        btnMyEvents.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            MyEventsActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // NOTIFICATIONS
        // =========================

        btnNotifications.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            NotificationActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // LOGOUT
        // =========================

        btnLogout.setOnClickListener(v -> {

            auth.signOut();

            getSharedPreferences(
                    "LoginData",
                    MODE_PRIVATE
            )
                    .edit()
                    .clear()
                    .apply();

            Intent intent =
                    new Intent(
                            HomeActivity.this,
                            MainActivity2.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });
    }

    // =====================================================
    // CHECK USER ROLE
    // =====================================================

    private void checkUserRole() {

        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        btnCreateEvent.setVisibility(
                                View.GONE
                        );

                        return;
                    }

                    String role =
                            document.getString("role");

                    if (role != null &&
                            role.equalsIgnoreCase("admin")) {

                        // ADMIN
                        btnCreateEvent.setVisibility(
                                View.VISIBLE
                        );

                        btnCreateEvent.setOnClickListener(
                                v -> {

                                    Intent intent =
                                            new Intent(
                                                    HomeActivity.this,
                                                    CreateEventActivity.class
                                            );

                                    startActivity(intent);
                                }
                        );

                    } else {

                        // STUDENT
                        btnCreateEvent.setVisibility(
                                View.GONE
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    btnCreateEvent.setVisibility(
                            View.GONE
                    );

                    Toast.makeText(
                            HomeActivity.this,
                            "Unable to check user role.",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}