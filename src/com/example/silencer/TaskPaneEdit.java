package com.example.silencer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Switch;

import com.example.silencer.DialogTime.TimeDialogListener;
import com.example.silencer.db.DBAdapter;
import com.example.silencer.entity.Rule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskPaneEdit extends Activity implements TimeDialogListener{
	Button buttonFromTime;
	Button buttonToTime;
	Button buttonFromDate;
	Button buttonToDate;
	Button buttonSet;
	Button buttonDel;
    Switch switchEnable;

    CheckBox checkBoxMonday;
    CheckBox checkBoxTuesday;
    CheckBox checkBoxWednesday;
    CheckBox checkBoxThursday;
    CheckBox checkBoxFriday;
    CheckBox checkBoxSaturday;
    CheckBox checkBoxSunday;

    CheckBox checkBoxVibrateEdit;

	final String LOG_TAG = "myLogs";
	
	int hourStart =0;
	int minutStart = 0;
	int hourStop =0;
	int minutStop = 0;
	int buttonId=0;

    String monday = "00000000";
    String tuesday = "00000000";
    String wednesday = "00000000";
    String thursday = "00000000";
    String friday = "00000000";
    String saturday = "00000000";
    String sunday = "00000000";
    String notCheckedDay = "00000000";

    int binaryMonday;
    int binaryTuesday;
    int binaryWednesday;
    int binaryThursday;
    int binaryFriday;
    int binarySaturday;
    int binarySunday;
    int result;

    int id;
    int enable = 0;
	boolean changeFromTime = false;
	boolean changeToTime = false;
    int vibrate = 0;

	DialogTime dialogtime;

    DBAdapter dbAdapter;
	
	Time timeStart;	
	Time timeStop;

    long timeFrom;
    long timeTo;
    Calendar setTimeFrom = Calendar.getInstance();
    Calendar setTimeTo = Calendar.getInstance();
    String startTime;
    String stopTime;
    SimpleDateFormat sdf;

    Rule rule;

    Context context;
	
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
        switchEnable = (Switch) findViewById(R.id.switch3Edit);

	    buttonFromDate.setText(getDate());
	    buttonToDate.setText(getDate());

		buttonFromTime.setOnClickListener(new ButtonListener());
		buttonToTime.setOnClickListener(new ButtonListener());
		buttonSet.setOnClickListener(new ButtonListener());
		buttonDel.setOnClickListener(new ButtonListener());
        switchEnable.setOnCheckedChangeListener(new switchListener());

        checkBoxMonday = (CheckBox) findViewById(R.id.checkBoxMondayEdit);
        checkBoxTuesday = (CheckBox) findViewById(R.id.checkBoxTuesdayEdit);
        checkBoxWednesday = (CheckBox) findViewById(R.id.checkBoxWednesdayEdit);
        checkBoxThursday = (CheckBox) findViewById(R.id.checkBoxThursdayEdit);
        checkBoxFriday = (CheckBox) findViewById(R.id.checkBoxFridayEdit);
        checkBoxSaturday = (CheckBox) findViewById(R.id.checkBoxSaturdayEdit);
        checkBoxSunday = (CheckBox) findViewById(R.id.checkBoxSundayEdit);

        checkBoxVibrateEdit = (CheckBox) findViewById(R.id.checkBoxVibrateEdit);

        checkBoxMonday.setOnCheckedChangeListener(new switchListener());
        checkBoxTuesday.setOnCheckedChangeListener(new switchListener());
        checkBoxWednesday.setOnCheckedChangeListener(new switchListener());
        checkBoxThursday.setOnCheckedChangeListener(new switchListener());
        checkBoxFriday.setOnCheckedChangeListener(new switchListener());
        checkBoxSaturday.setOnCheckedChangeListener(new switchListener());
        checkBoxSunday.setOnCheckedChangeListener(new switchListener());

        checkBoxVibrateEdit.setOnCheckedChangeListener(new switchListener());

		Intent intent = getIntent();
        context = getApplicationContext();
		//row number
        id = intent.getIntExtra("id", 0);
        Log.d(LOG_TAG, "row selected, ID = " + id);
	    dialogtime = new DialogTime();

        dbAdapter = new DBAdapter(getApplicationContext());
        rule = dbAdapter.getRule(id);

        long fromTime = rule.getStartTime();
        long toTime = rule.getStopTime();
        int enable = rule.getEnabled();
        int vibrate = rule.getVibrate();
        int days = rule.getDays();

        //get week days
        ArrayList<Integer> week = new Utils(context).getWeek(days);

        if (week.size() !=0){
            for (int i = 0; i < week.size(); i++){
                switch(week.get(i)){
                    case 1:
                        checkBoxMonday.setChecked(true);
                    break;
                    case 2:
                        checkBoxTuesday.setChecked(true);
                    break;
                    case 3:
                        checkBoxWednesday.setChecked(true);
                    break;
                    case 4:
                        checkBoxThursday.setChecked(true);
                    break;
                    case 5:
                        checkBoxFriday.setChecked(true);
                        break;
                    case 6:
                        checkBoxSaturday.setChecked(true);
                    break;
                    case 7:
                        checkBoxSunday.setChecked(true);
                    break;
                }
            }
        }

          setTimeFrom.setTimeInMillis(fromTime);
          setTimeTo.setTimeInMillis(toTime);

          sdf = new SimpleDateFormat("HH:mm");
          startTime = sdf.format(setTimeFrom.getTime());
          stopTime = sdf.format(setTimeTo.getTime());

	      buttonFromTime.setText(startTime);
	      buttonToTime.setText(stopTime);
          timeFrom = fromTime;
          timeTo = toTime;
          switchEnable.setChecked(enable != 0);
          checkBoxVibrateEdit.setChecked(vibrate != 0);
	      
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
                    binaryMonday = Integer.parseInt(monday,2);
                    binaryTuesday = Integer.parseInt(tuesday,2);
                    binaryWednesday = Integer.parseInt(wednesday,2);
                    binaryThursday = Integer.parseInt(thursday,2);
                    binaryFriday = Integer.parseInt(friday,2);
                    binarySaturday = Integer.parseInt(saturday,2);
                    binarySunday = Integer.parseInt(sunday,2);

                    //total

                    result = binaryMonday + binaryTuesday + binaryWednesday + binaryThursday + binaryFriday
                            + binarySaturday + binarySunday;

                    rule = new Rule();

                    rule.setStartTime(timeFrom);
                    rule.setStopTime(timeTo);
                    rule.setDays(result);
                    rule.setEnable(enable);
                    rule.setVibrate(vibrate);

                    dbAdapter.updateRow(id, rule);
                    finish();

				break;	
				case  R.id.buttonDelete:
                    dbAdapter.deleteRow(id);
                    finish();
				break;
				}
			}

		}

        private void startServ(int id){
            Intent serviceIntent = new Intent(context, MyService.class);
            serviceIntent.putExtra("id", id);
            context.startService(serviceIntent);
        }
		
		private class switchListener implements OnCheckedChangeListener{
			@Override
			public void onCheckedChanged(CompoundButton v, boolean isChecked) {
				switch(v.getId()){
                    case  R.id.switch3Edit:
                        if(isChecked) {
                            enable = 1;
                        } else {
                            enable = 0;
                        }
                        break;
                    case  R.id.checkBoxVibrateEdit:
                        if(isChecked) {
                            vibrate = 1;
                        } else {
                            vibrate = 0;
                        }
                        break;
                    case R.id.checkBoxMondayEdit:
                        if (isChecked) {
                            monday = "00000001";
                        } else {
                            monday = notCheckedDay;
                        }
                        break;
                    case R.id.checkBoxTuesdayEdit:
                        if (isChecked) {
                            tuesday = "00000010";
                        } else {
                            tuesday = notCheckedDay;
                        }
                        break;
                    case R.id.checkBoxWednesdayEdit:
                        if (isChecked) {
                            wednesday = "00000100";
                        } else {
                            wednesday = notCheckedDay;
                        }
                        break;
                    case R.id.checkBoxThursdayEdit:
                        if (isChecked) {
                            thursday = "00001000";
                        } else {
                            thursday = notCheckedDay;
                        }
                        break;
                    case R.id.checkBoxFridayEdit:
                        if (isChecked) {
                            friday = "00010000";
                        } else {
                            friday = notCheckedDay;
                        }
                        break;
                    case R.id.checkBoxSaturdayEdit:
                        if (isChecked) {
                            saturday = "00100000";
                        } else {
                            saturday = notCheckedDay;
                        }
                        break;
                    case R.id.checkBoxSundayEdit:
                        if (isChecked) {
                            sunday = "01000000";
                        } else {
                            sunday = notCheckedDay;
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
                    setTimeFrom.set(Calendar.SECOND, 0);
                    setTimeFrom.set(Calendar.MILLISECOND, 0);
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
