package com.example.silencer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

public class MainActivity extends Activity implements OnClickListener{
	TextView clock;
	Button btn_add;
	Button viewList;
	ListView myList;
	TaskPane taskPane;
	TextView from;
    Intent intentEdit;

	DBAdapter myDb;
	SimpleCursorAdapter myCursorAdapter;
	final String LOG_TAG = "myLogs";
	
	int selectedID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		clock = (TextView) findViewById(R.id.clock);
		clock.setText(getTime());
		btn_add = (Button) findViewById(R.id.button_add);
		btn_add.setOnClickListener(this);
		viewList = (Button) findViewById(R.id.buttonV);
		viewList.setOnClickListener(this);
		myList = (ListView) findViewById(R.id.listView1);
		myList.setOnItemClickListener(new listItemListener());
        intentEdit = new Intent(this, TaskPaneEdit.class);
		
		openDB();
		
		Cursor cursor = myDb.getAllRows();

		startManagingCursor(cursor);
		String[] fromFieldNames = new String[] 
				{DBAdapter.KEY_FROM_TIME, DBAdapter.KEY_TO_TIME, DBAdapter.KEY_SOUND, DBAdapter.KEY_SOUND_AFTER};
		int[] toViewIDs = new int[]
				{R.id.textViewFrom,     R.id.textViewTo,           R.id.textViewSound,     R.id.textViewSoundAfter};
		
		myCursorAdapter = new MyCursorAdapter(
						this,					// Context
						R.layout.item_layout,	// Row layout template
						cursor,					// cursor (set of DB records to map)
						fromFieldNames,			// DB Column names
						toViewIDs				// View IDs to put information in
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
		// TODO Auto-generated method stub
		switch(v.getId()){
		case  R.id.button_add:
			Intent intent = new Intent(this, TaskPane.class);
			startActivity(intent);
		break;
		case  R.id.buttonV:
			Intent intentEdit = new Intent(this, TaskPaneEdit.class);
			intentEdit.putExtra("id", selectedID);
			startActivity(intentEdit);
			//fromDbToList();
			
		break;
		}

	}
	
	private class listItemListener implements OnItemClickListener{

		
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			//Get selected item ID
			from = (TextView) v.findViewById(R.id.textViewFrom);
			selectedID = position;
            intentEdit.putExtra("id", id);
            startActivity(intentEdit);
            Log.d(LOG_TAG, "row inserted, ID = " + id);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}

		
		
	}
	  
}
