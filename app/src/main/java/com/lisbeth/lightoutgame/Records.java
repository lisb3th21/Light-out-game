package com.lisbeth.lightoutgame;

import android.content.ContentValues;

import java.util.UUID;

public class Records {
    private String id;
    private String date;
    private String seconds;

    public Records( String date, String seconds) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.seconds = seconds;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(RecordsContract.RecordEntry.ID, id);
        values.put(RecordsContract.RecordEntry.DATE, date);
        values.put(RecordsContract.RecordEntry.SECONDS, seconds);

        return values;
    }
}
