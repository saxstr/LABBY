package com.example.gp_test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

/** @noinspection deprecation*/
public class MedTablesSc extends AppCompatActivity {

    private LinearLayout medicationContainer;
    private LinearLayout addForm;
    private EditText newMedicationName;
    private Button addTableButton;
    private Button addConfirmButton;

    private static final String PREFS_NAME = "MedTablesPrefs";
    private static final String MEDICATIONS_KEY = "medications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.med_tables_sc);

        // Initialize views
        medicationContainer = findViewById(R.id.medication_container);
        addForm = findViewById(R.id.add_form);
        newMedicationName = findViewById(R.id.new_medication_name);
        addTableButton = findViewById(R.id.add_table_button);
        addConfirmButton = findViewById(R.id.add_confirm_button);

        // Show the form when "تسجيل قائمة جديدة" is clicked
        addTableButton.setOnClickListener(v -> addForm.setVisibility(View.VISIBLE));

        // Add new table when "إضافة" is clicked
        addConfirmButton.setOnClickListener(v -> {
            String tableName = newMedicationName.getText().toString().trim();
            if (!tableName.isEmpty()) {
                addNewMedicationTable(tableName);
                saveMedication(tableName); // Save the medication to SharedPreferences
                newMedicationName.setText(""); // Clear input
                addForm.setVisibility(View.GONE); // Hide form
            } else {
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            }
        });

        // Load saved medications on launch
        loadMedications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedications(); // Reload medications whenever the user comes back to this screen
    }

    private void addNewMedicationTable(String name) {
        // Create a new TextView for the medication name
        TextView medicationNameView = new TextView(this);
        medicationNameView.setText(name);
        medicationNameView.setTextSize(18);
        medicationNameView.setPadding(16, 16, 16, 16);
        medicationNameView.setBackground(getResources().getDrawable(android.R.color.white));
        medicationNameView.setTextColor(getResources().getColor(android.R.color.black));

        // Add the TextView to the container
        medicationContainer.addView(medicationNameView);
    }

    private void saveMedication(String name) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> medications = prefs.getStringSet(MEDICATIONS_KEY, new HashSet<>());
        medications.add(name); // Add the new medication
        prefs.edit().putStringSet(MEDICATIONS_KEY, medications).apply();
    }

    private void loadMedications() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> medications = prefs.getStringSet(MEDICATIONS_KEY, new HashSet<>());

        // Clear the container to avoid duplicate entries
        medicationContainer.removeAllViews();

        // Add each medication to the container
        for (String medication : medications) {
            addNewMedicationTable(medication);
        }
    }
}
