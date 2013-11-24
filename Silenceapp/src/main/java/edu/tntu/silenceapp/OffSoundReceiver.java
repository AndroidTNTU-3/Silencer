package edu.tntu.silenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public class OffSoundReceiver extends BroadcastReceiver {
    final String LOG_TAG = "myLogs";

    @Override
    public void onReceive(Context context, Intent intent) {

        AudioManager sound = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        intent.getBooleanExtra("flag", false);
        intent.putExtra("flag", true);
        sound.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Toast.makeText(context, "Sound OFF", Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "Sound OFF");
    }

}
