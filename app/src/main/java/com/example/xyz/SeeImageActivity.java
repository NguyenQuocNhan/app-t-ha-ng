package com.example.xyz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SeeImageActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_image);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        final String imageProduct = intent.getStringExtra("image");

        Glide.with(SeeImageActivity.this).load(imageProduct).into(imageView);
    }
}