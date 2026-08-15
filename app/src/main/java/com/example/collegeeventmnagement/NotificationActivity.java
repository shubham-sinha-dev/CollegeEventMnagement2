package com.example.collegeeventmnagement;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class NotificationActivity extends AppCompatActivity {

    private LinearLayout notificationContainer;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        notificationContainer =
                findViewById(R.id.notificationContainer);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadNotifications();
    }

    private void loadNotifications() {

        notificationContainer.removeAllViews();

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("notifications")
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        TextView noNotification =
                                new TextView(this);

                        noNotification.setText(
                                "No notifications available."
                        );

                        noNotification.setTextSize(18);

                        noNotification.setPadding(
                                20,
                                40,
                                20,
                                40
                        );

                        notificationContainer.addView(
                                noNotification
                        );

                        return;
                    }

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String title =
                                document.getString("title");

                        String message =
                                document.getString("message");

                        if (title == null) {
                            title = "Notification";
                        }

                        if (message == null) {
                            message = "";
                        }

                        TextView notification =
                                new TextView(this);

                        notification.setText(
                                title
                                        + "\n\n"
                                        + message
                        );

                        notification.setTextSize(17);

                        notification.setPadding(
                                20,
                                20,
                                20,
                                20
                        );

                        notificationContainer.addView(
                                notification
                        );

                        TextView separator =
                                new TextView(this);

                        separator.setText(
                                "--------------------------------"
                        );

                        notificationContainer.addView(
                                separator
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            NotificationActivity.this,
                            "Unable to load notifications: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}