package com.lisbeth.lightoutgame;

import android.os.Handler;
import android.widget.TextView;

public class TimerRunnable implements Runnable {

    private int seconds;
    private Handler handler;
    private TextView textTime;

    public TimerRunnable(Handler handler, TextView textTime) {
        this(handler, textTime, 0);
    }

    public TimerRunnable(Handler handler, TextView textTime, int initialTime) {
        this.handler = handler;
        this.textTime = textTime;
        this.seconds = initialTime;
    }

    /**
     * This function will run every second and increment the seconds variable by
     * one.
     */
    @Override
    public void run() {
        seconds++;
        textTime.setText(String.valueOf(seconds));
        handler.postDelayed(this, 1000);
    }

    /**
     * This function will stop the timer and cancel the scheduled updates to the UI.
     */
    public void stop() {
        handler.removeCallbacks(this);
    }

    public int getSeconds() {
        return seconds;
    }

    public Handler getHandler() {
        return handler;
    }

    public TextView getTextTime() {
        return textTime;
    }
}
