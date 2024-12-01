package com.example.gp_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DisplayListsSc extends AppCompatActivity implements SavedResultsAdapter.OnItemClickListener {

    private DatabaseReference firebaseDatabase;
    private ArrayList<HashMap<String, String>> savedResults;
    private SavedResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_lists_sc);

        RecyclerView recyclerView = findViewById(R.id.savedResultsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set vertical scrolling

        savedResults = new ArrayList<>();
        adapter = new SavedResultsAdapter(this, savedResults, this); // Pass 'this' as listener
        recyclerView.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference("NamedPages");
        fetchSavedResults();
    }

    private void fetchSavedResults() {
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedResults.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    HashMap<String, String> result = new HashMap<>();
                    result.put("name", childSnapshot.child("name").getValue(String.class));
                    result.put("code", childSnapshot.child("code").getValue(String.class));
                    result.put("ocrData", childSnapshot.child("ocrData").getValue(String.class)); // Retrieve ocrData
                    result.put("key", childSnapshot.getKey()); // Store the unique key
                    savedResults.add(result);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    @Override
    public void onItemClick(String selectedKey) {
        // Retrieve the specific item from Firebase
        firebaseDatabase.child(selectedKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String code = snapshot.child("code").getValue(String.class);
                    String ocrData = snapshot.child("ocrData").getValue(String.class);

                    // Debugging logs
                    Log.d("FirebaseRetrieve", "Name: " + name + ", Code: " + code + ", OCR Data: " + ocrData);

                    // Pass the data to TestResultSc
                    Intent intent = new Intent(DisplayListsSc.this, TestResultSc.class);
                    intent.putExtra("name", name);
                    intent.putExtra("code", code);
                    intent.putExtra("ocrData", ocrData);
                    startActivity(intent);
                } else {
                    Log.d("FirebaseRetrieve", "No data found for the selected key.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseRetrieve", "Error retrieving data: " + error.getMessage());
            }
        });
    }
}
