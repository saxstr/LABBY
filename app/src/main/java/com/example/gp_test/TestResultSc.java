package com.example.gp_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TestResultSc extends AppCompatActivity {
    private Button toTables;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.testresult_sc);
        TextView textViewRecognized = findViewById(R.id.displayOcr);

        String recognizedText = getIntent().getStringExtra("recognizedText");
        textViewRecognized.setText(recognizedText);


        toTables = (Button) findViewById(R.id.tables);
        toTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(TestResultSc.this, MedTablesSc.class);
                startActivity(s);
            }



        });
    }
}