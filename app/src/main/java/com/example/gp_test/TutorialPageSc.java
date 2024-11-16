package com.example.gp_test;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.gp_test.R.id.navHome;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

/** @noinspection deprecation*/
public class TutorialPageSc extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 2;

    private static final int REQUEST_CAMERA_PERMISSION =100 ;
    private Button toResult;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;

    private ImageView capturedImageView;
    private TextView resultTextView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tutorialpage_sc);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }




//      <!--  BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//
//// Set default fragment
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new homeFragmment()).commit();
//
//// Set up listener for navigation item selection
//        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//            Fragment selectedFragment = null;
//
//            switch (item.getItemId()) {
//                case R.id.navGallery:
//                    selectedFragment = new galleryFragment();
//                    break;
//                case R.id.navStats:
//                    selectedFragment = new statsFragment();
//                    break;
//                case R.id.navReminder:
//                    selectedFragment = new reminderFragment();
//                    break;
//                case R.id.navHome:
//                    selectedFragment = new homeFragmment();
//                    break;
//            }
//
//            // Replace the current fragment with the selected fragment
//            if (selectedFragment != null) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
//            }
//
//            return true;
//        });


        toResult = findViewById(R.id.tostats);
        toResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TutorialPageSc.this, TestResultSc.class);
                startActivity(i);
            }
        });
        capturedImageView = findViewById(R.id.capturedImageView);
        resultTextView = findViewById(R.id.resultTextView);

        findViewById(R.id.openCameraButton).setOnClickListener(v -> openCamera());
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openCamera() {
        { try{Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }

             catch(Exception e){
            Toast.makeText(this,e.getMessage(), LENGTH_SHORT).show();
            }
            }
        }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can open the camera now
                openCamera();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission is required to use this feature.", LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No camera hardware detected", LENGTH_SHORT).show();
        }
        }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                processImage(bitmap);
            }
        }
    }
    private void processImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(this::passDataToTestResult)
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void passDataToTestResult(Text result) {
        StringBuilder recognizedText = new StringBuilder();
        for (Text.TextBlock block : result.getTextBlocks()) {
            recognizedText.append(block.getText()).append("\n");
        }

        Intent intent = new Intent(TutorialPageSc.this, TestResultSc.class);
        intent.putExtra("recognizedText", recognizedText.toString());
        startActivity(intent);
    }


}


