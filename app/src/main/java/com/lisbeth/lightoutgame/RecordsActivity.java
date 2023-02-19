package com.lisbeth.lightoutgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class RecordsActivity extends AppCompatActivity {
    private ImageButton toHome;
    private ListView listView;
    private DatabaseAdapter databaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);


        listView = findViewById(R.id.listview);
        this.toHome = findViewById(R.id.to_home);
        this.toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        GameDatabaseHelper databaseHelper = new GameDatabaseHelper(this);
        List<Records> itemList = databaseHelper.getAllScores();

        databaseAdapter = new DatabaseAdapter(this, itemList);
        listView.setAdapter(databaseAdapter);


    }



}


