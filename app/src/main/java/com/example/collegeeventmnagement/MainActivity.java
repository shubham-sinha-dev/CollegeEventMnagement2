package com.example.collegeeventmnagement ;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "LoginData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences =
                getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        boolean isLoggedIn =
                preferences.getBoolean("isLoggedIn", false);

        // Agar already login hai
        if (isLoggedIn) {

            Intent intent = new Intent(
                    MainActivity.this,
                    HomeActivity.class
            );

            startActivity(intent);
            finish();

            return;
        }

        setContentView(R.layout.activity_main);
    }

    // LOGIN BUTTON
    public void openLogin(View view) {

        Intent intent = new Intent(
                MainActivity.this,
                MainActivity2.class
        );

        startActivity(intent);
    }

    // REGISTER BUTTON
    public void openRegister(View view) {

        Intent intent = new Intent(
                MainActivity.this,
                RegisterActivity.class
        );

        startActivity(intent);
    }
}