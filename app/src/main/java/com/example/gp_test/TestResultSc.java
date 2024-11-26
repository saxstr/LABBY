package com.example.gp_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

/** @noinspection deprecation*/
public class TestResultSc extends AppCompatActivity {
    private Button toTables;

    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testresult_sc);

        // Retrieve grouped rows
        ArrayList<String> recognizedLines = getIntent().getStringArrayListExtra("recognizedLines");

        // Find the parent container
        LinearLayout dynamicContainer = findViewById(R.id.dynamicContainer);

        // Check if recognizedLines is empty
        if (recognizedLines == null || recognizedLines.isEmpty()) {
            TextView noResultsView = new TextView(this);
            noResultsView.setText("No text recognized.");
            noResultsView.setPadding(16, 16, 16, 16);
            noResultsView.setTextSize(18);
            noResultsView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            dynamicContainer.addView(noResultsView);
            return;
        }

        // Dynamically create a white block for each row
        for (String line : recognizedLines) {
            TextView textView = new TextView(this);
            textView.setText(line); // Set the text of the row
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(16);
            textView.setBackgroundColor(getResources().getColor(android.R.color.white)); // White block
            textView.setTextColor(getResources().getColor(android.R.color.black)); // Black text

            // Add margins between blocks
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(16, 16, 16, 16); // Add spacing between blocks
            textView.setLayoutParams(layoutParams);

            // Add the TextView to the dynamic container
            dynamicContainer.addView(textView);
        }
    }



}