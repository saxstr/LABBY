package com.example.gp_test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/** @noinspection deprecation*/
public class galleryFragment extends Fragment {

    private ImageView capturedImageView;
    private Uri photoUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorialpage_sc, container, false);

        capturedImageView = view.findViewById(R.id.capturedImageView);
        Button openCameraButton = view.findViewById(R.id.openCameraButton);

        // Set button click listener
        openCameraButton.setOnClickListener(v -> openCamera());

        return view;
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = getOutputUri();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // Save full-size image
            startActivityForResult(takePictureIntent, 1);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    private Uri getOutputUri() {
        File photoFile = new File(requireContext().getExternalFilesDir(null), "photo.jpg");
        return FileProvider.getUriForFile(requireContext(), "com.example.gp_test.fileprovider", photoFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to use this feature.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 1) {
                if (photoUri != null) {
                    logImageInfo(photoUri, "Before cropping");

                    // Start UCrop for cropping
                    cropImage(photoUri);
                } else {
                    Toast.makeText(requireContext(), "Image capture failed", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP && data != null) {
                try {
                    Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        logImageInfo(resultUri, "After cropping");

                        Bitmap croppedBitmap = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(resultUri));

                        // Pass cropped image for OCR processing
                        processImageWithOCR(croppedBitmap);
                    }
                } catch (IOException e) {
                    Toast.makeText(requireContext(), "Error retrieving cropped image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.RESULT_ERROR) {
                Throwable cropError = UCrop.getError(data);
                Toast.makeText(requireContext(), "Crop error: " + (cropError != null ? cropError.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cropImage(Uri sourceUri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(sourceUri), null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;

        // Use original dimensions as max result size
        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "croppedImage.png"));

        UCrop.Options uCropOptions = new UCrop.Options();
        uCropOptions.setCompressionFormat(Bitmap.CompressFormat.PNG);
        uCropOptions.setCompressionQuality(100); // No compression
        uCropOptions.setFreeStyleCropEnabled(true);

        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(originalWidth, originalHeight)
                .withOptions(uCropOptions)
                .start(requireContext(), this);
    }

    private void processImageWithOCR(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    StringBuilder recognizedText = new StringBuilder();
                    for (Text.TextBlock block : result.getTextBlocks()) {
                        recognizedText.append(block.getText()).append("\n");
                    }

                    // Pass recognized text to TestResultSc activity
                    Intent intent = new Intent(requireContext(), TestResultSc.class);
                    intent.putExtra("recognizedText", recognizedText.toString());
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "OCR Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void logImageInfo(Uri imageUri, String label) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(imageUri), null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            long fileSize = new File(imageUri.getPath()).length() / 1024; // File size in KB
            Log.d("ImageInfo", label + " - Width: " + width + ", Height: " + height + ", FileSize: " + fileSize + " KB");
        } catch (Exception e) {
            Log.e("ImageInfo", "Error retrieving image info: " + e.getMessage());
        }
    }
}
