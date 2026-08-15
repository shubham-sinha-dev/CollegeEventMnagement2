package com.example.collegeeventmnagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    Button btnCreateEvent;
    Button btnManageEvents;
    Button btnMyEvents;
    Button btnRegistrations;
    Button btnNotifications;
    Button btnLogout;

    private static final String PREF_NAME = "LoginData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);

        btnCreateEvent =
                findViewById(R.id.btnCreateEvent);

        btnManageEvents =
                findViewById(R.id.btnManageEvents);

        btnMyEvents =
                findViewById(R.id.btnMyEvents);

        btnRegistrations =
                findViewById(R.id.btnRegistrations);

        btnNotifications =
                findViewById(R.id.btnNotifications);

        btnLogout =
                findViewById(R.id.btnLogout);

        // CREATE EVENT
        btnCreateEvent.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminActivity.this,
                            CreateEventActivity.class
                    );

            startActivity(intent);
        });

        // MANAGE EVENTS
        btnManageEvents.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminActivity.this,
                            EventsActivity.class
                    );

            startActivity(intent);
        });

        // MY EVENTS
        btnMyEvents.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminActivity.this,
                            MyEventsActivity.class
                    );

            startActivity(intent);
        });

        // REGISTRATIONS
        btnRegistrations.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminActivity.this,
                            RegistrationsActivity.class
                    );

            startActivity(intent);
        });

        // SEND NOTIFICATION
        btnNotifications.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminActivity.this,
                            NotificationAdminActivity.class
                    );

            startActivity(intent);
        });

        // LOGOUT
        btnLogout.setOnClickListener(v -> {

            SharedPreferences preferences =
                    getSharedPreferences(
                            PREF_NAME,
                            MODE_PRIVATE
                    );

            preferences.edit()
                    .putBoolean(
                            "isLoggedIn",
                            false
                    )
                    .apply();

            Intent intent =
                    new Intent(
                            AdminActivity.this,
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
}