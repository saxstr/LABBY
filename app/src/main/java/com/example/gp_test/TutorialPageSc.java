package com.example.gp_test;

import static com.example.gp_test.R.id.navHome;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TutorialPageSc extends AppCompatActivity {


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tutorialpage_sc);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

// Set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new homeFragmment()).commit();

// Set up listener for navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navGallery:
                    selectedFragment = new galleryFragment();
                    break;
                case R.id.navStats:
                    selectedFragment = new statsFragment();
                    break;
                case R.id.navReminder:
                    selectedFragment = new reminderFragment();
                    break;
                case R.id.navHome:
                    selectedFragment = new homeFragmment();
                    break;
            }

            // Replace the current fragment with the selected fragment
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true;
        });

    }

}