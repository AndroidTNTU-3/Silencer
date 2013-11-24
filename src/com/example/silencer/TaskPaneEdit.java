package com.example.silencer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.silencer.DialogTime.TimeDialogListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskPaneEdit extends Activity implements TimeDialogListener{
	Button buttonFromTime;
	Button buttonToTime;
	Button buttonFromDate;
	Button buttonToDate;
	Button buttonSet;
	Button buttonDel;
	Switch switchSound;
	Switch switchSoundAfter;
    Switch switchEnable;
	final String LOG_TAG = "myLogs";
	
	int hourStart =0;
	int minutStart = 0;
	int hourStop =0;
	int minutStop = 0;
	int temphour = 0;
	int buttonId=0;
	boolean sound = false;
	boolean soundAfter = false;
    boolean enable = false;
	boolean changeFromTime = false;
	boolean changeToTime = false;
	
	
	DBHelper dbHelper;
	DialogTime dialogtime;
	long id;
	SQLiteDatabase db;
	
	Time timeStart;	
	Time timeStop;

    long timeFrom;
    long timeTo;
    Calendar setTimeFrom = Calendar.getInstance();
    Calendar setTimeTo = Calendar.getInstance();
    String startTime;
    String stopTime;
    SimpleDateFormat sdf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_pane_edit);
		
		buttonFromTime = (Button) findViewById(R.id.button_fromEdit);
		buttonToTime = (Button) findViewById(R.id.button_toEdit);
		buttonFromDate = (Button) findViewById(R.id.buttonFromDate);
		buttonToDate = (Button) findViewById(R.id.buttonToDate);
		buttonSet = (Button) findViewById(R.id.buttonSetTaskEdit);
		buttonDel = (Button) findViewById(R.id.buttonDelete);
		switchSound = (Switch) findViewById(R.id.switch1Edit);
		switchSoundAfter = (Switch) findViewById(R.id.switch2Edit);
        switchEnable = (Switch) findViewById(R.id.switch3Edit);

	    buttonFromDate.setText(getDate());
	    buttonToDate.setText(getDate());

		buttonFromTime.setOnClickListener(new ButtonListener());
		buttonToTime.setOnClickListener(new ButtonListener());
		buttonSet.setOnClickListener(new ButtonListener());
		buttonDel.setOnClickListener(new ButtonListener());
		switchSound.setOnCheckedChangeListener(new switchListener());
		switchSoundAfter.setOnCheckedChangeListener(new switchListener());
        switchEnable.setOnCheckedChangeListener(new switchListener());
		Intent intent = getIntent();
	    
		//row number
		id = intent.getLongExtra("id", 0);
        Log.d(LOG_TAG, "row selected, ID = " + id);
	    dialogtime = new DialogTime();
		
	    dbHelper = new DBHelper(this);
	    db = dbHelper.getWritableDatabase();
	    String sql = "SELECT * FROM mytable WHERE _id=" + String.valueOf(id)+";";
	    Cursor c = db.rawQuery(sql, null);
	    c.moveToFirst();

	        // number of column into query
	      int idColIndex = c.getColumnIndex("_id");
	      int timeStartColIndex = c.getColumnIndex("timeStart");
	      int timeStopColIndex = c.getColumnIndex("timeStop");
	      int soundColIndex = c.getColumnIndex("sound");
	      int soundAfterColIndex = c.getColumnIndex("soundAfter");
          int enableColIndex = c.getColumnIndex("enabled");
          long fromTime = c.getLong(timeStartColIndex);
          long toTime = c.getLong(timeStopColIndex);

          setTimeFrom.setTimeInMillis(fromTime);
          setTimeTo.setTimeInMillis(toTime);

          sdf = new SimpleDateFormat("HH:mm");
          startTime = sdf.format(setTimeFrom.getTime());
          stopTime = sdf.format(setTimeTo.getTime());

	      buttonFromTime.setText(startTime);
	      buttonToTime.setText(stopTime);
          timeFrom = fromTime;
          timeTo = toTime;
	      sound = (c.getInt(soundColIndex) != 0);
	      soundAfter = (c.getInt(soundAfterColIndex) != 0);
          enable = (c.getInt(enableColIndex) != 0);
	      switchSound.setChecked(sound);
	      switchSoundAfter.setChecked(soundAfter);
          switchEnable.setChecked(enable);
	      
		    timeStart = new Time();
		    timeStop = new Time();
	      
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
		getMenuInflater().inflate(R.menu.task_pane_edit, menu);
		return true;
	}
	
	// open TimePicker
		private class ButtonListener implements OnClickListener{	
		@Override

			public void onClick(View v) {
				buttonId = v.getId();
				switch(buttonId){
				case  R.id.button_fromEdit:
				dialogtime.show(getFragmentManager(), "dlg1");
				break;
				case  R.id.button_toEdit:
				dialogtime.show(getFragmentManager(), "dlg1");
				break;
				case  R.id.buttonSetTaskEdit:
					ContentValues cv = new ContentValues();					  
				    // connect to DB

					if(changeFromTime) cv.put("timeStart", timeFrom);
					if(changeToTime) cv.put("timeStop", timeTo);
				    cv.put("sound", sound); 
				    cv.put("soundAfter", soundAfter);
                    cv.put("enabled", enable);

                    long rowID = db.update("mytable", cv, "_id = ?",
				            new String[] { String.valueOf(id) });
				    Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                    Intent intent = new Intent();

                    intent.putExtra("from", timeFrom);
                    intent.putExtra("to", timeTo);
                    intent.putExtra("enable", enable);

                    setResult(RESULT_OK, intent);
                    finish();

				break;	
				case  R.id.buttonDelete:							    			    
				  long rowID1 = db.delete("mytable", "_id = " + id, null);
				  Log.d(LOG_TAG, "row deleted, ID = " + rowID1);
				break;
				}
			}

		}
		
		private class switchListener implements OnCheckedChangeListener{
			@Override
			public void onCheckedChanged(CompoundButton v, boolean isChecked) {
				switch(v.getId()){
				case  R.id.switch1Edit:
					if(isChecked) {
						sound = true;
				    } else {
				    	sound = false;
				    }
				break;
				case  R.id.switch2Edit:
                        if(isChecked) {
                            soundAfter = true;
                        } else {
                            soundAfter = false;
                        }
                        break;
                    case  R.id.switch3Edit:
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
				case  R.id.button_fromEdit:
				{
					hourStart = Hour;
					minutStart = Minut;

                    setTimeFrom.set(Calendar.HOUR_OF_DAY, Hour);
                    setTimeFrom.set(Calendar.MINUTE, Minut);
                    timeFrom = setTimeFrom.getTimeInMillis();
                    startTime = sdf.format(setTimeFrom.getTime());
					buttonFromTime.setText(startTime);
					changeFromTime = true;
				}
				break;
				case R.id.button_toEdit:
				{
					hourStop = Hour;
					minutStop = Minut;

                    setTimeTo.set(Calendar.HOUR_OF_DAY, Hour);
                    setTimeTo.set(Calendar.MINUTE, Minut);
                    timeTo = setTimeTo.getTimeInMillis();
                    stopTime = sdf.format(setTimeTo.getTime());
					buttonToTime.setText(stopTime);
					changeToTime = true;
				}
				break;
			}
		}

}
