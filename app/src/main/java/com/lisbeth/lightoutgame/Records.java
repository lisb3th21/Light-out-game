package com.lisbeth.lightoutgame;


/**
 * Records is a class that contains the date and seconds of a record
 */
public class Records {
    private long id;
    private String date;
    private String seconds;

    public Records( String date, String seconds) {

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
