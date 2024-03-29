package com.example.shoto.gotothis;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Recognize more date formats, clean code, and remove date from title

public class MainActivity extends AppCompatActivity {

    private Button btnClick;
    private ImageView ivPicture;
    private TextView tvItem;

    private static final int CAMERA_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvItem = (TextView) findViewById(R.id.tvItem);

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

            btnClick.setText("Take another");
            ivPicture.setImageBitmap(bitmap);

            detectText(bitmap);
        }
    }

    public void detectText(Bitmap bitmap) {

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

        Pattern p = Pattern.compile("[0-9]+[-/][0-9]+[-/][0-9]{4}");
        Matcher m = p.matcher(content);

        if(m.find()){
            String d = m.group(0);
            long startTime = 0;

            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(d);
                startTime=date.getTime();
                System.out.println(startTime);
            }
            catch(Exception e){ }

            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            calIntent.setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.ALL_DAY,true);
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, content);
            calIntent.putExtra(CalendarContract.Events.TITLE, content);
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
            startActivity(calIntent);
        }
        else {
            System.out.println("Date regex not matched");
        }
    }
}
