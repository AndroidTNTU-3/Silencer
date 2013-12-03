package com.example.silencer;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.silencer.DBAdapter;
import com.example.silencer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.support.v4.app.ActivityCompat.startActivity;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

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

    DBAdapter myDb;

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


    public void bindView(View view, final Context _context, final Cursor _cursor) {

        final Context context =  (MainActivity) _context;
        final Activity ma = (MainActivity) _context;
        int id_id = cursor.getColumnIndex( DBAdapter.KEY_ROWID );
        int idfromTime = cursor.getColumnIndex( DBAdapter.KEY_FROM_TIME );
        int idtoTime = cursor.getColumnIndex( DBAdapter.KEY_TO_TIME );
        int idDate = cursor.getColumnIndex( DBAdapter.KEY_DATE );
        int idEnable = cursor.getColumnIndex( DBAdapter.KEY_ENABLED );
        int idVibrate = cursor.getColumnIndex( DBAdapter.KEY_VIBRATE );
        final long id = cursor.getInt(id_id);
        final long fromTime = cursor.getLong(idfromTime);
        final long toTime = cursor.getLong(idtoTime);
        final int date = cursor.getInt(idDate);
        final int enable = cursor.getInt(idEnable);
        final int vibrate = cursor.getInt(idVibrate);

        /*DBHelper dbHelper = new DBHelper(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();*/

        openDB();

        String sql = "SELECT * FROM mytable WHERE _id=" + String.valueOf(id)+";";
        myDb.getRowQuery(sql);

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

        //get week days
        ArrayList<Integer> week = new TimeService(context).getWeek(date);
        Resources resource = context.getResources();

        if (week.size() !=0){
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
                    Rule rule = new Rule(id, fromTime, toTime, date, true, vibrate);
                    //update DB
                    myDb.updateRow(id, fromTime, toTime, date, 1, vibrate);
                    ((MainActivity) _context).setRule(rule);
                   // ((MainActivity) _context).setTime(fromTime, toTime);

                }
                else if (!checkBox.isChecked()) {
                    Rule rule = new Rule(id, fromTime, toTime, date, false, vibrate);
                    myDb.updateRow(id, fromTime, toTime, date, 0, vibrate);
                    ((MainActivity) _context).setRule(rule);
                }
            }
        });

    }


    @Override
    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        return view;

    }

    private void openDB() {
        myDb = new DBAdapter(context);
        myDb.open();
    }

}
