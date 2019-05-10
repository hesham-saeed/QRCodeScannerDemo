package com.example.qrcodescannerdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int INTENT_REQUEST = 444;

    private TextView dataTextView;
    private ImageView barCodeImageView;
    private Button processButton;
    private Button takePhotoByCameraButton;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataTextView = findViewById(R.id.txtContent);
        barCodeImageView = findViewById(R.id.imgview);
        processButton = findViewById(R.id.button);

        takePhotoByCameraButton = findViewById(R.id.takePhotoBtn);

        takePhotoByCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Please Note that sometimes you may receive No Data Found message after Processing
                //That maybe due to a bad image capture, so try to take a good image, cropping only the specific barcode and clipping the rest

                //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //if you want to capture an image
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //here, we only pick from gallery
                intent.setType("image/*");
                startActivityForResult(intent, INTENT_REQUEST);
            }
        });

        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting up Barcode detector to detect many types
                BarcodeDetector detector =
                        new BarcodeDetector.Builder(getApplicationContext())
                                //.setBarcodeFormats(Barcode.CODABAR | Barcode.QR_CODE)
                                //uncomment the previous line if you one a specific format, by default Barcode checks for all formats
                                .build();

                if (!detector.isOperational()) {
                    dataTextView.setText("Could not set up the detector!");
                    return;
                }

                if (bitmap == null) {
                    dataTextView.setText("Empty Bitmap");
                    return;
                } else
                    barCodeImageView.setImageBitmap(bitmap);

                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Barcode> barcodes = detector.detect(frame);
                if (barcodes.size() != 0){
                    Barcode thisCode = barcodes.valueAt(0);
                    TextView txtView = findViewById(R.id.txtContent);
                    txtView.setText(thisCode.rawValue);
                }
                else {
                    dataTextView.setText("No data found");
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == INTENT_REQUEST && resultCode == RESULT_OK ) {

            if (data == null)
                Toast.makeText(this, "Empty Result", Toast.LENGTH_SHORT).show();
            else
            {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    barCodeImageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
