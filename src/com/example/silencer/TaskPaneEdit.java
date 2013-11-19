package com.example.silencer;

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
	final String LOG_TAG = "myLogs";
	
	int hourStart =0;
	int minutStart = 0;
	int hourStop =0;
	int minutStop = 0;
	int temphour = 0;
	int buttonId=0;
	boolean sound = false;
	boolean soundAfter = false;
	boolean changeFromTime = false;
	boolean changeToTime = false;
	
	
	DBHelper dbHelper;
	DialogTime dialogtime;
	long id;
	SQLiteDatabase db;
	
	Time timeStart;	
	Time timeStop;	
	
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
	    buttonFromDate.setText(getDate());
	    buttonToDate.setText(getDate());
		buttonToDate.setText(getDate());	
		buttonFromTime.setOnClickListener(new ButtonListener());
		buttonToTime.setOnClickListener(new ButtonListener());
		buttonSet.setOnClickListener(new ButtonListener());
		buttonDel.setOnClickListener(new ButtonListener());
		switchSound.setOnCheckedChangeListener(new switchListener());
		switchSoundAfter.setOnCheckedChangeListener(new switchListener());
		
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
	      buttonFromTime.setText(c.getString(timeStartColIndex));
	      buttonToTime.setText(c.getString(timeStopColIndex));

	      sound = (c.getInt(soundColIndex) != 0);
	      soundAfter = (c.getInt(soundAfterColIndex) != 0);
	      
	      switchSound.setChecked(sound);
	      switchSoundAfter.setChecked(soundAfter);
	      
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

					if(changeFromTime) cv.put("timeStart", timeStart.format("%k:%M"));
					if(changeToTime) cv.put("timeStop", timeStop.format("%k:%M"));
				    cv.put("sound", sound); 
				    cv.put("soundAfter", soundAfter); 			    
				    
				    long rowID = db.update("mytable", cv, "_id = ?",
				            new String[] { String.valueOf(id) });
				  Log.d(LOG_TAG, "row inserted, ID = " + rowID);
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
					timeStart.set(0, minutStart, hourStart, 0, 0, 0);
					buttonFromTime.setText(timeStart.format("%k:%M"));
					changeFromTime = true;
				}
				break;
				case R.id.button_toEdit:
				{
					hourStop = Hour;
					minutStop = Minut;
					timeStop.set(0, minutStop, hourStop, 0, 0, 0);
					buttonToTime.setText(timeStop.format("%k:%M"));
					changeToTime = true;
				}
				break;
			}
		}

}
