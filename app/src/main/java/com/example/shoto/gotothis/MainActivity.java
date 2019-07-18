package com.example.shoto.gotothis;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Button btnClick;
    private ImageView ivPicture;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            int maxHeight = 2000;
            int maxWidth = 2000;
            float scale = Math.min(((float)maxHeight / bitmap.getWidth()), ((float)maxWidth / bitmap.getHeight()));

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            btnClick.setText("Take another");
            ivPicture.setImageBitmap(bitmap);
        }
    }
}
