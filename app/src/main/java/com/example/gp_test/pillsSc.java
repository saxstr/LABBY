package com.example.gp_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** @noinspection deprecation */
public class pillsSc extends AppCompatActivity {
    private static final HashMap<String, List<AddPillSc.Medication>> pillsData = new HashMap<>();

    private PillAdapter adapter; // Declare globally
    private List<AddPillSc.Medication> pills; // Declare pills list globally
    private RecyclerView recyclerView;
    private String tableReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pills_sc);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPills);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
// Retrieve the table reference from the Intent
        Intent intent = getIntent();
        tableReference = intent.getStringExtra("table_reference");

// Validate the tableReference
        if (tableReference == null) {
            tableReference = "default_table"; // Fallback to a default value
        }

        // Initialize pills list
        pills = pillsData.getOrDefault(tableReference, new ArrayList<>());

        // Initialize adapter
        adapter = new PillAdapter(pills, position -> {
            // Remove the pill from the list
            pills.remove(position);

            // Notify the adapter about the removed item
            adapter.notifyItemRemoved(position);
        });

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);

        // Handle the "Add Medication" button
        Button addMedicationButton = findViewById(R.id.addmed);
        addMedicationButton.setOnClickListener(v -> {
            Intent addPillsIntent = new Intent(pillsSc.this, AddPillSc.class);
            startActivityForResult(addPillsIntent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Retrieve the pill data from the Intent
            String pillName = data.getStringExtra("pill_name");
            String pillDosage = data.getStringExtra("pill_dosage");
            String pillTime = data.getStringExtra("pill_time");

            if (pillName != null && pillDosage != null && pillTime != null) {
                // Create a Medication object and add it to the pills list
                AddPillSc.Medication pill = new AddPillSc.Medication(pillName, pillDosage, pillTime);
                pills.add(pill); // Add to the list
                adapter.notifyItemInserted(pills.size() - 1); // Notify adapter of new item
            }
        }
    }
}
