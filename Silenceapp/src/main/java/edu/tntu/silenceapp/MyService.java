package edu.tntu.silenceapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class MyService extends Service {

    long timeStart;
    long timeStop;
    final String LOG_TAG = "myLogs";
    PendingIntent piOnStart;
    PendingIntent piOnStop;
    Intent intentOfSound;
    Intent intentOnSound;
    AudioManager sound;
    boolean flag = false;
    Context context;


    public int onStartCommand(Intent intent, int flags, int startId) {

        timeStart = intent.getLongExtra("timeStart", 0);
        timeStop = intent.getLongExtra("timeStop", 0);
        Calendar timeFromDB = Calendar.getInstance();
        timeFromDB.setTimeInMillis(timeStart);

        int minut = timeFromDB.get(Calendar.MINUTE);
        Log.d(LOG_TAG, "minut: " + minut);

        context = getApplicationContext();
        Toast.makeText(context, "Start service", Toast.LENGTH_LONG).show();
        sound = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //  test rules when system state changed
        if (sound.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            soundOff(timeStart);
            soundOn(timeStop);
        } else soundOff(timeStart);

        return super.onStartCommand(intent, flags, startId);

    }

    private void soundOff(long startTime) {

        intentOfSound = new Intent(this, OffSoundReceiver.class);
        intentOfSound.putExtra("flag", flag);
        piOnStart = PendingIntent.getBroadcast(this, 0, intentOfSound, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmS = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmS.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY, piOnStart);

    }

    private void soundOn(long stopTime) {

        intentOnSound = new Intent(this, OnSoundReceiver.class);
        intentOfSound.putExtra("flag", flag);
        piOnStop = PendingIntent.getBroadcast(this, 0, intentOnSound, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmE = (AlarmManager) getSystemService(ALARM_SERVICE);
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
