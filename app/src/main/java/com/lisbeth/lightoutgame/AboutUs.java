package com.lisbeth.lightoutgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.VideoView;

public class AboutUs extends AppCompatActivity {
    private VideoView videoHowToPlay;
    private ImageButton toHome;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        this.videoHowToPlay = findViewById(R.id.video_how_to_play);


        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.resolver);
        videoHowToPlay.setVideoURI(videoUri);
        videoHowToPlay.start();


        this.toHome = findViewById(R.id.to_home);

        this.toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUs.this, MainActivity.class);
                startActivity(intent);
            }
        });




    }
}