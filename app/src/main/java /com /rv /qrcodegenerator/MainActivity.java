package com.rv.qrcodegenerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {

    private boolean isQRGenerated = false; // Tracks whether a QR code is generated
    private long lastBackPressedTime = 0; // Tracks the time of the last back button press
    private static final long BACK_PRESS_THRESHOLD = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.edit_text);
        Button button = findViewById(R.id.button);
        ImageView imageView = findViewById(R.id.qr_code);
        RelativeLayout rootLayout = findViewById(R.id.root_layout);

        // Generate QR code
        button.setOnClickListener(v -> {
            hideKeyboard();
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(editText.getText().toString(), BarcodeFormat.QR_CODE, 300, 300);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageView.setImageBitmap(bitmap);
                isQRGenerated = true; // Set flag to indicate QR code is generated
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
        });

        // Close keyboard when touching outside EditText
        rootLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View currentFocus = getCurrentFocus();
                if (currentFocus != null && currentFocus instanceof EditText) {
                    hideKeyboard();
                    currentFocus.clearFocus();
                }
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if (isQRGenerated) {
            // Reset the app to its initial state
            resetApp();
        } else {
            // Exit app on double back press
            if (System.currentTimeMillis() - lastBackPressedTime < BACK_PRESS_THRESHOLD) {
                super.onBackPressed(); // Exit the app
            } else {
                lastBackPressedTime = System.currentTimeMillis();
                showToast("Press back again to exit");
            }
        }
    }

    // Helper method to reset the app state
    private void resetApp() {
        EditText editText = findViewById(R.id.edit_text);
        ImageView imageView = findViewById(R.id.qr_code);

        editText.setText(""); // Clear the text input
        imageView.setImageBitmap(null); // Clear the QR code
        isQRGenerated = false; // Reset the flag
    }

    // Helper method to hide the keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Helper method to show a toast message
    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}
