package com.example.gp_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddPillSc extends AppCompatActivity {

    private EditText medicationName;
    private EditText dosage;
    private EditText time;

    // Static list to hold medication data temporarily
    public static List<Medication> medicationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_pill_sc);

        // Retrieve the table reference
        String tableReference = getIntent().getStringExtra("table_reference");

        // Initialize views
        EditText medicationName = findViewById(R.id.medication_name);
        EditText dosage = findViewById(R.id.dosage);
        EditText time = findViewById(R.id.time);
        Button saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {
            // Get input data
            String name = medicationName.getText().toString();
            String dosageValue = dosage.getText().toString();
            String timeValue = time.getText().toString();

            // Validate input
            if (name.isEmpty() || dosageValue.isEmpty() || timeValue.isEmpty()) {
                Toast.makeText(AddPillSc.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Create pill data
                String pillData = "اسم: " + name + "\nجرعة: " + dosageValue + "\nوقت: " + timeValue;

                // Save and send back the pill data
                Intent resultIntent = new Intent();
                resultIntent.putExtra("pill_name", medicationName.getText().toString().trim());
                resultIntent.putExtra("pill_dosage", dosage.getText().toString().trim());
                resultIntent.putExtra("pill_time", time.getText().toString().trim());
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });
    }




    // Medication data class
    public static class Medication {
        public String name;
        public String dosage;
        public String time;

        public Medication(String name, String dosage, String time) {
            this.name = name;
            this.dosage = dosage;
            this.time = time;
        }
    }
}
