package com.example.silencer;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        int idEnable = cursor.getColumnIndex( DBAdapter.KEY_ENABLED );
        final long id = cursor.getInt(id_id);
        final long fromTime = cursor.getLong(idfromTime);
        final long toTime = cursor.getLong(idtoTime);
        final int enable = cursor.getInt(idEnable);

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
                ContentValues cv = new ContentValues();
                Intent serviceIntent = new Intent(context, MyService.class);
                serviceIntent.putExtra("timeStart", fromTime);
                serviceIntent.putExtra("timeStop", toTime);
                context.startService(serviceIntent);


                if(checkBox.isChecked()) {
                   // itemChecked.set(pos, true);

                    myDb.updateRow(id, fromTime, toTime, 0, 1);
                    ((MainActivity) _context).setTime(fromTime, toTime);

                }
                else if (!checkBox.isChecked()) {
                  //  itemChecked.set(pos, false);
                    /*cv.put("enabled", 0);
                    long rowID = db.update("mytable", cv, "_id = ?",
                            new String[] { String.valueOf(id) });*/
                    myDb.updateRow(id, fromTime, toTime,0, 0);
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
