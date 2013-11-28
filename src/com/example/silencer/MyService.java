package com.example.silencer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Alex on 21.11.13.
 */
public class MyService extends Service {

    long timeStart;
    long timeStop;
    long startDayTime;

    AudioManager sound;
    boolean flag = false;
    Context context;
    ArrayList<Rule> rules;
    Calendar startDay;
    Calendar ruleTimeStart;
    Calendar ruleTimeStop;
    long ruleID;
    boolean enable;
    int days;
    int currentDayOfWeek;

    Rule rule;

    final String LOG_TAG = "myLogs";


    public int onStartCommand(Intent intent, int flags, int startId) {

        timeStart = intent.getLongExtra("timeStart", 0);
        timeStop = intent.getLongExtra("timeStop", 0);
        ruleID = intent.getLongExtra("id", 0);
        enable = intent.getBooleanExtra("enable", true);
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
        //startDay.set(Calendar.MINUTE, 0);
        //startDay.set(Calendar.HOUR_OF_DAY, 0);
        startDay.set(Calendar.MINUTE, timeFromDB.get(Calendar.MINUTE));
        startDay.set(Calendar.HOUR_OF_DAY, timeFromDB.get(Calendar.HOUR_OF_DAY));
        long timeStart1 = startDay.getTimeInMillis();
        //startDayTime = startDay.getTimeInMillis();
        //timeStart = timeStart + startDayTime;
       // timeStop = timeStart + startDayTime;


        startDay.set(Calendar.MINUTE, timeToDB.get(Calendar.MINUTE));
        startDay.set(Calendar.HOUR_OF_DAY, timeToDB.get(Calendar.HOUR_OF_DAY));
        long timeStop1 = startDay.getTimeInMillis();

        currentDayOfWeek = startDay.get(Calendar.DAY_OF_WEEK);
        Log.d(LOG_TAG, "---day of month: " + currentDayOfWeek);
        // get rules for this day
        context = getApplicationContext();
        Toast.makeText(context, "Start service", Toast.LENGTH_LONG).show();
        sound = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

      //  test rules when system state changed
        if (enable){
            ArrayList<Integer> week = new TimeService(context).getWeek(days);

            if (week.size() !=0){                               //if any selected day of week
                Calendar calc = Calendar.getInstance();
                calc.setTimeInMillis(timeStart);
                Log.d(LOG_TAG, "---day of month: " + calc.get(Calendar.DAY_OF_MONTH));
                Log.d(LOG_TAG, "---day week: " + calc.get(Calendar.DAY_OF_WEEK));
                Log.d(LOG_TAG, "---hour: " + calc.get(Calendar.HOUR_OF_DAY));
                Log.d(LOG_TAG, "---minut: " + calc.get(Calendar.MINUTE));

                for (int i = 0; i < week.size(); i++){
                    Log.d(LOG_TAG, "---day of week in base: " + week.get(i));
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

                    soundOff(timeStart, ruleID);
                    soundOn(timeStop, ruleID);

                }
            }else{                              //if none is selected start current set time
                if (timeStart < currentTime){
                    timeStart += AlarmManager.INTERVAL_DAY;
                    timeStop += AlarmManager.INTERVAL_DAY;
                }

                soundOff(timeStart, ruleID);
                soundOn(timeStop, ruleID);
            }


           /* if (sound.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                soundOff(timeStart, ruleID);
                soundOn(timeStop, ruleID);
            } else soundOff(timeStart, ruleID);*/
        } else removeAlarm(context, ruleID);

        return super.onStartCommand(intent, flags, startId);

    }

    public void soundOff(long startTime, long ruleID) {

        Intent intentOfSound = new Intent(context, offSoundResiver.class);
        //PendingIntent piOnStart = PendingIntent.getBroadcast(this, (int)ruleID, intentOfSound,0);
        PendingIntent piOnStart = PendingIntent.getBroadcast(this, (int)ruleID, intentOfSound,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY*7, piOnStart);
        Calendar weekalarm = Calendar.getInstance();
        weekalarm.setTimeInMillis(startTime);

    }

    public void soundOn(long stopTime, long ruleID) {

        Intent intentOnSound = new Intent(context, onSoundResiver.class);
        //PendingIntent piOnStop = PendingIntent.getBroadcast(this,-(int) ruleID,intentOnSound,0);
        PendingIntent piOnStop = PendingIntent.getBroadcast(this, (int)ruleID, intentOnSound,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, stopTime, AlarmManager.INTERVAL_DAY*7, piOnStop);

    }

    private void removeAlarm(Context context, long ruleID) {
        Intent removeIntent = new Intent(context, MyService.class);
        PendingIntent piRemove = PendingIntent.getService(context, (int) ruleID, removeIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(piRemove);
    }


    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
