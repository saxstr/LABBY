package com.example.gp_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class pillsSc extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PillAdapter adapter;
    private List<Pill> pills;
    private String tableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pills_sc);

        // Get tableId from intent
        tableId = getIntent().getStringExtra("table_id");
        pills = reminderFragment.localPillsStorage.getOrDefault(tableId, new ArrayList<>()); // Updated reference

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPills);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PillAdapter(pills, position -> {
            pills.remove(position);
            adapter.notifyItemRemoved(position);
        });
        recyclerView.setAdapter(adapter);

        // Add new pill
        Button addPillButton = findViewById(R.id.addmed);
        addPillButton.setOnClickListener(v -> {
            Intent intent = new Intent(pillsSc.this, AddPillSc.class);
            intent.putExtra("table_id", tableId);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String pillName = data.getStringExtra("pill_name");
            String pillDosage = data.getStringExtra("pill_dosage");
            String pillTime = data.getStringExtra("pill_time");

            Pill newPill = new Pill(pillName, pillDosage, pillTime);
            pills.add(newPill);

            // Update local storage
            reminderFragment.localPillsStorage.put(tableId, pills); // Updated reference

            // Notify adapter
            adapter.notifyDataSetChanged();
        }
    }
}
