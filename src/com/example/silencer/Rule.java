package com.example.silencer;

/**
 * Created by silvestr on 11/27/13.
 */

public class Rule {

    private long id;
    private long startTime;
    private long stopTime;
    private int days;
    private boolean enable;
    private int vibrate;

    Rule(long num, long stTime, long spTime, int wDays, boolean onOf, int vibra){
        id = num;
        startTime =  stTime;
        stopTime = spTime;
        days = wDays;
        enable = onOf;
        vibrate = vibra;
    }

    Rule(long num, long stTime, long spTime){
        id = num;
        startTime =  stTime;
        stopTime = spTime;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getVibrate() { return vibrate; }

    public void setVibrate(int vibrate) { this.vibrate = vibrate; }
}
