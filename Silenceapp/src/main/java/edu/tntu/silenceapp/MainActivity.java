package edu.tntu.silenceapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends Activity implements OnClickListener {
    TextView clock;
    Button btn_add;
    Button start;
    ListView myList;
    TaskPane taskPane;
    TextView labelfrom;
    TextView labelto;
    Intent intentEdit;

    SharedPreferences sPref;
    boolean enable = false;

    long timeFrom;
    long timeTo;
    Calendar settedTimeFrom;
    Calendar settedTimeTo;
    Calendar timeFromDB;
    Calendar timeToDB;

    DBAdapter myDb;
    SimpleCursorAdapter myCursorAdapter;
    final String LOG_TAG = "myLogs";
    final String FROM_TIME = "from_time";
    final String FROM_TO = "to_time";


    int selectedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clock = (TextView) findViewById(R.id.clock);
        clock.setText(getTime());
        btn_add = (Button) findViewById(R.id.button_add);
        start = (Button) findViewById(R.id.button_start);
        btn_add.setOnClickListener(this);
        start.setOnClickListener(this);
        myList = (ListView) findViewById(R.id.listView1);
        myList.setFocusable(true);
        myList.setOnItemClickListener(new listItemListener());
        intentEdit = new Intent(this, TaskPaneEdit.class);
        openDB();

        Cursor cursor = myDb.getAllRows();

        settedTimeFrom = Calendar.getInstance();
        settedTimeTo = Calendar.getInstance();
        timeFromDB = Calendar.getInstance(); //time from DB (set in TimePicker from)
        timeToDB = Calendar.getInstance(); //time from DB (set in TimePicker to)

        startManagingCursor(cursor);
        String[] fromFieldNames = new String[]
                {DBAdapter.KEY_FROM_TIME, DBAdapter.KEY_TO_TIME, DBAdapter.KEY_SOUND, DBAdapter.KEY_SOUND_AFTER, DBAdapter.KEY_ENABLED};
        int[] toViewIDs = new int[]
                {R.id.textViewFrom, R.id.textViewTo, R.id.textViewSound, R.id.textViewSoundAfter};


        myCursorAdapter = new MyCursorAdapter(
                this,                    // Context
                R.layout.item_layout,    // Row layout template
                cursor,                    // cursor (set of DB records to map)
                fromFieldNames,            // DB Column names
                toViewIDs                // View IDs to put information in
        );
        myList.setAdapter(myCursorAdapter);

    }


    private void openDB() {
        myDb = new DBAdapter(this);
        myDb.open();

    }

    private void closeDB() {
        myDb.close();
    }

    //Get current time
    private CharSequence getTime() {
        String time;
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        time = today.format("%k:%M:%S");
        return time;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add:
                Intent intent = new Intent(this, TaskPane.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.button_start:
                Intent serviceIntent = new Intent(this, MyService.class);
                serviceIntent.putExtra("timeStart", timeFrom);
                serviceIntent.putExtra("timeStop", timeTo);
                startService(serviceIntent);

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        timeFrom = data.getLongExtra("from", 0); //setted time in mills
        timeTo = data.getLongExtra("to", 0); //setted time in mills
        enable = data.getBooleanExtra("enable", false);

        settedTimeFrom.setTimeInMillis(System.currentTimeMillis()); // get current time
        settedTimeTo.setTimeInMillis(System.currentTimeMillis());   // get current time
        timeFromDB.setTimeInMillis(timeFrom);                       //object Calendar in mills
        timeToDB.setTimeInMillis(timeTo);                           //object Calendar in mills

        settedTimeFrom.set(Calendar.HOUR_OF_DAY, timeFromDB.get(Calendar.HOUR_OF_DAY)); //Set silence time from
        settedTimeFrom.set(Calendar.MINUTE, timeFromDB.get(Calendar.MINUTE));           //Set silence time from

        settedTimeTo.set(Calendar.HOUR_OF_DAY, timeToDB.get(Calendar.HOUR_OF_DAY)); //Set silence time to
        settedTimeTo.set(Calendar.MINUTE, timeToDB.get(Calendar.MINUTE));           //Set silence time to

        String startTime = String.valueOf(settedTimeFrom.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(settedTimeFrom.get(Calendar.MINUTE));
        String stopTime = String.valueOf(settedTimeTo.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(settedTimeTo.get(Calendar.MINUTE));

    }

    private class listItemListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int position,
                                long id) {
            //Get selected item ID
            selectedID = position;
            intentEdit.putExtra("id", id);
            startActivityForResult(intentEdit, 1);
            Log.d(LOG_TAG, "row inserted, ID = " + id);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }


    public void setTime(long fromTime, long toTime) {
        timeFrom = fromTime;
        timeTo = toTime;
        settedTimeFrom.setTimeInMillis(System.currentTimeMillis()); // get current time
        settedTimeTo.setTimeInMillis(System.currentTimeMillis());   // get current time
        timeFromDB.setTimeInMillis(timeFrom);                       //object Calendar in mills
        timeToDB.setTimeInMillis(timeTo);                           //object Calendar in mills

        settedTimeFrom.set(Calendar.HOUR_OF_DAY, timeFromDB.get(Calendar.HOUR_OF_DAY)); //Set silence time from
        settedTimeFrom.set(Calendar.MINUTE, timeFromDB.get(Calendar.MINUTE));           //Set silence time from

        settedTimeTo.set(Calendar.HOUR_OF_DAY, timeToDB.get(Calendar.HOUR_OF_DAY)); //Set silence time to
        settedTimeTo.set(Calendar.MINUTE, timeToDB.get(Calendar.MINUTE));           //Set silence time to

        String startTime = String.valueOf(settedTimeFrom.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(settedTimeFrom.get(Calendar.MINUTE));
        String stopTime = String.valueOf(settedTimeTo.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(settedTimeTo.get(Calendar.MINUTE));

    }



   /* private void saveText(){
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putLong(FROM_TIME, timeFrom);
        ed.putLong(FROM_TO, timeTo);
        ed.commit();
    }*/

}