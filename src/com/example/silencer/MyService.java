package com.example.silencer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.Toast;

import com.example.silencer.db.DBAdapter;
import com.example.silencer.entity.Rule;
import com.example.silencer.resivers.offSoundResiver;
import com.example.silencer.resivers.onSoundResiver;

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
    int ruleID;
    boolean enable;
    int vibrate;
    int days;
    int currentDayOfWeek;

    Rule rule;
    DBAdapter dbAdapter;

    final String LOG_TAG = "myLogs";

    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean onceFlag = false; //Whether or not alarm set once

        dbAdapter = new DBAdapter(getApplicationContext());
        rule = dbAdapter.getRule(intent.getIntExtra("id", 0));

        ruleID = rule.getId();
        timeStart = rule.getStartTime();
        timeStop = rule.getStopTime();
        enable = rule.getEnabled()!=0;
        vibrate = rule.getVibrate();
        days = rule.getDays();


        startDay = Calendar.getInstance();
        startDay.setTimeInMillis(System.currentTimeMillis());

       long currentTime = startDay.getTimeInMillis();

        Calendar timeFromDB = Calendar.getInstance();
        timeFromDB.setTimeInMillis(timeStart);

        Calendar timeToDB = Calendar.getInstance();
        timeToDB.setTimeInMillis(timeStop);

        //set midnight
        startDay.set(Calendar.MILLISECOND, 0);
        startDay.set(Calendar.SECOND, 0);
        startDay.set(Calendar.MINUTE, timeFromDB.get(Calendar.MINUTE));
        startDay.set(Calendar.HOUR_OF_DAY, timeFromDB.get(Calendar.HOUR_OF_DAY));

        long timeStart1 = timeStart;
        startDay.set(Calendar.MINUTE, timeToDB.get(Calendar.MINUTE));
        startDay.set(Calendar.HOUR_OF_DAY, timeToDB.get(Calendar.HOUR_OF_DAY));

        long timeStop1 = timeStop;
        currentDayOfWeek = startDay.get(Calendar.DAY_OF_WEEK) - 1; //first day of week begin in sunday

        // get rules for this day
        context = getApplicationContext();
        Toast.makeText(context, "Start service", Toast.LENGTH_LONG).show();
        sound = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

      //  test rules when system state changed
        if (enable){
            ArrayList<Integer> week = new Utils(context).getWeek(days);
            Calendar calc = Calendar.getInstance();
            calc.setTimeInMillis(timeStart);

            if (week.size() !=0){                               //if any selected day of week

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

    private void setTime(long timeStart, long timeStop, int ruleID, boolean onceFlag){
        if (sound.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            soundOff(timeStart, ruleID, onceFlag);
            soundOn(timeStop, ruleID, onceFlag);
        } else soundOff(timeStart, ruleID, onceFlag);
    }

    public void soundOff(long startTime, int ruleID, boolean onceFlag) {

        Intent intentOfSound = new Intent(context, offSoundResiver.class);
        if (vibrate == 1) intentOfSound.putExtra("vibrate", true);
        else intentOfSound.putExtra("vibrate", false);
        PendingIntent piOnStart = PendingIntent.getBroadcast(this, ruleID, intentOfSound,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        if (onceFlag) alarm.set(AlarmManager.RTC_WAKEUP, startTime, piOnStart);
        else alarm.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY*7, piOnStart);

    }

    public void soundOn(long stopTime, int ruleID, boolean onceFlag) {

        Intent intentOnSound = new Intent(context, onSoundResiver.class);
        PendingIntent piOnStop = PendingIntent.getBroadcast(this, -ruleID, intentOnSound,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        if (onceFlag) alarm.set(AlarmManager.RTC_WAKEUP, stopTime, piOnStop);
        else alarm.setRepeating(AlarmManager.RTC_WAKEUP, stopTime, AlarmManager.INTERVAL_DAY*7, piOnStop);

    }

    private void removeAlarm(Context context, int ruleID) {
        Intent removeIntentOff = new Intent(context, offSoundResiver.class);
        PendingIntent piRemoveOff = PendingIntent.getBroadcast(context, ruleID, removeIntentOff, 0);
        AlarmManager alarmManagerOff = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManagerOff.cancel(piRemoveOff);

        Intent removeIntentOn = new Intent(context, onSoundResiver.class);
        PendingIntent piRemoveOn = PendingIntent.getBroadcast(context, - ruleID, removeIntentOn, 0);
        AlarmManager alarmManagerOn = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManagerOn.cancel(piRemoveOn);
    }

    public void onDestroy() {
        super.onDestroy();
        dbAdapter.closeDB();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
