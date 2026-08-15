package com.example.collegeeventmnagement;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrationsActivity extends AppCompatActivity {

    private LinearLayout registrationsContainer;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registrations);

        registrationsContainer =
                findViewById(R.id.registrationsContainer);

        db = FirebaseFirestore.getInstance();

        loadRegistrations();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (registrationsContainer != null) {
            loadRegistrations();
        }
    }

    // =====================================================
    // LOAD ALL REGISTRATIONS
    // =====================================================

    private void loadRegistrations() {

        registrationsContainer.removeAllViews();

        db.collection("registrations")
                .orderBy(
                        "registeredAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    // =================================================
                    // NO REGISTRATIONS
                    // =================================================

                    if (querySnapshot.isEmpty()) {

                        TextView noRegistrations =
                                new TextView(this);

                        noRegistrations.setText(
                                "No registrations found."
                        );

                        noRegistrations.setTextSize(18);

                        noRegistrations.setPadding(
                                20,
                                40,
                                20,
                                40
                        );

                        registrationsContainer.addView(
                                noRegistrations
                        );

                        return;
                    }

                    // =================================================
                    // TOTAL REGISTRATIONS
                    // =================================================

                    TextView totalRegistrations =
                            new TextView(this);

                    totalRegistrations.setText(
                            "Total Registrations: "
                                    + querySnapshot.size()
                    );

                    totalRegistrations.setTextSize(20);

                    totalRegistrations.setTextColor(
                            0xFF000000
                    );

                    totalRegistrations.setPadding(
                            20,
                            20,
                            20,
                            25
                    );

                    registrationsContainer.addView(
                            totalRegistrations
                    );

                    // =================================================
                    // REGISTRATION COUNT
                    // =================================================

                    int count = 1;

                    // =================================================
                    // LOOP THROUGH REGISTRATIONS
                    // =================================================

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        // =================================================
                        // GET DATA
                        // =================================================

                        String userIdValue =
                                document.getString("userId");

                        String emailValue =
                                document.getString("email");

                        String eventNameValue =
                                document.getString("eventName");

                        String eventDateValue =
                                document.getString("eventDate");

                        String venueValue =
                                document.getString("venue");

                        Long registeredAtValue =
                                document.getLong("registeredAt");

                        // =================================================
                        // NULL CHECK
                        // =================================================

                        final String userId =
                                userIdValue == null
                                        ? ""
                                        : userIdValue;

                        final String email =
                                emailValue == null
                                        ? "N/A"
                                        : emailValue;

                        final String eventName =
                                eventNameValue == null
                                        ? "N/A"
                                        : eventNameValue;

                        final String eventDate =
                                eventDateValue == null
                                        ? "N/A"
                                        : eventDateValue;

                        final String venue =
                                venueValue == null
                                        ? "N/A"
                                        : venueValue;

                        // =================================================
                        // REGISTRATION DATE AND TIME
                        // =================================================

                        String registeredAtText =
                                "N/A";

                        if (registeredAtValue != null) {

                            SimpleDateFormat dateFormat =
                                    new SimpleDateFormat(
                                            "dd MMM yyyy, hh:mm a",
                                            Locale.getDefault()
                                    );

                            registeredAtText =
                                    dateFormat.format(
                                            new Date(
                                                    registeredAtValue
                                            )
                                    );
                        }

                        final String finalRegisteredAtText =
                                registeredAtText;

                        // =================================================
                        // MAIN REGISTRATION LAYOUT
                        // =================================================

                        LinearLayout registrationLayout =
                                new LinearLayout(this);

                        registrationLayout.setOrientation(
                                LinearLayout.VERTICAL
                        );

                        registrationLayout.setPadding(
                                20,
                                20,
                                20,
                                20
                        );

                        // =================================================
                        // REGISTRATION NUMBER
                        // =================================================

                        TextView registrationNumber =
                                new TextView(this);

                        registrationNumber.setText(
                                "Registration " + count
                        );

                        registrationNumber.setTextSize(
                                20
                        );

                        registrationNumber.setTextColor(
                                0xFF000000
                        );

                        registrationNumber.setPadding(
                                0,
                                0,
                                0,
                                10
                        );

                        registrationLayout.addView(
                                registrationNumber
                        );

                        // =================================================
                        // DETAILS
                        // =================================================

                        TextView details =
                                new TextView(this);

                        details.setText(
                                "Student Name: Loading..."
                                        + "\n\n"
                                        + "Student Email: "
                                        + email
                                        + "\n\n"
                                        + "Event Name: "
                                        + eventName
                                        + "\n"
                                        + "Event Date: "
                                        + eventDate
                                        + "\n"
                                        + "Venue: "
                                        + venue
                                        + "\n"
                                        + "Registered On: "
                                        + finalRegisteredAtText
                        );

                        details.setTextSize(16);

                        details.setTextColor(
                                0xFF000000
                        );

                        details.setPadding(
                                0,
                                10,
                                0,
                                10
                        );

                        registrationLayout.addView(
                                details
                        );

                        registrationsContainer.addView(
                                registrationLayout
                        );

                        // =================================================
                        // LOAD STUDENT NAME
                        // =================================================

                        if (!userId.isEmpty()) {

                            db.collection("users")
                                    .document(userId)
                                    .get()
                                    .addOnSuccessListener(
                                            userDocument -> {

                                                String studentName =
                                                        "N/A";

                                                if (userDocument.exists()) {

                                                    String nameValue =
                                                            userDocument
                                                                    .getString(
                                                                            "name"
                                                                    );

                                                    if (nameValue != null
                                                            && !nameValue
                                                            .trim()
                                                            .isEmpty()) {

                                                        studentName =
                                                                nameValue;
                                                    }
                                                }

                                                details.setText(
                                                        "Student Name: "
                                                                + studentName
                                                                + "\n\n"
                                                                + "Student Email: "
                                                                + email
                                                                + "\n\n"
                                                                + "Event Name: "
                                                                + eventName
                                                                + "\n"
                                                                + "Event Date: "
                                                                + eventDate
                                                                + "\n"
                                                                + "Venue: "
                                                                + venue
                                                                + "\n"
                                                                + "Registered On: "
                                                                + finalRegisteredAtText
                                                );
                                            }
                                    );
                        } else {

                            details.setText(
                                    "Student Name: N/A"
                                            + "\n\n"
                                            + "Student Email: "
                                            + email
                                            + "\n\n"
                                            + "Event Name: "
                                            + eventName
                                            + "\n"
                                            + "Event Date: "
                                            + eventDate
                                            + "\n"
                                            + "Venue: "
                                            + venue
                                            + "\n"
                                            + "Registered On: "
                                            + finalRegisteredAtText
                            );
                        }

                        // =================================================
                        // SEPARATOR
                        // =================================================

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

                        registrationsContainer.addView(
                                separator
                        );

                        count++;
                    }
                })
                .addOnFailureListener(e -> {

                    String errorMessage =
                            e.getMessage();

                    if (errorMessage == null
                            || errorMessage.trim().isEmpty()) {

                        errorMessage =
                                "Unknown Firebase error";
                    }

                    Toast.makeText(
                            RegistrationsActivity.this,
                            "Unable to load registrations: "
                                    + errorMessage,
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}