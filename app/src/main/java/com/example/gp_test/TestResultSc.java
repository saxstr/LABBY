package com.example.gp_test;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TestResultSc extends AppCompatActivity {
private Button contactButton;
    private DatabaseReference firebaseDatabase; // Firebase Database reference
    private String ocrContent; // Declare globally

    private LocalDatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testresult_sc);


        ArrayList<String> recognizedLines = getIntent().getStringArrayListExtra("recognizedLines");

        if (recognizedLines == null || recognizedLines.isEmpty()) {
            recognizedLines = new ArrayList<>();
            recognizedLines.add("No OCR content"); // Fallback
        }
        Log.d("OCRContentCheck", "OCR Content onCreate: " + ocrContent); // Log the value of ocrContent

        StringBuilder ocrContentBuilder = new StringBuilder();
        for (String line : recognizedLines) {
            ocrContentBuilder.append(line).append("\n");
        }
        String ocrContent = ocrContentBuilder.toString();

        Log.d("OCRContentCheck", "OCR Content received: " + ocrContent);

        // Save ocrContent for Firebase
        this.ocrContent = ocrContent; // Assuming ocrContent is a global variable in TestResultSc


        String name = getIntent().getStringExtra("name");
        String code = getIntent().getStringExtra("code");
        String ocrData = getIntent().getStringExtra("ocrData"); // Retrieve OCR data


        // Example: Display the name and code as part of the test results
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText("تحليل: " + name + " (" + code + ")");

        firebaseDatabase = FirebaseDatabase.getInstance().getReference("NamedPages");

        databaseHelper = new LocalDatabaseHelper(this);

        Button toSavedResultsButton = findViewById(R.id.toSavedResultsButton);
        toSavedResultsButton.setOnClickListener(v -> {
            Intent intent = new Intent(TestResultSc.this, DisplayListsSc.class);
            startActivity(intent);
        });

        TextView saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> showNameInputDialog());

        // Initialize the "Call 911" button
        Button contactButton = findViewById(R.id.contactButton);
        contactButton.setOnClickListener(v -> callEmergencyLine("937"));

        // Retrieve grouped rows (already translated)
        LinearLayout dynamicContainer = findViewById(R.id.dynamicContainer);

        // Ensure RTL layout for the parent container
        dynamicContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        dynamicContainer.setGravity(Gravity.END);

        // Check if recognizedLines is empty
        if (recognizedLines == null || recognizedLines.isEmpty()) {
            TextView noResultsView = new TextView(this);
            noResultsView.setText("لم يتم التعرف على أي نص.");
            noResultsView.setPadding(16, 16, 16, 16);
            noResultsView.setTextSize(18);
            noResultsView.setTextColor(Color.RED);
            noResultsView.setGravity(Gravity.CENTER); // Center for the empty message
            dynamicContainer.addView(noResultsView);
            return;
        }

        // Display each result in a styled card
        for (String line : recognizedLines) {
            // Create a vertical layout for the card
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 16, 16, 16);
            card.setBackgroundResource(R.drawable.rounded_corners_background);

            // Set card layout params
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(16, 16, 16, 16); // Add spacing between cards
            cardParams.gravity = Gravity.END; // Align card to the right
            card.setLayoutParams(cardParams);
            card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // Ensure RTL

            // Parse line into components
            String[] parts = line.split("\n");
            String testName = parts.length > 0 ? parts[0] : "غير معروف";
            String result = parts.length > 1 ? parts[1] : "غير معروف";
            String status = parts.length > 2 ? parts[2] : "غير معروف";
            String range = parts.length > 3 ? parts[3] : "غير معروف";

            // Test Name
            TextView testNameView = new TextView(this);
            testNameView.setText(testName);
            testNameView.setTextSize(18);
            testNameView.setTextColor(Color.BLACK);
            testNameView.setTextDirection(View.TEXT_DIRECTION_RTL); // Ensure RTL text
            card.addView(testNameView);

            // Result
            TextView resultView = new TextView(this);
            resultView.setText(result);
            resultView.setTextSize(16);
            resultView.setTextColor(Color.DKGRAY);
            resultView.setTextDirection(View.TEXT_DIRECTION_RTL); // Ensure RTL text
            card.addView(resultView);

            // Create a horizontal row for status (label + value)
            LinearLayout statusRow = new LinearLayout(this);
            statusRow.setOrientation(LinearLayout.HORIZONTAL);
            statusRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // Ensure RTL for Arabic

            // Add status label
            TextView statusLabelView = new TextView(this);
            statusLabelView.setText("الحالة:");
            statusLabelView.setTextSize(16);
            statusLabelView.setTextColor(Color.BLACK); // Always black for the label
            statusLabelView.setTextDirection(View.TEXT_DIRECTION_RTL);
            statusRow.addView(statusLabelView);

            // Add status value
            TextView statusValueView = new TextView(this);
            if (status.startsWith("الحالة: ")) {
                status = status.replace("الحالة: ", "").trim();
            }
            statusValueView.setText(status);
            statusValueView.setTextSize(16);

            // Set status color dynamically
            switch (status) {
                case "طبيعي": // Normal
                    statusValueView.setTextColor(Color.parseColor("#388E3C")); // Green
                    break;
                case "زائد": // High
                    statusValueView.setTextColor(Color.parseColor("#FBC02D")); // Red
                    break;
                case "ناقص": // Low
                    statusValueView.setTextColor(Color.parseColor("#D32F2F")); // Yellow/Orange
                    break;
                default:
                    statusValueView.setTextColor(Color.GRAY); // Default gray
                    break;
            }
            statusRow.addView(statusValueView);

            // Add statusRow to the card
            card.addView(statusRow);

            // Range
            TextView rangeView = new TextView(this);
            rangeView.setText(range);
            rangeView.setTextSize(14);
            rangeView.setTextColor(Color.DKGRAY);
            rangeView.setTextDirection(View.TEXT_DIRECTION_RTL); // Ensure RTL text
            card.addView(rangeView);

            // Add card to container
            dynamicContainer.addView(card);
        }
    }

    private void callEmergencyLine(String emergencyNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL); // Open the dialer
        callIntent.setData(Uri.parse("tel:" + emergencyNumber)); // Set the phone number
        startActivity(callIntent);
    }
    private void showNameInputDialog() {
        // Create an alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إدخال الاسم");

        // Create an EditText for user input
        final EditText input = new EditText(this);
        input.setHint("أدخل الاسم");
        input.setGravity(Gravity.RIGHT);
        builder.setView(input);

        // Add "Save" button
        builder.setPositiveButton("حفظ", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                saveToFirebase(name, ocrContent);
            } else {
                Toast.makeText(this, "الرجاء إدخال اسم", Toast.LENGTH_SHORT).show();
            }
        });

        // Add "Cancel" button
        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
    private void saveToFirebase(String name, String ocrContent) {
        String code = generateRandomCode();

        // Create a new entry
        String key = firebaseDatabase.push().getKey(); // Generate a unique key for this result
        if (key != null) {
            // Prepare the data to save
            HashMap<String, String> data = new HashMap<>();
            data.put("name", name); // The name provided by the user
            data.put("code", code); // The random 6-digit code
            data.put("ocrData", ocrContent); // The OCR content to be saved


            // Save the entry to Firebase
            firebaseDatabase.child(key).setValue(data)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "تم حفظ الاسم بنجاح", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "حدث خطأ أثناء الحفظ", Toast.LENGTH_SHORT).show());
        }
    }


    private String generateRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}

