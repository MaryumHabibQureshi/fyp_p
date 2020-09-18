package com.example.practice_3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import static com.example.practice_3.MainActivity.matT;

public class DisplayImage extends AppCompatActivity {
    private ImageView ivCapture;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        ivCapture = findViewById(R.id.ivCapture);
        btnBack = findViewById(R.id.btnBack);

        Bitmap bitmap = Bitmap.createBitmap(matT.cols(), matT.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matT, bitmap);

        ivCapture.setImageBitmap(bitmap);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}