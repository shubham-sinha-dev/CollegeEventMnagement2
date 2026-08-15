package com.example.collegeeventmnagement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    private LinearLayout eventsContainer;
    private EditText etSearchEvent;
    private Spinner spinnerFilter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private boolean isAdmin = false;

    private final List<EventData> allEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events);

        eventsContainer = findViewById(R.id.eventsContainer);
        etSearchEvent = findViewById(R.id.etSearchEvent);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        setupFilter();
        setupSearch();
        checkUserRole();
    }

    private void setupFilter() {

        String[] filters = {
                "All Events",
                "Upcoming Events"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filters
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        filterEvents();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );
    }

    private void setupSearch() {

        etSearchEvent.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterEvents();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    private void checkUserRole() {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        String role = document.getString("role");

                        isAdmin = role != null &&
                                role.equalsIgnoreCase("admin");

                    } else {

                        isAdmin = false;
                    }

                    loadAllEvents();
                })
                .addOnFailureListener(e -> {

                    isAdmin = false;

                    Toast.makeText(
                            this,
                            "Unable to check user role.",
                            Toast.LENGTH_LONG
                    ).show();

                    loadAllEvents();
                });
    }

    private void loadAllEvents() {

        eventsContainer.removeAllViews();
        allEvents.clear();

        db.collection("events")
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showNoEvents(
                                "No events available yet."
                        );

                        return;
                    }

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String eventId = document.getId();

                        String eventName =
                                document.getString("eventName");

                        String eventDate =
                                document.getString("eventDate");

                        String venue =
                                document.getString("venue");

                        if (eventName == null) {
                            eventName = "N/A";
                        }

                        if (eventDate == null) {
                            eventDate = "N/A";
                        }

                        if (venue == null) {
                            venue = "N/A";
                        }

                        EventData event = new EventData(
                                eventId,
                                eventName,
                                eventDate,
                                venue
                        );

                        allEvents.add(event);
                    }

                    filterEvents();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to load events: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void filterEvents() {

        if (eventsContainer == null) {
            return;
        }

        eventsContainer.removeAllViews();

        String searchText = etSearchEvent
                .getText()
                .toString()
                .trim()
                .toLowerCase();

        String selectedFilter = spinnerFilter
                .getSelectedItem()
                .toString();

        int count = 1;

        for (EventData event : allEvents) {

            boolean matchesSearch =
                    event.eventName
                            .toLowerCase()
                            .contains(searchText);

            if (!matchesSearch) {
                continue;
            }

            boolean matchesFilter = true;

            if (selectedFilter.equals("Upcoming Events")) {

                matchesFilter =
                        isUpcomingEvent(event.eventDate);
            }

            if (!matchesFilter) {
                continue;
            }

            addEventToScreen(event, count);

            count++;
        }

        if (count == 1) {

            showNoEvents(
                    "No matching events found."
            );
        }
    }

    private boolean isUpcomingEvent(String eventDate) {

        if (eventDate == null ||
                eventDate.trim().isEmpty()) {

            return false;
        }

        String[] dateFormats = {
                "dd/MM/yyyy",
                "dd-MM-yyyy",
                "yyyy-MM-dd",
                "dd MMM yyyy",
                "dd MMMM yyyy"
        };

        Date today = new Date();

        for (String format : dateFormats) {

            try {

                SimpleDateFormat sdf =
                        new SimpleDateFormat(
                                format,
                                Locale.getDefault()
                        );

                sdf.setLenient(false);

                Date parsedDate =
                        sdf.parse(eventDate.trim());

                if (parsedDate != null) {

                    return !parsedDate.before(today);
                }

            } catch (ParseException ignored) {
            }
        }

        return false;
    }

    private void addEventToScreen(
            EventData event,
            int count) {

        LinearLayout eventLayout =
                new LinearLayout(this);

        eventLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        eventLayout.setPadding(
                20,
                20,
                20,
                20
        );

        TextView number =
                new TextView(this);

        number.setText(
                "Event " + count
        );

        number.setTextSize(20);

        TextView details =
                new TextView(this);

        details.setText(
                "Event Name: "
                        + event.eventName
                        + "\n\n"
                        + "Event Date: "
                        + event.eventDate
                        + "\n"
                        + "Venue: "
                        + event.venue
        );

        details.setTextSize(17);

        eventLayout.addView(number);
        eventLayout.addView(details);

        if (!isAdmin) {

            Button register =
                    new Button(this);

            register.setText(
                    "Register for Event"
            );

            register.setOnClickListener(
                    v -> registerForEvent(
                            event.eventId,
                            event.eventName,
                            event.eventDate,
                            event.venue
                    )
            );

            eventLayout.addView(register);
        }

        if (isAdmin) {

            Button edit =
                    new Button(this);

            edit.setText("Edit Event");

            edit.setOnClickListener(
                    v -> editEvent(
                            event.eventId,
                            event.eventName,
                            event.eventDate,
                            event.venue
                    )
            );

            eventLayout.addView(edit);

            Button delete =
                    new Button(this);

            delete.setText("Delete Event");

            delete.setOnClickListener(
                    v -> deleteEvent(event.eventId)
            );

            eventLayout.addView(delete);
        }

        eventsContainer.addView(eventLayout);

        TextView separator =
                new TextView(this);

        separator.setText(
                "--------------------------------"
        );

        separator.setGravity(Gravity.CENTER);

        eventsContainer.addView(separator);
    }

    private void showNoEvents(String message) {

        TextView text =
                new TextView(this);

        text.setText(message);

        text.setTextSize(18);

        text.setPadding(
                20,
                40,
                20,
                40
        );

        text.setGravity(Gravity.CENTER);

        eventsContainer.addView(text);
    }

    private void registerForEvent(
            String eventId,
            String eventName,
            String eventDate,
            String venue) {

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

        String userId =
                currentUser.getUid();

        String email =
                currentUser.getEmail();

        if (email == null) {
            email = "";
        }

        final String userEmail = email;

        db.collection("registrations")
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!querySnapshot.isEmpty()) {

                        Toast.makeText(
                                EventsActivity.this,
                                "You are already registered for this event.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    Map<String, Object> registration =
                            new HashMap<>();

                    registration.put(
                            "userId",
                            userId
                    );

                    registration.put(
                            "email",
                            userEmail
                    );

                    registration.put(
                            "eventId",
                            eventId
                    );

                    registration.put(
                            "eventName",
                            eventName
                    );

                    registration.put(
                            "eventDate",
                            eventDate
                    );

                    registration.put(
                            "venue",
                            venue
                    );

                    registration.put(
                            "registeredAt",
                            System.currentTimeMillis()
                    );

                    db.collection("registrations")
                            .add(registration)
                            .addOnSuccessListener(
                                    documentReference -> {

                                        Toast.makeText(
                                                EventsActivity.this,
                                                "Registration successful!",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                            )
                            .addOnFailureListener(
                                    e -> {

                                        Toast.makeText(
                                                EventsActivity.this,
                                                "Unable to register: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                            );
                })
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    EventsActivity.this,
                                    "Unable to check registration: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }
    // =====================================================
    // EDIT EVENT
    // =====================================================

    private void editEvent(
            String eventId,
            String oldName,
            String oldDate,
            String oldVenue) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Edit Event");

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,
                20,
                40,
                10
        );

        EditText nameInput =
                new EditText(this);

        nameInput.setHint("Event Name");
        nameInput.setText(oldName);

        EditText dateInput =
                new EditText(this);

        dateInput.setHint("Event Date");
        dateInput.setText(oldDate);

        EditText venueInput =
                new EditText(this);

        venueInput.setHint("Venue");
        venueInput.setText(oldVenue);

        layout.addView(nameInput);
        layout.addView(dateInput);
        layout.addView(venueInput);

        builder.setView(layout);

        builder.setNegativeButton(
                "Cancel",
                null
        );

        builder.setPositiveButton(
                "Update",
                (dialog, which) -> {

                    String newName =
                            nameInput.getText()
                                    .toString()
                                    .trim();

                    String newDate =
                            dateInput.getText()
                                    .toString()
                                    .trim();

                    String newVenue =
                            venueInput.getText()
                                    .toString()
                                    .trim();

                    if (newName.isEmpty() ||
                            newDate.isEmpty() ||
                            newVenue.isEmpty()) {

                        Toast.makeText(
                                EventsActivity.this,
                                "Please fill all fields.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Map<String, Object> updateData =
                            new HashMap<>();

                    updateData.put(
                            "eventName",
                            newName
                    );

                    updateData.put(
                            "eventDate",
                            newDate
                    );

                    updateData.put(
                            "venue",
                            newVenue
                    );

                    db.collection("events")
                            .document(eventId)
                            .update(updateData)
                            .addOnSuccessListener(
                                    unused -> {

                                        Toast.makeText(
                                                EventsActivity.this,
                                                "Event updated successfully!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        loadAllEvents();
                                    }
                            )
                            .addOnFailureListener(
                                    e -> {

                                        Toast.makeText(
                                                EventsActivity.this,
                                                "Unable to update event: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                            );
                }
        );

        builder.show();
    }

    // =====================================================
    // DELETE EVENT
    // =====================================================

    private void deleteEvent(String eventId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage(
                        "Are you sure you want to delete this event?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection("events")
                                    .document(eventId)
                                    .delete()
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        EventsActivity.this,
                                                        "Event deleted successfully!",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                loadAllEvents();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> {

                                                Toast.makeText(
                                                        EventsActivity.this,
                                                        "Unable to delete event: "
                                                                + e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            }
                                    );
                        }
                )
                .show();
    }
    // =====================================================
    // EVENT DATA MODEL
    // =====================================================

    private static class EventData {

        String eventId;
        String eventName;
        String eventDate;
        String venue;

        EventData(
                String eventId,
                String eventName,
                String eventDate,
                String venue) {

            this.eventId = eventId;
            this.eventName = eventName;
            this.eventDate = eventDate;
            this.venue = venue;
        }
    }
}