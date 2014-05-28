package com.example.silencer;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.silencer.db.DBAdapter;
import com.example.silencer.db.DBHelper;
import com.example.silencer.entity.Rule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Alex on 19.11.13.
 */
public class MyCursorAdapter extends SimpleCursorAdapter {

    private int layout;
    private final String[] from;
    private final Context context;
    private Cursor cursor;
    Intent intentEdit;

    final String LOG_TAG = "myLogs";

    Calendar setTimeFrom = Calendar.getInstance();
    Calendar setTimeTo = Calendar.getInstance();

    String startTime;
    String stopTime;

    DBAdapter dbAdapter;

    public MyCursorAdapter(Context _context, int _layout, Cursor _c, String[] _from, int[] _to) {
        super(_context, _layout, _c, _from, _to);
        layout = _layout;
        from = _from;
        context = _context;
        cursor = _c;
    }


    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
        protected CheckBox checkbox;
    }


    public void bindView(View view, final Context context, Cursor cursor) {

        final int id = cursor.getInt(cursor.getColumnIndex(DBHelper.Table._ID));
        final long fromTime = cursor.getLong(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_TIME_START ));
        final long toTime = cursor.getLong(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_TIME_STOP));
        final int date = cursor.getInt(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_DAYS));
        final int enable = cursor.getInt(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_ENABLED ));
        final int vibrate = cursor.getInt(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_VIBRATE ));

        dbAdapter = new DBAdapter(context);
        Resources resource = context.getResources();

        setTimeFrom.setTimeInMillis(fromTime);
        setTimeTo.setTimeInMillis(toTime);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        startTime = sdf.format(setTimeFrom.getTime());
        stopTime = sdf.format(setTimeTo.getTime());

        TextView textfrom = (TextView) view.findViewById(R.id.textViewFrom);
        TextView textto = (TextView) view.findViewById(R.id.textViewTo);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxEnable);
        textfrom.setText(startTime);
        textto.setText(stopTime);

        TextView labelMo = (TextView) view.findViewById(R.id.Monday);
        TextView labelTu = (TextView) view.findViewById(R.id.Tuesday);
        TextView labelWe = (TextView) view.findViewById(R.id.Wednesday);
        TextView labelTh = (TextView) view.findViewById(R.id.Thursday);
        TextView labelFr = (TextView) view.findViewById(R.id.Friday);
        TextView labelSa = (TextView) view.findViewById(R.id.Saturday);
        TextView labelSu = (TextView) view.findViewById(R.id.Sunday);

        labelMo.setTextColor(resource.getColor(R.color.darkgray));
        labelTu.setTextColor(resource.getColor(R.color.darkgray));
        labelWe.setTextColor(resource.getColor(R.color.darkgray));
        labelTh.setTextColor(resource.getColor(R.color.darkgray));
        labelFr.setTextColor(resource.getColor(R.color.darkgray));
        labelSa.setTextColor(resource.getColor(R.color.darkgray));
        labelSu.setTextColor(resource.getColor(R.color.darkgray));

        //get week days
        ArrayList<Integer> week = new Utils(context).getWeek(date);


        if (week.size() !=0){
            String s= "";
            for (int i = 0; i < week.size(); i++){

                switch(week.get(i)){
                    case 1:
                        labelMo.setTextColor(resource.getColor(R.color.deepblue));
                        break;
                    case 2:
                        labelTu.setTextColor(resource.getColor(R.color.deepblue));
                        break;
                    case 3:
                        labelWe.setTextColor(resource.getColor(R.color.deepblue));
                        break;
                    case 4:
                        labelTh.setTextColor(resource.getColor(R.color.deepblue));
                        break;
                    case 5:
                        labelFr.setTextColor(resource.getColor(R.color.deepblue));
                        break;
                    case 6:
                        labelSa.setTextColor(resource.getColor(R.color.deepblue));
                        break;
                    case 7:
                        labelSu.setTextColor(resource.getColor(R.color.deepblue));
                        break;

                }
            }
        }


      if (enable == 1) checkBox.setChecked(true);
      else checkBox.setChecked(false);
        //select rows on ListView
      view.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.d(LOG_TAG, "id = " + id);
                intentEdit = new Intent(context, TaskPaneEdit.class);
                intentEdit.putExtra("id", id);
                context.startActivity(intentEdit);

            }
        });

      checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(checkBox.isChecked()) {
                    //set object rule from service;
                    //Rule rule = new Rule(id, fromTime, toTime, date, true, vibrate);
                    Rule rule = new Rule();
                    rule.setId(id);
                    rule.setStartTime(fromTime);
                    rule.setStopTime(toTime);
                    rule.setDays(date);
                    rule.setEnable(1);
                    rule.setVibrate(vibrate);
                    dbAdapter.updateRow(id, rule);
                    startServ(id);

                }
                else if (!checkBox.isChecked()) {
                    //Rule rule = new Rule(id, fromTime, toTime, date, false, vibrate);
                    Rule rule = new Rule();
                    rule.setId(id);
                    rule.setStartTime(fromTime);
                    rule.setStopTime(toTime);
                    rule.setDays(date);
                    rule.setEnable(0);
                    rule.setVibrate(vibrate);
                    //dbAdapter.updateRow(id, fromTime, toTime, date, 0, vibrate);
                    dbAdapter.updateRow(id, rule);
                    startServ(id);
                }
            }
        });

    }

    private void startServ(int id){
        Intent serviceIntent = new Intent(context, MyService.class);
        serviceIntent.putExtra("id", id);
        context.startService(serviceIntent);
    }



    @Override
    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        return view;

    }

}
