package com.example.gp_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** @noinspection deprecation */
public class pillsSc extends AppCompatActivity {
        private Button too;
    private LinearLayout medicationContainer;
    private String tableReference;
    private static final HashMap<String, List<AddMedTables.Medication>> pillsData = new HashMap<>(); // Temporary storage for pills mapped by tableReference

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pills_sc);
        too = findViewById(R.id.too);
        too.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(pillsSc.this, CodeCheckSc.class);
                startActivity(i);
            }
        });
        // Retrieve the table reference from the Intent
        Intent intent = getIntent();
        tableReference = intent.getStringExtra("table_reference"); // Get the unique reference of the table

        // Initialize the medication container
        medicationContainer = findViewById(R.id.medication_container);

        // Initialize the "Add Medication" button
        Button addMedicationButton = findViewById(R.id.addmed);
        addMedicationButton.setOnClickListener(v -> {
            Intent addPillsIntent = new Intent(pillsSc.this, AddMedTables.class);
            addPillsIntent.putExtra("table_reference", tableReference); // Pass the table reference
            startActivityForResult(addPillsIntent, 1); // Use startActivityForResult to get data back
        });

        // Load pills for the selected table
        loadPills();
    }

    private void loadPills() {
        // Get the pills for the current table reference
        List<AddMedTables.Medication> pills = pillsData.getOrDefault(tableReference, new ArrayList<>());

        // Clear the existing container
        medicationContainer.removeAllViews();

        // Display pills dynamically
        for (int i = 0; i < pills.size(); i++) {
            AddMedTables.Medication pill = pills.get(i);

            // Create a vertical LinearLayout for the pill entry
            LinearLayout pillEntryLayout = new LinearLayout(this);
            pillEntryLayout.setOrientation(LinearLayout.VERTICAL);
            pillEntryLayout.setPadding(16, 16, 16, 16);
            pillEntryLayout.setBackground(getResources().getDrawable(android.R.color.white));
            pillEntryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Add TextView for اسم
            TextView nameTextView = new TextView(this);
            nameTextView.setText("اسم: " + pill.name);
            nameTextView.setTextSize(16);
            nameTextView.setTextColor(getResources().getColor(R.color.navy));
            pillEntryLayout.addView(nameTextView);

            // Add TextView for جرعة
            TextView dosageTextView = new TextView(this);
            dosageTextView.setText("جرعة: " + pill.dosage);
            dosageTextView.setTextSize(14);
            dosageTextView.setTextColor(getResources().getColor(android.R.color.black));
            pillEntryLayout.addView(dosageTextView);

            // Add TextView for وقت
            TextView timeTextView = new TextView(this);
            timeTextView.setText("وقت: " + pill.time);
            timeTextView.setTextSize(14);
            timeTextView.setTextColor(getResources().getColor(android.R.color.black));
            pillEntryLayout.addView(timeTextView);

            // Add a horizontal layout for the delete button
            LinearLayout actionLayout = new LinearLayout(this);
            actionLayout.setOrientation(LinearLayout.HORIZONTAL);
            actionLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Add spacing between the details and the button
            actionLayout.setPadding(0, 8, 0, 0);

            // Add a small Delete Button
            Button deleteButton = new Button(this);
            deleteButton.setText("حذف");
            deleteButton.setTextSize(12);
            deleteButton.setTextColor(getResources().getColor(android.R.color.white));
            deleteButton.setBackgroundColor(getResources().getColor(R.color.red));
            deleteButton.setPadding(16, 8, 16, 8);
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            deleteParams.setMargins(8, 0, 0, 0); // Add a margin for better spacing
            deleteButton.setLayoutParams(deleteParams);

            // On click, delete the pill
            deleteButton.setOnClickListener(v -> {
                pills.remove(pill); // Remove the selected pill
                loadPills(); // Refresh the pills list
            });

            // Add the button to the action layout
            actionLayout.addView(deleteButton);

            // Add the action layout to the pill entry layout
            pillEntryLayout.addView(actionLayout);

            // Add a margin between pill entries
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) pillEntryLayout.getLayoutParams();
            params.setMargins(0, 8, 0, 8); // Top and bottom margins
            pillEntryLayout.setLayoutParams(params);

            // Add the dynamic entry to the container
            medicationContainer.addView(pillEntryLayout);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Refresh pills dynamically when returning to this screen
        loadPills();
    }

    // Call this method to add pills for the selected table
    public static void addPill(String tableReference, AddMedTables.Medication pill) {
        // Get or create the pill list for the given table reference
        List<AddMedTables.Medication> pills = pillsData.getOrDefault(tableReference, new ArrayList<>());
        pills.add(pill);
        pillsData.put(tableReference, pills); // Save the updated list back to the map
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Retrieve the pill data from the Intent
            String pillName = data.getStringExtra("pill_name");
            String pillDosage = data.getStringExtra("pill_dosage");
            String pillTime = data.getStringExtra("pill_time");

            // Validate that data is not null
            if (pillName != null && pillDosage != null && pillTime != null) {
                // Create a Medication object and add it to the selected table's pills
                AddMedTables.Medication pill = new AddMedTables.Medication(pillName, pillDosage, pillTime);
                pillsSc.addPill(tableReference, pill);

                // Refresh the display
                loadPills();
            } else {
                // Log an error if data is missing
                android.util.Log.e("pillsSc", "Missing data: " +
                        "pill_name=" + pillName + ", pill_dosage=" + pillDosage + ", pill_time=" + pillTime);
            }
        }
    }
}
