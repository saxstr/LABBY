package com.example.gp_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TestResultSc extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testresult_sc);

        // Get the OCR results from the Intent
        String recognizedText = getIntent().getStringExtra("recognizedText");

        // Find the parent container for dynamic blocks
        LinearLayout dynamicContainer = findViewById(R.id.dynamicContainer);

        // If recognizedText is null or empty, display a message
        if (recognizedText == null || recognizedText.isEmpty()) {
            addMessageBlock(dynamicContainer, "No text recognized.", android.R.color.holo_red_dark);
            return;
        }

        // Process and display each line of the recognized text
        String[] lines = recognizedText.split("\n");
        for (String line : lines) {
            addMessageBlock(dynamicContainer, line, android.R.color.black);
        }

        // Set up navigation to tables (if button is needed)
        Button toTables = findViewById(R.id.tables);
        toTables.setOnClickListener(v -> {
            Intent intent = new Intent(TestResultSc.this, MedTablesSc.class);
            startActivity(intent);
        });
    }

    /**
     * Adds a block of text to the dynamic container.
     *
     * @param container The parent container for text blocks.
     * @param text      The text to display.
     * @param textColor The color resource for the text.
     */
    private void addMessageBlock(LinearLayout container, String text, int textColor) {
        // Create a new TextView
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(16);
        textView.setBackgroundColor(getResources().getColor(android.R.color.white));
        textView.setTextColor(getResources().getColor(textColor));

        // Add layout parameters with spacing
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 8, 8, 8); // Add spacing between blocks
        textView.setLayoutParams(layoutParams);

        // Add the TextView to the container
        container.addView(textView);
    }
}
