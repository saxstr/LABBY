package com.example.gp_test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddMedTables extends AppCompatActivity {

    private EditText medicationName;
    private EditText dosage;
    private EditText time;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_med_tables);

        medicationName = findViewById(R.id.medication_name);
        dosage = findViewById(R.id.dosage);
        time = findViewById(R.id.time);
        Button saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get input data
                String name = medicationName.getText().toString();
                String dosageValue = dosage.getText().toString();
                String timeValue = time.getText().toString();

                // Validation and feedback (example)
                if (name.isEmpty() || dosageValue.isEmpty() || timeValue.isEmpty()) {
                    Toast.makeText(AddMedTables.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Code to save the medication data
                    Toast.makeText(AddMedTables.this, "Medication saved!", Toast.LENGTH_SHORT).show();

                    // Return to the previous activity (optional)
                    finish();
                }
            }
        });
    }
}
