package com.example.gp_test;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class MedTablesSc extends AppCompatActivity {

    private LinearLayout medicationContainer;
    private EditText newMedicationName;
    private Button addConfirmButton;

    private static final String PREFS_NAME = "MedTablesPrefs";
    private static final String MEDICATIONS_KEY = "medications";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.med_tables_sc);

        // Initialize views
        medicationContainer = findViewById(R.id.recyclerViewMedTables);
        newMedicationName = findViewById(R.id.new_medication_name);
        addConfirmButton = findViewById(R.id.add_confirm_button);

        // Load saved Med Tables on launch
        loadMedications();

        // Add new Med Table when "إضافة" button is clicked
        addConfirmButton.setOnClickListener(v -> {
            String tableName = newMedicationName.getText().toString().trim();
            if (!tableName.isEmpty()) {
                addNewMedicationTable(tableName);
                saveMedication(tableName);
                newMedicationName.setText(""); // Clear input
            } else {
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewMedicationTable(String name) {
        // Create a vertical layout for the card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 16, 16, 16);
        card.setBackgroundResource(R.drawable.rounded_corners_background);

        // Set card layout params
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(16, 16, 16, 16); // Add spacing between cards
        cardParams.gravity = Gravity.END; // Align card to the right
        card.setLayoutParams(cardParams);
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // Ensure RTL

        // Create TextView for Med Table name
        TextView tableNameView = new TextView(this);
        tableNameView.setText(name);
        tableNameView.setTextSize(18);
        tableNameView.setTextColor(getResources().getColor(android.R.color.black));
        tableNameView.setGravity(Gravity.START);

        // Create TextView for the random code
        String code = "#" + generateRandomCode();
        TextView codeView = new TextView(this);
        codeView.setText(code);
        codeView.setTextSize(14);
        codeView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        codeView.setGravity(Gravity.START);

        // Create a "Delete" button
        TextView deleteButton = new TextView(this);
        deleteButton.setText("حذف");
        deleteButton.setTextSize(14);
        deleteButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        deleteButton.setPadding(8, 8, 8, 8);
        deleteButton.setBackgroundResource(R.drawable.delete_button_background);
        deleteButton.setOnClickListener(v -> {
            medicationContainer.removeView(card); // Remove the card from the container
            removeMedicationFromStorage(name); // Remove from storage
        });

        // Add all views to the card layout
        card.addView(tableNameView);
        card.addView(codeView);
        card.addView(deleteButton);

        // Add the card to the container
        medicationContainer.addView(card);
    }

    private void saveMedication(String name) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> medications = prefs.getStringSet(MEDICATIONS_KEY, new HashSet<>());
        medications.add(name); // Add the new medication
        prefs.edit().putStringSet(MEDICATIONS_KEY, medications).apply();
    }

    private void removeMedicationFromStorage(String name) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> medications = prefs.getStringSet(MEDICATIONS_KEY, new HashSet<>());
        medications.remove(name);
        prefs.edit().putStringSet(MEDICATIONS_KEY, medications).apply();
    }

    private void loadMedications() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> medications = prefs.getStringSet(MEDICATIONS_KEY, new HashSet<>());

        if (medications != null) {
            for (String medication : medications) {
                addNewMedicationTable(medication);
            }
        }
    }

    private String generateRandomCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
