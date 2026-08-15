package com.example.collegeeventmnagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etEventName;
    private EditText etEventDate;
    private EditText etEventVenue;

    private Button btnSaveEvent;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_event);

        etEventName =
                findViewById(R.id.etEventName);

        etEventDate =
                findViewById(R.id.etEventDate);

        etEventVenue =
                findViewById(R.id.etEventVenue);

        btnSaveEvent =
                findViewById(R.id.btnSaveEvent);

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        btnSaveEvent.setOnClickListener(
                v -> saveEvent()
        );
    }

    private void saveEvent() {

        String eventName =
                etEventName.getText()
                        .toString()
                        .trim();

        String eventDate =
                etEventDate.getText()
                        .toString()
                        .trim();

        String venue =
                etEventVenue.getText()
                        .toString()
                        .trim();

        // Event name validation
        if (eventName.isEmpty()) {

            etEventName.setError(
                    "Enter event name"
            );

            etEventName.requestFocus();

            return;
        }

        // Event date validation
        if (eventDate.isEmpty()) {

            etEventDate.setError(
                    "Enter event date"
            );

            etEventDate.requestFocus();

            return;
        }

        // Venue validation
        if (venue.isEmpty()) {

            etEventVenue.setError(
                    "Enter venue"
            );

            etEventVenue.requestFocus();

            return;
        }

        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnSaveEvent.setEnabled(false);

        // Event data
        Map<String, Object> event =
                new HashMap<>();

        event.put(
                "eventName",
                eventName
        );

        event.put(
                "eventDate",
                eventDate
        );

        event.put(
                "venue",
                venue
        );

        event.put(
                "createdBy",
                currentUser.getUid()
        );

        event.put(
                "createdAt",
                System.currentTimeMillis()
        );

        // Save to Firestore
        db.collection("events")
                .add(event)
                .addOnSuccessListener(
                        documentReference -> {

                            Toast.makeText(
                                    CreateEventActivity.this,
                                    "Event created successfully!",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            btnSaveEvent.setEnabled(true);

                            String error =
                                    e.getMessage();

                            if (error == null ||
                                    error.trim().isEmpty()) {

                                error =
                                        "Unknown Firebase error";
                            }

                            Toast.makeText(
                                    CreateEventActivity.this,
                                    "Unable to create event: "
                                            + error,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }
}