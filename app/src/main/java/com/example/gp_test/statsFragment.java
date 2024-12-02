package com.example.gp_test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class statsFragment extends Fragment {

    private DatabaseReference firebaseDatabase;
    private ArrayList<HashMap<String, String>> savedResults;
    private SavedResultsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.display_lists_sc, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.savedResultsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // Vertical scrolling

        // Initialize ArrayList and Adapter
        savedResults = new ArrayList<>();
        adapter = new SavedResultsAdapter(requireContext(), savedResults, null); // No listener required
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("NamedPages");

        // Fetch saved results
        fetchSavedResults();

        return view;
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
                    result.put("ocrData", childSnapshot.child("ocrData").getValue(String.class)); // OCR Data
                    result.put("key", childSnapshot.getKey()); // Store unique key
                    savedResults.add(result);
                }
                adapter.notifyDataSetChanged(); // Notify adapter about data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle Firebase query error (optional)
            }
        });
    }
}
