package com.example.silencer;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;


public class TimeService {
    final String LOG_TAG = "myLogs";
    Context context;

    TimeService(Context context){
        this.context = context;
    }
    // get day of week
    public ArrayList<Integer> getWeek(int days){

        ArrayList<Integer> weekDays = new ArrayList<Integer>();
        for(int i = 1; i <= 64 ; i=i*2){
            int result = days & i;
            switch (result){
                case 1:
                    weekDays.add(1);
                    break;
                case 2:
                    weekDays.add(2);
                    break;
                case 4:
                    weekDays.add(3);
                    break;
                case 8:
                    weekDays.add(4);
                    break;
                case 16:
                    weekDays.add(5);
                    break;
                case 32:
                    weekDays.add(6);
                    break;
                case 64:
                    weekDays.add(7);
                    break;
                default:
                    break;
            }
        }
        return weekDays;
    }
}
