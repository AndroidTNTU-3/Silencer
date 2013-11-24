package edu.tntu.silenceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StateSoundReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.getBooleanExtra("flag", false);
        intent.putExtra("flag", false);
        Toast.makeText(context, "Sound CHANGED", Toast.LENGTH_LONG).show();
    }
}