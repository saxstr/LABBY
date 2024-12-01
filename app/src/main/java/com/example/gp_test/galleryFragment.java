package com.example.gp_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @noinspection deprecation*/
public class galleryFragment extends Fragment {
    private static final int REQUEST_GALLERY_IMAGE = 1001;
    private static final int CAMERA_REQUEST_CODE = 1;

    private ImageView capturedImageView;
    private Uri photoUri;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorialpage_sc, container, false);

        try {
            capturedImageView = view.findViewById(R.id.capturedImageView);
            Button openCameraButton = view.findViewById(R.id.openCameraButton);
            Button openGalleryButton = view.findViewById(R.id.openGalleryButton);

            openCameraButton.setOnClickListener(v -> openCamera());
            openGalleryButton.setOnClickListener(v -> openGallery());

            requestStoragePermission();
        } catch (Exception e) {
            Log.e("GalleryFragment", "Error in onCreateView: " + e.getMessage());
        }

        return view;
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = getOutputUri();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }
    }

    private Uri getOutputUri() {
        File photoFile = new File(requireContext().getExternalFilesDir(null), "photo.jpg");
        return FileProvider.getUriForFile(requireContext(), "com.example.gp_test.fileprovider", photoFile);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE && photoUri != null) {
                cropImage(photoUri); // Handle camera image and pass to UCrop
            } else if (requestCode == REQUEST_GALLERY_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    cropImage(selectedImageUri); // Handle gallery image and pass to UCrop
                }
            } else if (requestCode == UCrop.REQUEST_CROP && data != null) {
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    try {
                        // Load cropped image as Bitmap
                        Bitmap originalBitmap = BitmapFactory.decodeStream(
                                requireContext().getContentResolver().openInputStream(resultUri));

                        // Preprocess the image
                        Bitmap processedBitmap = preprocessImage(originalBitmap);

                        // Pass the preprocessed image for OCR
                        processImageWithOCR(processedBitmap);
                    } catch (IOException e) {
                        Toast.makeText(requireContext(), "Error loading cropped image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == UCrop.RESULT_ERROR) {
                Throwable cropError = UCrop.getError(data);
                Toast.makeText(requireContext(), "Crop error: " + (cropError != null ? cropError.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "croppedImage.png"));

        UCrop.Options uCropOptions = new UCrop.Options();
        uCropOptions.setCompressionFormat(Bitmap.CompressFormat.PNG);
        uCropOptions.setCompressionQuality(100);
        uCropOptions.setFreeStyleCropEnabled(true);

        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(1080, 1080)
                .withOptions(uCropOptions)
                .start(requireContext(), this);
    }
    private Bitmap preprocessImage(Bitmap bitmap) {
        // Resize the bitmap to improve OCR accuracy
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight() * 2, false);

        // Convert to grayscale to simplify text recognition
        Bitmap grayBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int pixel = bitmap.getPixel(x, y);
                int avg = (int) ((0.3 * ((pixel >> 16) & 0xFF)) + (0.59 * ((pixel >> 8) & 0xFF)) + (0.11 * (pixel & 0xFF)));
                grayBitmap.setPixel(x, y, (0xFF << 24) | (avg << 16) | (avg << 8) | avg);
            }
        }

        return grayBitmap;
    }

    private void processImageWithOCR(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(this::handleOCRResults)
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "OCR Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void handleOCRResults(Text result) {
        List<String> groupedLines = new ArrayList<>();
        HashMap<String, String> translations = getTranslationMap();

        List<Text.Element> elements = new ArrayList<>();
        for (Text.TextBlock block : result.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                elements.addAll(line.getElements());
            }
        }

        while (!elements.isEmpty()) {
            Text.Element firstElement = elements.remove(0);
            Rect firstBoundingBox = firstElement.getBoundingBox();

            if (firstBoundingBox != null) {
                StringBuilder currentRow = new StringBuilder(firstElement.getText());
                Iterator<Text.Element> iterator = elements.iterator();

                while (iterator.hasNext()) {
                    Text.Element element = iterator.next();
                    Rect boundingBox = element.getBoundingBox();

                    if (boundingBox != null && isSameRowWithHorizontalAllowance(firstBoundingBox, boundingBox)) {
                        currentRow.append(" ").append(element.getText());
                        iterator.remove();
                    }
                }

                Log.d("RawLine", "Detected Line: " + currentRow.toString());

                // Translate and process the row
                String translatedRow = translateLine(currentRow.toString(), translations);
                Log.d("TranslatedRow", "Translated Row: " + translatedRow);

                String filteredRow = cleanRow(translatedRow);
                String resultValue = extractResult(filteredRow);
                String referenceRange = extractReferenceRange(filteredRow);
                String unit = extractUnit(filteredRow); // Extract the unit

                if (resultValue != null && referenceRange != null) {
                    String testName = filteredRow
                            .replace(resultValue, "")
                            .replace(referenceRange, "")
                            .replace(unit != null ? unit : "", "") // Remove the unit if found
                            .trim();
                    String status = determineStatus(resultValue, referenceRange);

                    // Adjust output format to include the unit on its own line
                    String finalRow = "" + testName + "\n" +
                            "النتيجة: " + resultValue + "\n" +
                            "الحالة: " + status + "\n" +
                            "النطاق المرجعي: " + referenceRange;

                    Log.d("FinalRow", "Processed Row: " + finalRow);
                    groupedLines.add(finalRow);
                }
            }
        }

        if (groupedLines.isEmpty()) {
            Toast.makeText(requireContext(), "لم يتم التعرف على نص", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(requireContext(), TestResultSc.class);
            intent.putStringArrayListExtra("recognizedLines", new ArrayList<>(groupedLines));
            startActivity(intent);
        }
    }







    private String cleanRow(String row) {
        return row.replaceAll("\\[|\\]|\\(|\\)|\\||NORMAL|,|\\s{2,}", " ") // Remove brackets, parentheses, pipe symbol, "NORMAL", and extra spaces
                .replaceAll("\\b(?:nmolL|J umlL|mmol/L|UL|J uL|umolL|J ugL|pmolL|miuL|JpmollL|pg/mL|ng/mL|mg/dL|nmol/L|g/L|IU/L|µg/L|U/L|kU/L|mmoL|/LL|J pmollL|J)\\b", "") // Remove specified units
                .trim(); // Trim leading/trailing spaces
    }



    private String extractUnit(String row) {
        // Match and return the unit from the row
        Pattern pattern = Pattern.compile("\\b(mg/dL|nmol/L|g/L|IU/L|mmol/L|µg/L|pg/mL|ng/mL|U/L|kU/L|nmolL|mmoL|J umlL|/L|UL|J uL|umolL|J ugL|pmolL|miuL|JpmollL)\\b");
        Matcher matcher = pattern.matcher(row);
        if (matcher.find()) {
            return matcher.group(1); // Return the detected unit
        }
        return null; // Return null if no unit is found
    }

    private boolean isSameRowWithHorizontalAllowance(Rect box1, Rect box2) {
        int verticalThreshold = 30; // Increased tolerance for row height
        int horizontalAllowance = 1500; // Larger allowance for distant elements

        boolean isVerticallyAligned = Math.abs(box1.top - box2.top) < verticalThreshold;

        boolean isHorizontallyClose = (box1.right < box2.left && (box2.left - box1.right) < horizontalAllowance) ||
                (box2.right < box1.left && (box1.left - box2.right) < horizontalAllowance);

        Log.d("BoundingBoxCheck", "Box1: " + box1 + ", Box2: " + box2 + ", Vertically Aligned: " + isVerticallyAligned + ", Horizontally Close: " + isHorizontallyClose);

        return isVerticallyAligned && isHorizontallyClose;
    }


    private HashMap<String, String> getTranslationMap() {
        HashMap<String, String> translations = new HashMap<>();
        translations.put("VITAMIN D3 (25-OH)", "فيتامين د3");
        translations.put("VITAMIN D3 (25-0H)", "فيتامين د3");

        translations.put("BLOOD UREA NITROGEN", "نيتروجين يوريا الدم");
        translations.put("CREATININE, SERUM", "الكرياتينين، المصل");
        translations.put("CALCIUM,TOTAL", "الكالسيوم، الإجمالي");
        translations.put("ALKALINE PHOSPHATASE, SERUM", "أنزيم الفوسفاتاز القلوي");
        translations.put("ALANINE AMINOTRANSFERASE", "أنزيم ناقلة أمين الألانين");
        translations.put("CONJUGATED BILIRUBIN", "البيليروبين المباشر في الدم");
        translations.put("URIC ACID SERUM", "حمض اليوريك");
        translations.put("IRON SERUM", "الحديد");
        translations.put("FERRITIN SERUM", "مخزون الحديد");
        translations.put("VITAMINB12", "فيتامين ب12");
        translations.put("THYROID STIMULATING HORMONE", "هرمون تحفيز الغدة الدرقية");
        translations.put("THYROXINE- FREE", "ثيروكسين - الحر");
        // Add more variations of names if needed
        return translations;
    }


    private String translateLine(String line, HashMap<String, String> translations) {
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (line.toUpperCase().replaceAll("\\s+", "").contains(key.toUpperCase().replaceAll("\\s+", ""))) {
                Log.d("TranslationMatch", "Fuzzy Match Found: " + key + " -> " + value);
                line = line.replaceAll("(?i)" + Pattern.quote(key), value);
            }
        }
        return line.trim();
    }




    private String extractResult(String row) {
        // Match and return the result value (e.g., "16" from "VITAMIN D3 (25-OH) 16 nmol/L")
        Pattern pattern = Pattern.compile("\\b(\\d+(\\.\\d+)?)\\b");
        Matcher matcher = pattern.matcher(row);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    private String extractReferenceRange(String row) {
        // Match and return the reference range (e.g., "50 - 125" from "[50 - 125]")
        Pattern pattern = Pattern.compile("\\b(\\d+(\\.\\d+)?\\s*-\\s*\\d+(\\.\\d+)?)\\b");
        Matcher matcher = pattern.matcher(row);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    private String determineStatus(String result, String referenceRange) {
        try {
            double value = Double.parseDouble(result);
            String[] ranges = referenceRange.split("-");
            double lower = Double.parseDouble(ranges[0].trim());
            double upper = Double.parseDouble(ranges[1].trim());

            if (value < lower) return "ناقص";
            if (value > upper) return "زائد";
            return "طبيعي";
        } catch (Exception e) {
            Log.e("DetermineStatus", "Error determining status: " + e.getMessage());
        }
        return "غير معروف";
    }




}
