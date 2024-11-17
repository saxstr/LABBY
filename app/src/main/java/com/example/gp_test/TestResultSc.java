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

public class TestResultSc extends AppCompatActivity {
    private Button toTables;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.testresult_sc);

        // Get the OCR results from the Intent
        String recognizedText = getIntent().getStringExtra("recognizedText");

        // Find the parent container for dynamic blocks
        LinearLayout dynamicContainer = findViewById(R.id.dynamicContainer);

        // Check if recognizedText is null or empty
        if (recognizedText == null || recognizedText.isEmpty()) {
            TextView noResultsView = new TextView(this);
            noResultsView.setText("No text recognized.");
            noResultsView.setPadding(16, 16, 16, 16);
            noResultsView.setTextSize(18);
            noResultsView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            dynamicContainer.addView(noResultsView);
            return;
        }

        // Split the recognized text into lines
        String[] lines = recognizedText.split("\n");

        // Dynamically create a block for each line
        for (String line : lines) {
            // Create a new LinearLayout for the block
            LinearLayout blockLayout = new LinearLayout(this);
            blockLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Set layout parameters for spacing
            LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            blockParams.setMargins(8, 8, 8, 8); // Add spacing between blocks
            blockLayout.setLayoutParams(blockParams);

            // Create a TextView for the line
            TextView textView = new TextView(this);
            textView.setText(line);
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(16);
            textView.setBackgroundColor(getResources().getColor(android.R.color.white));
            textView.setTextColor(getResources().getColor(android.R.color.black));

            // Add the TextView to the block layout
            blockLayout.addView(textView);

            // Add the block layout to the parent container
            dynamicContainer.addView(blockLayout);
        }

        // Navigation button logic (if needed)
        Button toTables = findViewById(R.id.tables);
        toTables.setOnClickListener(v -> {
            Intent intent = new Intent(TestResultSc.this, MedTablesSc.class);
            startActivity(intent);
        });
    }

}

