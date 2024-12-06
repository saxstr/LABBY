package com.example.gp_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class reminderFragment extends Fragment {

    private LinearLayout medicationContainer;
    private LinearLayout addForm;
    private EditText newMedicationName;
    private Button addTableButton, addConfirmButton, renameButton;
    public static HashMap<String, List<Pill>> localPillsStorage = new HashMap<>();

    private DatabaseReference databaseReference;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("NamedPages");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize views
        medicationContainer = view.findViewById(R.id.recyclerViewMedTables);
        addForm = view.findViewById(R.id.add_form);
        newMedicationName = view.findViewById(R.id.new_medication_name);
        addTableButton = view.findViewById(R.id.add_table_button);
        addConfirmButton = view.findViewById(R.id.add_confirm_button);
        renameButton = view.findViewById(R.id.rename_button);

        // Show the form when "Add New Table" is clicked
        addTableButton.setOnClickListener(v -> addForm.setVisibility(View.VISIBLE));

        // Add a new table
        addConfirmButton.setOnClickListener(v -> {
            String tableName = newMedicationName.getText().toString().trim();
            if (!tableName.isEmpty()) {
                String uniqueReference = generateUniqueReference();
                addNewMedicationTable(tableName, uniqueReference);
                newMedicationName.setText(""); // Clear input
                addForm.setVisibility(View.GONE); // Hide form
            } else {
                Toast.makeText(getContext(), "الرجاء إدخال اسم القائمة", Toast.LENGTH_SHORT).show();
            }
        });

        // Rename functionality
        renameButton.setOnClickListener(v -> toggleEditingMode());

        return view;
    }

    private void addNewMedicationTable(String name, String reference) {
        String tableId = databaseReference.push().getKey();
        if (tableId != null) {
            // Save the table to Firebase
            HashMap<String, String> tableData = new HashMap<>();
            tableData.put("name", name);
            tableData.put("reference", reference);

            databaseReference.child(tableId).setValue(tableData)
                    .addOnSuccessListener(aVoid -> {
                        addNewMedicationTableToUI(tableId, name, reference);
                        Toast.makeText(getContext(), "تمت إضافة القائمة", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "فشل في الإضافة", Toast.LENGTH_SHORT).show());
        }
    }

    private void addNewMedicationTableToUI(String tableId, String name, String reference) {
        LinearLayout newTableLayout = new LinearLayout(getContext());
        newTableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newTableLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTableLayout.setPadding(12, 12, 12, 12);
        newTableLayout.setBackground(getResources().getDrawable(R.drawable.med_table_background, null));
        newTableLayout.setTag(tableId); // Store the table ID as the tag

        // Name container
        LinearLayout nameContainer = new LinearLayout(getContext());
        nameContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        nameContainer.setOrientation(LinearLayout.VERTICAL);

        TextView tableName = new TextView(getContext());
        tableName.setText(name);
        tableName.setTextSize(18);
        tableName.setTextColor(getResources().getColor(R.color.navy));

        TextView tableReference = new TextView(getContext());
        tableReference.setText("#" + reference);
        tableReference.setTextSize(14);
        tableReference.setTextColor(getResources().getColor(android.R.color.darker_gray));

        nameContainer.addView(tableName);
        nameContainer.addView(tableReference);

        // Delete Button
        Button deleteButton = new Button(getContext());
        deleteButton.setText("حذف");
        deleteButton.setTextSize(14);
        deleteButton.setTextColor(getResources().getColor(android.R.color.white));
        deleteButton.setBackground(getResources().getDrawable(R.drawable.delete_button_background, null));

        deleteButton.setOnClickListener(v -> {
            databaseReference.child(tableId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        medicationContainer.removeView(newTableLayout);
                        Toast.makeText(getContext(), "تم حذف القائمة", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "فشل في الحذف", Toast.LENGTH_SHORT).show());
        });

        newTableLayout.addView(nameContainer);
        newTableLayout.addView(deleteButton);

        newTableLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), pillsSc.class);
            intent.putExtra("table_reference", tableId);
            startActivity(intent);
        });

        medicationContainer.addView(newTableLayout);
    }

    private void toggleEditingMode() {
        for (int i = 0; i < medicationContainer.getChildCount(); i++) {
            View child = medicationContainer.getChildAt(i);

            if (child instanceof LinearLayout) {
                LinearLayout tableLayout = (LinearLayout) child; // Each table layout
                LinearLayout nameContainer = (LinearLayout) tableLayout.getChildAt(0); // Name container

                TextView tableName = (TextView) nameContainer.getChildAt(0); // Name TextView
                EditText tableEdit;

                if (nameContainer.getChildCount() > 2 && nameContainer.getChildAt(2) instanceof EditText) {
                    tableEdit = (EditText) nameContainer.getChildAt(2);
                } else {
                    tableEdit = new EditText(getContext());
                    tableEdit.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    tableEdit.setText(tableName.getText().toString());
                    nameContainer.addView(tableEdit);
                }

                if (tableName.getVisibility() == View.VISIBLE) {
                    tableName.setVisibility(View.GONE);
                    tableEdit.setVisibility(View.VISIBLE);
                    tableEdit.requestFocus();
                } else {
                    String newName = tableEdit.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        tableName.setText(newName);

                        databaseReference.child(tableLayout.getTag().toString()).child("name").setValue(newName)
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "فشل في تحديث الاسم", Toast.LENGTH_SHORT).show());
                    }
                    tableName.setVisibility(View.VISIBLE);
                    tableEdit.setVisibility(View.GONE);
                }
            }
        }
    }

    private String generateUniqueReference() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}
