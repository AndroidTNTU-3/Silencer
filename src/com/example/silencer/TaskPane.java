package com.example.silencer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import com.example.silencer.DialogTime.TimeDialogListener;

import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

public class TaskPane extends Activity implements TimeDialogListener{
Button buttonFromTime;
Button buttonToTime;
Button buttonFromDate;
Button buttonToDate;
Button buttonSet;
Switch switchSound;
Switch switchSoundAfter;
Switch switchEnable;

int hourStart =0;
int minutStart = 0;
int hourStop =0;
int minutStop = 0;
int buttonId=0;

long timeFrom;
long timeTo;
Calendar timeFromCalendar;
Calendar timeToCalendar;

boolean sound = false;
boolean soundAfter = false;
boolean enable = false;


SimpleDateFormat sdf;
DBHelper dbHelper;

DialogTime dialogtime;

Time timeStart;	
Time timeStop;	

final String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_pane);
		
		buttonFromTime = (Button) findViewById(R.id.button_from);
		buttonToTime = (Button) findViewById(R.id.button_to);
		buttonFromDate = (Button) findViewById(R.id.buttonFromDate);
		buttonToDate = (Button) findViewById(R.id.buttonToDate);
		buttonSet = (Button) findViewById(R.id.buttonSetTask);
		switchSound = (Switch) findViewById(R.id.switch1);
		switchSoundAfter = (Switch) findViewById(R.id.switch2);
        switchEnable = (Switch) findViewById(R.id.switch3);
		buttonFromTime.setText(getTime());
		buttonToTime.setText(getTime());
		buttonFromDate.setText(getDate());
		buttonToDate.setText(getDate());	
		buttonFromTime.setOnClickListener(new ButtonListener());
		buttonToTime.setOnClickListener(new ButtonListener());
		buttonSet.setOnClickListener(new ButtonListener());
		switchSound.setOnCheckedChangeListener(new switchListener());
		switchSoundAfter.setOnCheckedChangeListener(new switchListener());
        switchEnable.setOnCheckedChangeListener(new switchListener());
		dialogtime = new DialogTime();
		
	    dbHelper = new DBHelper(this);
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
			buttonId = v.getId();
			switch(buttonId){
			case  R.id.button_from:
			dialogtime.show(getFragmentManager(), "dlg1");
			break;
			case  R.id.button_to:
			dialogtime.show(getFragmentManager(), "dlg1");
			break;
			case  R.id.buttonSetTask:
				ContentValues cv = new ContentValues();
				
				Date d = new Date();
			    
			    // connect to DB
			    SQLiteDatabase db = dbHelper.getWritableDatabase();
			    cv.put("timeStart", timeFrom);
			    cv.put("timeStop", timeTo);
			    cv.put("sound", sound); 
			    cv.put("soundAfter", soundAfter);
                cv.put("enabled", enable);
			    
			    long rowID = db.insert("mytable", null, cv);
			    Log.d(LOG_TAG, "row inserted, ID = " + rowID);

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
		public void onCheckedChanged(CompoundButton v, boolean isChecked) {
			switch(v.getId()){
			case  R.id.switch1:
				if(isChecked) {
					sound = true;
			    } else {
			    	sound = false;
			    }
			break;
			case  R.id.switch2:
				if(isChecked) {
					soundAfter = true;
			    } else {
			    	soundAfter = false;
			    }
			break;
                case  R.id.switch3:
                    if(isChecked) {
                        enable = true;
                    } else {
                        enable = false;
                    }
                    break;
			}
			
		}
		
	}

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
                timeTo = timeToCalendar.getTimeInMillis();
				timeStop.set(0, minutStop, hourStop, 0, 0, 0);
				buttonToTime.setText(timeStop.format("%k:%M"));
			}
			break;
		}
	}


}
