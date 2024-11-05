package com.example.gp_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class SplashSc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_sc);

        // Delay for 5 seconds before moving to LoginActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, LogInSc.class);
            startActivity(intent);
            finish();
        }, 5000); // 5000 milliseconds = 5 seconds

        FirebaseApp.initializeApp(this);

    }
}