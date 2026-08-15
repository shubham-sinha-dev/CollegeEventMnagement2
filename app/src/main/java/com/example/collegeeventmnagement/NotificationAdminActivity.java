package com.example.collegeeventmnagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NotificationAdminActivity extends AppCompatActivity {

    private EditText etNotificationTitle;
    private EditText etNotificationMessage;
    private Button btnSendNotification;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_notification_admin
        );

        etNotificationTitle =
                findViewById(
                        R.id.etNotificationTitle
                );

        etNotificationMessage =
                findViewById(
                        R.id.etNotificationMessage
                );

        btnSendNotification =
                findViewById(
                        R.id.btnSendNotification
                );

        db = FirebaseFirestore.getInstance();

        btnSendNotification.setOnClickListener(
                v -> sendNotification()
        );
    }

    private void sendNotification() {

        String title =
                etNotificationTitle
                        .getText()
                        .toString()
                        .trim();

        String message =
                etNotificationMessage
                        .getText()
                        .toString()
                        .trim();

        if (title.isEmpty()) {

            etNotificationTitle.setError(
                    "Enter notification title"
            );

            etNotificationTitle.requestFocus();

            return;
        }

        if (message.isEmpty()) {

            etNotificationMessage.setError(
                    "Enter notification message"
            );

            etNotificationMessage.requestFocus();

            return;
        }

        btnSendNotification.setEnabled(false);

        Map<String, Object> notification =
                new HashMap<>();

        notification.put(
                "title",
                title
        );

        notification.put(
                "message",
                message
        );

        notification.put(
                "createdAt",
                System.currentTimeMillis()
        );

        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(
                        documentReference -> {

                            btnSendNotification
                                    .setEnabled(true);

                            Toast.makeText(
                                    NotificationAdminActivity.this,
                                    "Notification sent successfully",
                                    Toast.LENGTH_LONG
                            ).show();

                            etNotificationTitle
                                    .setText("");

                            etNotificationMessage
                                    .setText("");
                        }
                )
                .addOnFailureListener(
                        e -> {

                            btnSendNotification
                                    .setEnabled(true);

                            Toast.makeText(
                                    NotificationAdminActivity.this,
                                    "Unable to send notification: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }
}