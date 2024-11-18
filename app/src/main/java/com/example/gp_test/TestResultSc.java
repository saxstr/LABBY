//testresult

package com.example.gp_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TestResultSc extends AppCompatActivity {
    private Button toTables;
    private Button renameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testresult_sc); // Ensure this matches your XML layout name

        // Initialize buttons
        renameButton = findViewById(R.id.rename1);
        toTables = findViewById(R.id.tables);

        // Set up rename button functionality
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditingMode();
            }
        });

        // Set up navigation to the tables page
        toTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestResultSc.this, MedTablesSc.class);
                startActivity(intent);
            }
        });

        // Load saved test names
        loadTestNames();
    }

    // Toggle between view and edit modes for test names
    private void toggleEditingMode() {
        LinearLayout resultsContainer = findViewById(R.id.results_container);
        if (resultsContainer == null) {
            // Prevent crashes if the container is not found
            return;
        }

        for (int i = 0; i < resultsContainer.getChildCount(); i++) {
            View child = resultsContainer.getChildAt(i);

            if (child instanceof LinearLayout) {
                TextView testNameText = child.findViewById(R.id.test_name_text);
                EditText testNameEdit = child.findViewById(R.id.test_name_edit);

                if (testNameText != null && testNameEdit != null) {
                    if (testNameText.getVisibility() == View.VISIBLE) {
                        // Switch to editing mode
                        testNameText.setVisibility(View.GONE);
                        testNameEdit.setVisibility(View.VISIBLE);
                        testNameEdit.requestFocus();
                    } else {
                        // Save changes and switch back to view mode
                        String newName = testNameEdit.getText().toString().trim();
                        testNameText.setText(newName);
                        testNameEdit.setVisibility(View.GONE);
                        testNameText.setVisibility(View.VISIBLE);

                        // Save the new name locally
                        saveTestNameLocally(i, newName);
                    }
                }
            }
        }
    }

    // Save test name locally using SharedPreferences
    private void saveTestNameLocally(int index, String newName) {
        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("test_name_" + index, newName);
        editor.apply();
    }

    // Load test names from SharedPreferences
    private void loadTestNames() {
        LinearLayout resultsContainer = findViewById(R.id.results_container);
        if (resultsContainer == null) {
            return; // Prevent crashes if the container is not found
        }

        SharedPreferences preferences = getSharedPreferences("TestResults", MODE_PRIVATE);

        for (int i = 0; i < resultsContainer.getChildCount(); i++) {
            View child = resultsContainer.getChildAt(i);

            if (child instanceof LinearLayout) {
                TextView testNameText = child.findViewById(R.id.test_name_text);
                if (testNameText != null) {
                    String savedName = preferences.getString("test_name_" + i, null);
                    if (savedName != null) {
                        testNameText.setText(savedName);
                    }
                }
            }
        }
    }
}
