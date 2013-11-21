package com.example.silencer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Alex on 21.11.13.
 */
public class MyService extends Service {

    long timeStart;
    long timeStop;
    final String LOG_TAG = "myLogs";
    PendingIntent piOnStart;
    PendingIntent piOnStop;
    Intent intentOfSound;
    Intent intentOnSound;


    public int onStartCommand(Intent intent, int flags, int startId) {

        timeStart = intent.getLongExtra("timeStart", 0);
        timeStop = intent.getLongExtra("timeStop", 0);
        Calendar timeFromDB = Calendar.getInstance();
        timeFromDB.setTimeInMillis(timeStart);
        int minut = timeFromDB.get(Calendar.MINUTE);
        soundOff(timeStart);
        soundOn(timeStop);
        Log.d(LOG_TAG, "Hour: " + String.valueOf(minut));
        return super.onStartCommand(intent, flags, startId);

    }

    private void soundOff(long startTime) {

        intentOfSound = new Intent(this, offSoundResiver.class);
        piOnStart = PendingIntent.getBroadcast(this,0,intentOfSound,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmS = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmS.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY, piOnStart);

    }

    private void soundOn(long stopTime) {

        intentOnSound = new Intent(this, onSoundResiver.class);
        piOnStop = PendingIntent.getBroadcast(this,0,intentOnSound,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmE = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmE.setRepeating(AlarmManager.RTC_WAKEUP, stopTime, AlarmManager.INTERVAL_DAY, piOnStop);

    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
