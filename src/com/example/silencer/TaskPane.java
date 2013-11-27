package com.example.silencer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import com.example.silencer.DialogTime.TimeDialogListener;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class TaskPane extends Activity implements TimeDialogListener{
    Button buttonFromTime;
    Button buttonToTime;
    Button buttonFromDate;
    Button buttonToDate;
    Button buttonSet;
    Switch switchEnable;

    CheckBox checkBoxMonday;
    CheckBox checkBoxTuesday;
    CheckBox checkBoxWednesday;
    CheckBox checkBoxThursday;
    CheckBox checkBoxFriday;
    CheckBox checkBoxSaturday;
    CheckBox checkBoxSunday;

    int hourStart = 0;
    int minutStart = 0;
    int hourStop = 0;
    int minutStop = 0;
    int buttonId = 0;

    String monday;
    String tuesday;
    String wednesday;
    String thursday;
    String friday;
    String saturday;
    String sunday;
    String notCheckedDay = "00000000";

    int binaryMonday;
    int binaryTuesday;
    int binaryWednesday;
    int binaryThursday;
    int binaryFriday;
    int binarySaturday;
    int binarySunday;
    int result;

    long timeFrom;
    long timeTo;
    Calendar timeFromCalendar;
    Calendar timeToCalendar;

    boolean sound = false;
    boolean soundAfter = false;
    boolean enable = false;


    SimpleDateFormat sdf;

    DBAdapter myDb;

    DialogTime dialogtime;

    Time timeStart;
    Time timeStop;

final String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_pane);

		checkBoxMonday = (CheckBox) findViewById(R.id.checkBoxMonday);
        checkBoxTuesday = (CheckBox) findViewById(R.id.checkBoxTuesday);
        checkBoxWednesday = (CheckBox) findViewById(R.id.checkBoxWednesday);
        checkBoxThursday = (CheckBox) findViewById(R.id.checkBoxThursday);
        checkBoxFriday = (CheckBox) findViewById(R.id.checkBoxFriday);
        checkBoxSaturday = (CheckBox) findViewById(R.id.checkBoxSaturday);
        checkBoxSunday = (CheckBox) findViewById(R.id.checkBoxSunday);
		buttonFromTime = (Button) findViewById(R.id.button_from);
		buttonToTime = (Button) findViewById(R.id.button_to);
		buttonFromDate = (Button) findViewById(R.id.buttonFromDate);
		buttonToDate = (Button) findViewById(R.id.buttonToDate);
		buttonSet = (Button) findViewById(R.id.buttonSetTask);
        switchEnable = (Switch) findViewById(R.id.switch3);
		buttonFromTime.setText(getTime());
		buttonToTime.setText(getTime());
		buttonFromDate.setText(getDate());
		buttonToDate.setText(getDate());
		buttonFromTime.setOnClickListener(new ButtonListener());
		buttonToTime.setOnClickListener(new ButtonListener());
		buttonSet.setOnClickListener(new ButtonListener());
        switchEnable.setOnCheckedChangeListener(new switchListener());
		dialogtime = new DialogTime();

        checkBoxMonday.setOnCheckedChangeListener(new switchListener());
        checkBoxTuesday.setOnCheckedChangeListener(new switchListener());
        checkBoxWednesday.setOnCheckedChangeListener(new switchListener());
        checkBoxThursday.setOnCheckedChangeListener(new switchListener());
        checkBoxFriday.setOnCheckedChangeListener(new switchListener());
        checkBoxSaturday.setOnCheckedChangeListener(new switchListener());
        checkBoxSunday.setOnCheckedChangeListener(new switchListener());

        openDB();

	    sdf = new SimpleDateFormat("HH:mm:ss");
	    
	    timeStart = new Time();
	    timeStop = new Time();

        timeFromCalendar = Calendar.getInstance();
        timeToCalendar = Calendar.getInstance();
	}
	
	//Get Time
	private CharSequence getTime() {
		String time;
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		time = today.format("%k:%M");
		return time;
	}
	
	//Get Data
	private CharSequence getDate() {
		Date d = new Date();
	    CharSequence date  = DateFormat.format("MMMM d, yyyy ", d.getTime());
		return date;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_pane, menu);
		return true;
	}

    // open TimePicker
	private class ButtonListener implements OnClickListener{	
	    @Override
		public void onClick(View v) {
            binaryMonday = Integer.parseInt(monday,2);
            binaryTuesday = Integer.parseInt(tuesday,2);
            binaryWednesday = Integer.parseInt(wednesday,2);
            binaryThursday = Integer.parseInt(thursday,2);
            binaryFriday = Integer.parseInt(friday,2);
            binarySaturday = Integer.parseInt(saturday,2);
            binarySunday = Integer.parseInt(sunday,2);

            //загальна сума

            result = binaryMonday + binaryTuesday + binaryWednesday + binaryThursday + binaryFriday
                     + binarySaturday + binarySunday;



			buttonId = v.getId();
			switch(buttonId){
			case  R.id.button_from:
			dialogtime.show(getFragmentManager(), "dlg1");
			break;
			case  R.id.button_to:
			dialogtime.show(getFragmentManager(), "dlg1");
			break;
			case  R.id.buttonSetTask:
                Date d = new Date();
                myDb.insertRow(timeFrom, timeTo, 0, enable/*, result*/);
				/*ContentValues cv = new ContentValues();

			    // connect to DB
			    SQLiteDatabase db = dbHelper.getWritableDatabase();
			    cv.put("timeStart", timeFrom);
			    cv.put("timeStop", timeTo);
			    cv.put("sound", sound); 
			    cv.put("soundAfter", soundAfter);
                cv.put("enabled", enable);
			    
			    long rowID = db.insert("mytable", null, cv);
			    Log.d(LOG_TAG, "row inserted, ID = " + rowID);*/



                Intent intent = new Intent();

                intent.putExtra("from", timeFrom);
                intent.putExtra("to", timeTo);
                intent.putExtra("enable", enable);

                setResult(RESULT_OK, intent);
                finish();

			break;			
			
			}
		}

	}
	
	private class switchListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
			switch(compoundButton.getId()) {

                case  R.id.switch3:
                    if (isChecked) {
                        enable = true;
                    } else {
                        enable = false;
                    }
                    break;
                case R.id.checkBoxMonday:
                    if (isChecked) {
                        monday = "00000001";
                    } else {
                        monday = notCheckedDay;
                    }
                    break;
                case R.id.checkBoxTuesday:
                    if (isChecked) {
                        tuesday = "00000010";
                    } else {
                        tuesday = notCheckedDay;
                    }
                    break;
                case R.id.checkBoxWednesday:
                    if (isChecked) {
                        wednesday = "00000100";
                    } else {
                        wednesday = notCheckedDay;
                    }
                    break;
                case R.id.checkBoxThursday:
                    if (isChecked) {
                        thursday = "00001000";
                    } else {
                        thursday = notCheckedDay;
                    }
                    break;
                case R.id.checkBoxFriday:
                    if (isChecked) {
                        friday = "00010000";
                    } else {
                        friday = notCheckedDay;
                    }
                    break;
                case R.id.checkBoxSaturday:
                    if (isChecked) {
                        saturday = "00100000";
                    } else {
                        saturday = notCheckedDay;
                    }
                    break;
                case R.id.checkBoxSunday:
                    if (isChecked) {
                        sunday = "01000000";
                    } else {
                        sunday = notCheckedDay;
                    }
                    break;
			}
			
		}
		
	};

	//CallBack
	@Override
	public void onDialogSetClick(int Hour, int Minut) {
	
		switch(buttonId){
			case  R.id.button_from:
			{
				hourStart = Hour;
				minutStart = Minut;
                timeFromCalendar.set(Calendar.HOUR_OF_DAY, Hour);
                timeFromCalendar.set(Calendar.MINUTE, Minut);
                timeFromCalendar.set(Calendar.SECOND, 0);
                timeFromCalendar.set(Calendar.MILLISECOND, 0);
                timeFrom = timeFromCalendar.getTimeInMillis();
				timeStart.set(0, minutStart, hourStart, 0, 0, 0);
				buttonFromTime.setText(timeStart.format("%k:%M"));
			}
			break;
			case R.id.button_to:
			{
				hourStop = Hour;
				minutStop = Minut;
                timeToCalendar.set(Calendar.HOUR_OF_DAY, Hour);
                timeToCalendar.set(Calendar.MINUTE, Minut);
                timeToCalendar.set(Calendar.SECOND, 0);
                timeToCalendar.set(Calendar.MILLISECOND, 0);

                timeTo = timeToCalendar.getTimeInMillis();
				timeStop.set(0, minutStop, hourStop, 0, 0, 0);
				buttonToTime.setText(timeStop.format("%k:%M"));
			}
			break;
		}
	}

    private void openDB() {
        myDb = new DBAdapter(this);
        myDb.open();
    }

}
