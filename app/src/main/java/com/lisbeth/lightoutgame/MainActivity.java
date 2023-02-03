package com.lisbeth.lightoutgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    public void toMainGame(View view) {

        Spinner spinner = (Spinner) findViewById(R.id.spinner_width);
        String w = spinner.getSelectedItem().toString();
        spinner = (Spinner) findViewById(R.id.spinner_height);
        String h = spinner.getSelectedItem().toString();

        Intent intent = new Intent(MainActivity.this, Game.class);

        intent.putExtra("width", Integer.parseInt(w));
        intent.putExtra("height", Integer.parseInt(h));
        startActivity(intent);
    }
}