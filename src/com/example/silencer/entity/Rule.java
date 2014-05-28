package com.example.silencer.entity;

public class Rule {

    private int id;
    private long startTime;
    private long stopTime;
    private int days;
    private int enabled;
    private int vibrate;

/*    public Rule(long num, long stTime, long spTime, int wDays, boolean onOf, int vibra){
        id = num;
        startTime =  stTime;
        stopTime = spTime;
        days = wDays;
        vibrate = vibra;
        enabled = onOf;
    }

    public Rule(long num, long stTime, long spTime){
        id = num;
        startTime =  stTime;
        stopTime = spTime;

    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getEnabled() {
        return enabled;
    }

    public void setEnable(int enabled) {
        this.enabled = enabled;
    }

    public int getVibrate() { return vibrate; }

    public void setVibrate(int vibrate) { this.vibrate = vibrate; }
}
