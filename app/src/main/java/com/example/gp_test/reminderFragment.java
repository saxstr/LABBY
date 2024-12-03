package com.example.gp_test;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class reminderFragment extends Fragment {

    private LinearLayout medicationContainer;
    private LinearLayout addForm;
    private EditText newMedicationName;
    private Button addTableButton;
    private Button addConfirmButton;
    private Button renameButton;

    private static final String PREFS_NAME = "MedicationPrefs";
    private static final String MEDICATION_KEY = "Medications";
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        // Initialize views
        medicationContainer = view.findViewById(R.id.recyclerViewMedTables);
        addForm = view.findViewById(R.id.add_form);
        newMedicationName = view.findViewById(R.id.new_medication_name);
        addTableButton = view.findViewById(R.id.add_table_button);
        addConfirmButton = view.findViewById(R.id.add_confirm_button);
        renameButton = view.findViewById(R.id.rename_button);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);

        // Show the form when "Add New Table" is clicked
        addTableButton.setOnClickListener(v -> addForm.setVisibility(View.VISIBLE));

        // Add a new table
        addConfirmButton.setOnClickListener(v -> {
            String tableName = newMedicationName.getText().toString().trim();
            if (!tableName.isEmpty()) {
                String uniqueReference = generateUniqueReference();
                addNewMedicationTable(tableName, uniqueReference);
                saveMedicationName(tableName + " | " + uniqueReference);
                newMedicationName.setText(""); // Clear input
                addForm.setVisibility(View.GONE); // Hide form
            }
        });

        // Rename functionality
        renameButton.setOnClickListener(v -> toggleEditingMode());

        // Load medication names
        loadMedicationNames();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadMedications();
    }

    private void addNewMedicationTable(String name, String reference) {
        // Create a new LinearLayout for the table
        LinearLayout newTableLayout = new LinearLayout(getContext());
        newTableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newTableLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTableLayout.setPadding(12, 12, 12, 12);
        newTableLayout.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Add margin for spacing
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) newTableLayout.getLayoutParams();
        layoutParams.setMargins(0, 8, 0, 8);
        newTableLayout.setLayoutParams(layoutParams);

        // Create FrameLayout for name and reference
        LinearLayout nameFrame = new LinearLayout(getContext());
        nameFrame.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        nameFrame.setOrientation(LinearLayout.VERTICAL);

        // Medication name TextView
        TextView medicationNameText = new TextView(getContext());
        medicationNameText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        medicationNameText.setText(name);
        medicationNameText.setTextSize(18);
        medicationNameText.setTextColor(getResources().getColor(R.color.navy));

        // Medication reference TextView
        TextView medicationReferenceText = new TextView(getContext());
        medicationReferenceText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        medicationReferenceText.setText("#" + reference);
        medicationReferenceText.setTextSize(14);
        medicationReferenceText.setTextColor(getResources().getColor(android.R.color.darker_gray));

        nameFrame.addView(medicationNameText);
        nameFrame.addView(medicationReferenceText);

        // Delete Button
        Button deleteButton = new Button(getContext());
        deleteButton.setText("Delete");
        deleteButton.setTextSize(12);
        deleteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        deleteButton.setTextColor(getResources().getColor(android.R.color.white));
        deleteButton.setPadding(8, 8, 8, 8);

        // Delete Button Click Listener
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(name + " | " + reference, newTableLayout));

        // Add components to the table layout
        newTableLayout.addView(nameFrame);
        newTableLayout.addView(deleteButton);

        // Click listener for the medication name
        newTableLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), pillsSc.class);
            intent.putExtra("table_reference", reference); // Pass the unique reference
            startActivity(intent);
        });

        // Add the new table layout to the container
        medicationContainer.addView(newTableLayout);
    }

    private String generateUniqueReference() {
        Random random = new Random();
        int uniqueNumber = 100000 + random.nextInt(900000);
        return String.valueOf(uniqueNumber);
    }

    private void saveMedicationName(String name) {
        Set<String> medications = sharedPreferences.getStringSet(MEDICATION_KEY, new HashSet<>());
        Set<String> updatedMedications = new HashSet<>(medications);
        updatedMedications.add(name);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(MEDICATION_KEY, updatedMedications);
        editor.apply();
    }

    private void showDeleteConfirmationDialog(String nameWithReference, LinearLayout tableLayout) {
        // Create an alert dialog to confirm deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to delete this table?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Remove the table from SharedPreferences
            removeMedicationTable(nameWithReference);
            // Remove the table layout from the UI
            medicationContainer.removeView(tableLayout);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void removeMedicationTable(String nameWithReference) {
        Set<String> medications = sharedPreferences.getStringSet(MEDICATION_KEY, new HashSet<>());
        if (medications != null) {
            Set<String> updatedMedications = new HashSet<>(medications);
            updatedMedications.remove(nameWithReference);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(MEDICATION_KEY, updatedMedications);
            editor.apply();
        }
    }

    private void toggleEditingMode() {
        for (int i = 0; i < medicationContainer.getChildCount(); i++) {
            View child = medicationContainer.getChildAt(i);

            if (child instanceof LinearLayout) {
                LinearLayout nameFrame = (LinearLayout) ((LinearLayout) child).getChildAt(0);
                Button deleteButton = (Button) ((LinearLayout) child).getChildAt(1);

                if (nameFrame.getChildCount() >= 2) {
                    TextView medicationNameText = (TextView) nameFrame.getChildAt(0);
                    TextView medicationReferenceText = (TextView) nameFrame.getChildAt(1);
                    EditText medicationNameEdit;

                    // Check if the EditText exists, otherwise create it dynamically
                    if (nameFrame.getChildCount() > 2 && nameFrame.getChildAt(2) instanceof EditText) {
                        medicationNameEdit = (EditText) nameFrame.getChildAt(2);
                    } else {
                        medicationNameEdit = new EditText(getContext());
                        medicationNameEdit.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        medicationNameEdit.setText(medicationNameText.getText().toString());
                        nameFrame.addView(medicationNameEdit); // Add dynamically
                    }

                    if (medicationNameText.getVisibility() == View.VISIBLE) {
                        // Switch to edit mode
                        medicationNameText.setVisibility(View.GONE);
                        medicationReferenceText.setVisibility(View.GONE);
                        medicationNameEdit.setVisibility(View.VISIBLE);
                        medicationNameEdit.requestFocus();
                        deleteButton.setVisibility(View.GONE); // Temporarily hide delete button for better UX
                    } else {
                        // Save the new name and switch to view mode
                        String newName = medicationNameEdit.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            medicationNameText.setText(newName);
                            saveMedicationNameLocally(i, newName); // Save new name to SharedPreferences
                        }
                        medicationNameEdit.setVisibility(View.GONE);
                        medicationNameText.setVisibility(View.VISIBLE);
                        medicationReferenceText.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE); // Restore delete button
                    }
                }
            }
        }
    }

    private void saveMedicationNameLocally(int index, String newName) {
        Set<String> medications = sharedPreferences.getStringSet(MEDICATION_KEY, new HashSet<>());
        if (medications == null || medications.isEmpty()) return;

        String[] medicationsArray = medications.toArray(new String[0]);

        if (index >= 0 && index < medicationsArray.length) {
            String[] parts = medicationsArray[index].split(" \\| ");
            if (parts.length > 1) {
                String reference = parts[1]; // Preserve the unique reference
                medicationsArray[index] = newName + " | " + reference;
            } else {
                medicationsArray[index] = newName;
            }

            Set<String> updatedMedications = new HashSet<>();
            for (String medication : medicationsArray) {
                updatedMedications.add(medication);
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(MEDICATION_KEY, updatedMedications);
            editor.apply();
        }
    }

    private void loadMedicationNames() {
        Set<String> medications = sharedPreferences.getStringSet(MEDICATION_KEY, new HashSet<>());
        for (String medication : medications) {
            String[] parts = medication.split(" \\| ");
            String name = parts[0];
            String reference = parts.length > 1 ? parts[1] : generateUniqueReference();
            addNewMedicationTable(name, reference);
        }
    }

    private void reloadMedications() {
        medicationContainer.removeAllViews();
        loadMedicationNames();
    }
}
