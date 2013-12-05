package com.example.silencer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MyService extends Service {

    long timeStart;
    long timeStop;
    long startDayTime;

    AudioManager sound;
    boolean flag = false;
    Context context;
    ArrayList<Rule> rules;
    Calendar startDay;
    long ruleID;
    boolean enable;
    int vibrate;
    int days;
    int currentDayOfWeek;

    Rule rule;

    final String LOG_TAG = "myLogs";

    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean onceFlag = false; //Whether or not alarm set once

        timeStart = intent.getLongExtra("timeStart", 0);
        timeStop = intent.getLongExtra("timeStop", 0);
        ruleID = intent.getLongExtra("id", 0);
        enable = intent.getBooleanExtra("enable", true);
        vibrate = intent.getIntExtra("vibrate", 0);
        days = intent.getIntExtra("days", 0);

        startDay = Calendar.getInstance();
        startDay.setTimeInMillis(System.currentTimeMillis());

        long currentTime = startDay.getTimeInMillis();

        Calendar timeFromDB = Calendar.getInstance();
        timeFromDB.setTimeInMillis(timeStart);

        Calendar timeToDB = Calendar.getInstance();
        timeToDB.setTimeInMillis(timeStop);

        int minut = timeFromDB.get(Calendar.MINUTE);
        Log.d(LOG_TAG, "minut: " + minut);

        //set midnight
        startDay.set(Calendar.MILLISECOND, 0);
        startDay.set(Calendar.SECOND, 0);
        startDay.set(Calendar.MINUTE, timeFromDB.get(Calendar.MINUTE));
        startDay.set(Calendar.HOUR_OF_DAY, timeFromDB.get(Calendar.HOUR_OF_DAY));
        //long timeStart1 = startDay.getTimeInMillis();
        long timeStart1 = timeStart;
        startDay.set(Calendar.MINUTE, timeToDB.get(Calendar.MINUTE));
        startDay.set(Calendar.HOUR_OF_DAY, timeToDB.get(Calendar.HOUR_OF_DAY));
//        long timeStop1 = startDay.getTimeInMillis();
        long timeStop1 = timeStop;
        currentDayOfWeek = startDay.get(Calendar.DAY_OF_WEEK) - 1; //first day of week begin in sunday
        Log.d(LOG_TAG, "---day of month: " + currentDayOfWeek);
        // get rules for this day
        context = getApplicationContext();
        Toast.makeText(context, "Start service", Toast.LENGTH_LONG).show();
        sound = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

      //  test rules when system state changed
        if (enable){
            ArrayList<Integer> week = new TimeService(context).getWeek(days);
            Calendar calc = Calendar.getInstance();
            calc.setTimeInMillis(timeStart);

            if (week.size() !=0){                               //if any selected day of week
                Log.d(LOG_TAG, "---year: " + calc.get(Calendar.YEAR));
                Log.d(LOG_TAG, "---month: " + calc.get(Calendar.MONTH));
                Log.d(LOG_TAG, "---day of month: " + calc.get(Calendar.DAY_OF_MONTH));
                Log.d(LOG_TAG, "---day week: " + calc.get(Calendar.DAY_OF_WEEK));
                Log.d(LOG_TAG, "---hour: " + calc.get(Calendar.HOUR_OF_DAY));
                Log.d(LOG_TAG, "---minut: " + calc.get(Calendar.MINUTE));

                onceFlag = false;

                for (int i = 0; i < week.size(); i++){
                    if (week.get(i) < currentDayOfWeek){
                        timeStart = timeStart1 + (7 - currentDayOfWeek + week.get(i))*AlarmManager.INTERVAL_DAY;
                        timeStop = timeStop1 + (7 - currentDayOfWeek + week.get(i))*AlarmManager.INTERVAL_DAY;
                    }
                    else if (week.get(i) > currentDayOfWeek){
                    timeStart = timeStart1 + (week.get(i) - currentDayOfWeek)*AlarmManager.INTERVAL_DAY;
                    timeStop = timeStop1 + (week.get(i) - currentDayOfWeek)*AlarmManager.INTERVAL_DAY;
                    }

                    calc.setTimeInMillis(timeStart);

                    Log.d(LOG_TAG, "day of month: " + calc.get(Calendar.DAY_OF_MONTH));
                    Log.d(LOG_TAG, "day week: " + calc.get(Calendar.DAY_OF_WEEK));
                    Log.d(LOG_TAG, "hour: " + calc.get(Calendar.HOUR_OF_DAY));
                    Log.d(LOG_TAG, "minut: " + calc.get(Calendar.MINUTE));

                    setTime(timeStart, timeStop, ruleID, onceFlag);

                }
            }else{                              //if none is selected start current set time
                onceFlag = true;
                if (timeStart < currentTime){
                    timeStart += AlarmManager.INTERVAL_DAY;
                    timeStop += AlarmManager.INTERVAL_DAY;
                }
                setTime(timeStart, timeStop, ruleID, onceFlag);
            }

        } else removeAlarm(context, ruleID);

        return super.onStartCommand(intent, flags, startId);

    }

    private void setTime(long timeStart, long timeStop, long ruleID, boolean onceFlag){
        if (sound.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            soundOff(timeStart, ruleID, onceFlag);
            soundOn(timeStop, ruleID, onceFlag);
        } else soundOff(timeStart, ruleID, onceFlag);
    }

    public void soundOff(long startTime, long ruleID, boolean onceFlag) {

        Intent intentOfSound = new Intent(context, offSoundResiver.class);
        if (vibrate == 1) intentOfSound.putExtra("vibrate", true);
        else intentOfSound.putExtra("vibrate", false);
        PendingIntent piOnStart = PendingIntent.getBroadcast(this, (int)ruleID, intentOfSound,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        if (onceFlag) alarm.set(AlarmManager.RTC_WAKEUP, startTime, piOnStart);
        else alarm.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY*7, piOnStart);

    }

    public void soundOn(long stopTime, long ruleID, boolean onceFlag) {

        Intent intentOnSound = new Intent(context, onSoundResiver.class);
        PendingIntent piOnStop = PendingIntent.getBroadcast(this, -(int)ruleID, intentOnSound,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        if (onceFlag) alarm.set(AlarmManager.RTC_WAKEUP, stopTime, piOnStop);
        else alarm.setRepeating(AlarmManager.RTC_WAKEUP, stopTime, AlarmManager.INTERVAL_DAY*7, piOnStop);

    }

    private void removeAlarm(Context context, long ruleID) {
        Intent removeIntentOff = new Intent(context, offSoundResiver.class);
        PendingIntent piRemoveOff = PendingIntent.getBroadcast(context, (int) ruleID, removeIntentOff, 0);
        AlarmManager alarmManagerOff = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManagerOff.cancel(piRemoveOff);

        Intent removeIntentOn = new Intent(context, onSoundResiver.class);
        PendingIntent piRemoveOn = PendingIntent.getBroadcast(context, -(int) ruleID, removeIntentOn, 0);
        AlarmManager alarmManagerOn = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManagerOn.cancel(piRemoveOn);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
