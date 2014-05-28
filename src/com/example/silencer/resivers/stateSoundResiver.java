package com.example.silencer.resivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Alex on 22.11.13.
 */
public class stateSoundResiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.getBooleanExtra("flag", false);
        intent.putExtra("flag",false);
        //Toast.makeText(context, "Sound CHANGED", Toast.LENGTH_LONG).show();
    }
}
