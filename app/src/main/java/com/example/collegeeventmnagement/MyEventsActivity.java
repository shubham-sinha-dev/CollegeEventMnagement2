package com.example.collegeeventmnagement;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyEventsActivity extends AppCompatActivity {

    private LinearLayout registeredContainer;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_events);

        registeredContainer =
                findViewById(R.id.registeredContainer);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadMyEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (registeredContainer != null) {
            loadMyEvents();
        }
    }

    private void loadMyEvents() {

        registeredContainer.removeAllViews();

        FirebaseUser currentUser =
                auth.getCurrentUser();

        // Check login
        if (currentUser == null) {

            showMessage(
                    "Please login first."
            );

            return;
        }

        String userId =
                currentUser.getUid();

        // Load registrations of current student
        db.collection("registrations")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    // No registered events
                    if (querySnapshot.isEmpty()) {

                        showMessage(
                                "You have not registered for any event yet."
                        );

                        return;
                    }

                    int count = 1;

                    // Loop through registered events
                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String eventName =
                                document.getString(
                                        "eventName"
                                );

                        String eventDate =
                                document.getString(
                                        "eventDate"
                                );

                        String venue =
                                document.getString(
                                        "venue"
                                );

                        // Null check
                        if (eventName == null ||
                                eventName.trim().isEmpty()) {

                            eventName = "N/A";
                        }

                        if (eventDate == null ||
                                eventDate.trim().isEmpty()) {

                            eventDate = "N/A";
                        }

                        if (venue == null ||
                                venue.trim().isEmpty()) {

                            venue = "N/A";
                        }

                        // Registration details
                        TextView eventDetails =
                                new TextView(this);

                        eventDetails.setText(
                                "Registration " + count
                                        + "\n\n"
                                        + "Event Name: "
                                        + eventName
                                        + "\n\n"
                                        + "Event Date: "
                                        + eventDate
                                        + "\n\n"
                                        + "Venue: "
                                        + venue
                        );

                        eventDetails.setTextSize(17);

                        eventDetails.setPadding(
                                20,
                                25,
                                20,
                                25
                        );

                        registeredContainer.addView(
                                eventDetails
                        );

                        // Separator
                        TextView separator =
                                new TextView(this);

                        separator.setText(
                                "--------------------------------"
                        );

                        separator.setTextSize(16);

                        separator.setPadding(
                                20,
                                5,
                                20,
                                5
                        );

                        registeredContainer.addView(
                                separator
                        );

                        count++;
                    }
                })
                .addOnFailureListener(e -> {

                    String errorMessage =
                            e.getMessage();

                    if (errorMessage == null ||
                            errorMessage.trim().isEmpty()) {

                        errorMessage =
                                "Unknown Firebase error";
                    }

                    Toast.makeText(
                            MyEventsActivity.this,
                            "Unable to load your events: "
                                    + errorMessage,
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showMessage(String message) {

        TextView textView =
                new TextView(this);

        textView.setText(message);

        textView.setTextSize(18);

        textView.setPadding(
                20,
                40,
                20,
                40
        );

        registeredContainer.addView(
                textView
        );
    }
}