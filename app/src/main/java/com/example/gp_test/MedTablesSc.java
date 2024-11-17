package com.example.gp_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MedTablesSc extends AppCompatActivity {
    private LinearLayout medicationContainer;
    private LinearLayout addForm;
    private EditText newMedicationName;
    private Button addTableButton;
    private Button addConfirmButton;
    private Button renameButton; // Button for renaming


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
        renameButton = findViewById(R.id.rename_button);
        Button toPills = findViewById(R.id.topills); // PILLS button initialization

        // Show the form when "تسجيل قائمة جديدة" is clicked
        addTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addForm.setVisibility(View.VISIBLE);
            }
        });

        // Add new table when "إضافة" is clicked
        addConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = newMedicationName.getText().toString().trim();
                if (!tableName.isEmpty()) {
                    addNewMedicationTable(tableName, "#000000");
                    newMedicationName.setText(""); // Clear input
                    addForm.setVisibility(View.GONE); // Hide form
                }
            }
        });

        // تسمية button functionality
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditingMode(); // Call the method to toggle edit mode
            }
        });

        // PILLS button functionality
        toPills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedTablesSc.this, pillsSc.class);
                startActivity(intent);
            }
        });

        // Load saved medication names
        loadMedicationNames();
    }



    // Add a new medication table dynamically
    private void addNewMedicationTable(String name, String code) {
        // Create a new LinearLayout for the table
        LinearLayout newTableLayout = new LinearLayout(this);
        newTableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newTableLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTableLayout.setPadding(12, 12, 12, 12);
        newTableLayout.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Create FrameLayout for name
        LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout nameFrame = new LinearLayout(this);
        nameFrame.setLayoutParams(frameParams);
        nameFrame.setOrientation(LinearLayout.VERTICAL);

        // TextView for medication name
        TextView medicationNameText = new TextView(this);
        medicationNameText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        medicationNameText.setText(name);
        medicationNameText.setTextSize(18);
        medicationNameText.setTextColor(getResources().getColor(R.color.navy));
        medicationNameText.setVisibility(View.VISIBLE);

        // Assign a unique ID
        medicationNameText.setId(View.generateViewId());

        // EditText for renaming
        EditText medicationNameEdit = new EditText(this);
        medicationNameEdit.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        medicationNameEdit.setText(name);
        medicationNameEdit.setTextSize(18);
        medicationNameEdit.setTextColor(getResources().getColor(R.color.navy));
        medicationNameEdit.setVisibility(View.GONE);

        // Assign a unique ID
        medicationNameEdit.setId(View.generateViewId());

        // Add TextView and EditText to FrameLayout
        nameFrame.addView(medicationNameText);
        nameFrame.addView(medicationNameEdit);

        // TextView for medication code
        TextView medicationCodeText = new TextView(this);
        medicationCodeText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        medicationCodeText.setText(code);
        medicationCodeText.setTextSize(14);

        // Add views to new table layout
        newTableLayout.addView(nameFrame);
        newTableLayout.addView(medicationCodeText);

        // Add new table layout to the container
        medicationContainer.addView(newTableLayout);
    }


    // Toggle between view and edit modes for medication names
    private void toggleEditingMode() {
        for (int i = 0; i < medicationContainer.getChildCount(); i++) {
            View child = medicationContainer.getChildAt(i);

            if (child instanceof LinearLayout) {
                // Retrieve TextView and EditText dynamically
                LinearLayout nameFrame = (LinearLayout) ((LinearLayout) child).getChildAt(0);
                TextView medicationNameText = (TextView) nameFrame.getChildAt(0);
                EditText medicationNameEdit = (EditText) nameFrame.getChildAt(1);

                if (medicationNameText != null && medicationNameEdit != null) {
                    if (medicationNameText.getVisibility() == View.VISIBLE) {
                        medicationNameText.setVisibility(View.GONE);
                        medicationNameEdit.setVisibility(View.VISIBLE);
                        medicationNameEdit.requestFocus();
                    } else {
                        String newName = medicationNameEdit.getText().toString().trim();
                        medicationNameText.setText(newName);
                        medicationNameEdit.setVisibility(View.GONE);
                        medicationNameText.setVisibility(View.VISIBLE);

                        saveMedicationNameLocally(i, newName);
                    }
                }
            }
        }
    }


    // Save medication name locally
    private void saveMedicationNameLocally(int index, String newName) {
        SharedPreferences preferences = getSharedPreferences("Medications", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("medication_name_" + index, newName);
        editor.apply();
    }

    // Load medication names from storage
    private void loadMedicationNames() {
        SharedPreferences preferences = getSharedPreferences("Medications", MODE_PRIVATE);

        for (int i = 0; i < medicationContainer.getChildCount(); i++) {
            View child = medicationContainer.getChildAt(i);

            if (child instanceof LinearLayout) {
                TextView medicationNameText = child.findViewById(R.id.add_table_button);
                if (medicationNameText != null) {
                    String savedName = preferences.getString("medication_name_" + i, null);
                    if (savedName != null) {
                        medicationNameText.setText(savedName);
                    }
                }
            }
        }
    }
}
