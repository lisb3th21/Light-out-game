package com.lisbeth.lightoutgame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 1;

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE game_time (id INTEGER PRIMARY KEY AUTOINCREMENT, time INTEGER, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Agrega código aquí para actualizar la base de datos si es necesario
    }
}
