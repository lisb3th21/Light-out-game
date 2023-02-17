package com.lisbeth.lightoutgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lisbeth.lightoutgame.RecordsContract;

import java.util.List;

public class GameDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Records.db";
    private static final int DATABASE_VERSION = 1;

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + RecordsContract.RecordEntry.TABLE_NAME + " ("
                + RecordsContract.RecordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RecordsContract.RecordEntry.ID + " TEXT NOT NULL,"
                + RecordsContract.RecordEntry.DATE + " TEXT NOT NULL,"
                + RecordsContract.RecordEntry.SECONDS + " TEXT NOT NULL,"
                + "UNIQUE (" + RecordsContract.RecordEntry.ID + "))");    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Agrega código aquí para actualizar la base de datos si es necesario
    }


    public  long saveRecord(Records records){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                RecordsContract.RecordEntry.TABLE_NAME,
                null,
                records.toContentValues());

    }

    public void showRecords(){
        String[] columns = {"ID", "DATE", "SECONDS"};
        Cursor cursor = getWritableDatabase().query("Records", columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String  time = cursor.getString(cursor.getColumnIndexOrThrow("seconds"));
            Log.d("Record: ", "id: " + id + ", time: " + time + ", date: " + date);
        }
        cursor.close();
    }


    public List<Records> getAllItems() {
        return  null;
    }
}
