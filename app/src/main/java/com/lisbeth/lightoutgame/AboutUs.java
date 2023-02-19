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

/**
 * This class is the activity that will be displayed when the user clicks on the
 * "About Us" button in
 * the main menu. This activity will display information about the game
 */
public class AboutUs extends AppCompatActivity {

    // ! attributes
    private VideoView videoHowToPlay;
    private ImageButton toHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.videoHowToPlay = findViewById(R.id.video_how_to_play);

        // locate the video and put it in a VideoView
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.resolver);
        videoHowToPlay.setVideoURI(videoUri);
        videoHowToPlay.start();

        // Creating a button that will take you back to the main menu.
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