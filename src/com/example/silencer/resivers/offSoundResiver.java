package com.example.silencer.resivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Alex on 21.11.13.
 */
public class offSoundResiver extends BroadcastReceiver {
    final String LOG_TAG = "myLogs";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        AudioManager sound = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        boolean vibrate = intent.getBooleanExtra("vibrate", false);
        //intent.putExtra("flag",true);
        if (vibrate) sound.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else sound.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Toast.makeText(context, "Sound OFF", Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "Sound OFF");
    }

}
