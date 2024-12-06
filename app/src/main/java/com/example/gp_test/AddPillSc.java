package com.example.gp_test;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddPillSc extends AppCompatActivity {

    private EditText medicationName, dosage;
    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_pill_sc);

        medicationName = findViewById(R.id.medication_name);
        dosage = findViewById(R.id.dosage);
        timeTextView = findViewById(R.id.time);
        Button saveButton = findViewById(R.id.save_button);

        timeTextView.setOnClickListener(v -> showTimePicker());

        saveButton.setOnClickListener(v -> {
            String name = medicationName.getText().toString().trim();
            String dosageValue = dosage.getText().toString().trim();
            String timeValue = timeTextView.getText().toString().trim();

            if (name.isEmpty() || dosageValue.isEmpty() || timeValue.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("pill_name", name);
                resultIntent.putExtra("pill_dosage", dosageValue);
                resultIntent.putExtra("pill_time", timeValue);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                    timeTextView.setText(formattedTime);
                }, currentHour, currentMinute, true);
        timePickerDialog.show();
    }
}
