package com.example.shoto.gotothis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btnClick;
    private ImageView ivPicture;
    private TextView tvItem;
    // SurfaceView mCameraView;
    // CameraSource cameraSource;

    private static final int CAMERA_REQUEST = 101;
    private static final int MY_CAMERA_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvItem = (TextView) findViewById(R.id.tvItem);
        //   mCameraView = (SurfaceView) findViewById(R.id.surfaceView);

        // createCameraSource();

        btnClick = (Button) findViewById(R.id.btnClick);
        ivPicture = (ImageView) findViewById(R.id.ivPicture);

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != CAMERA_REQUEST) {
            System.out.println("Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            int maxHeight = 2000;
//            int maxWidth = 2000;
//            float scale = Math.min(((float) maxHeight / bitmap.getWidth()), ((float) maxWidth / bitmap.getHeight()));
//
//            Matrix matrix = new Matrix();
//            matrix.postScale(scale, scale);
//
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            btnClick.setText("Take another");
            ivPicture.setImageBitmap(bitmap);

            detectText(bitmap);
        }
    }

    public void detectText(Bitmap bitmap) {
        //Bitmap textBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

        if (!textRecognizer.isOperational()) {
            new AlertDialog.Builder(this)
                    .setMessage("Text recognizer could not be set up on your device :(")
                    .show();
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> text = textRecognizer.detect(frame);
        String content="";

        for (int i = 0; i < text.size(); ++i) {
            TextBlock item = text.valueAt(i);
            if (item != null && item.getValue() != null) {
                content += item.getValue();
            }
        }
        tvItem.setText(content);
    }
}
