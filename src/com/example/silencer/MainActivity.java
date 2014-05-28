package com.example.silencer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.silencer.db.DBAdapter;
import com.example.silencer.db.DBHelper;

public class MainActivity extends Activity implements OnClickListener{
    DigitalClock clock;
	Button btn_add;
	ListView myList;

    DBAdapter dbAdapter;
	SimpleCursorAdapter myCursorAdapter;

    Intent intentEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		clock = (DigitalClock) findViewById(R.id.digitalClock);
		btn_add = (Button) findViewById(R.id.button_add);
		btn_add.setOnClickListener(this);

		myList = (ListView) findViewById(R.id.listView1);
        myList.setFocusable(true);

        dbAdapter = new DBAdapter(getApplicationContext());

		Cursor cursor = dbAdapter.getAllRows();
        intentEdit = new Intent(this, TaskPaneEdit.class);
        startManagingCursor(cursor);
		String[] fromFieldNames = new String[] 
				{DBHelper.Table.COLUMN_NAME_TIME_START, DBHelper.Table.COLUMN_NAME_TIME_STOP,DBHelper.Table.COLUMN_NAME_ENABLED};
		int[] toViewIDs = new int[]
				{R.id.textViewFrom, R.id.textViewTo};

		myCursorAdapter = new MyCursorAdapter(
						this,					// Context
						R.layout.item_layout,	// Row layout template
						cursor,					// cursor (set of DB records to map)
						fromFieldNames,			// DB Column names
						toViewIDs				// View IDs to put information in
						);		
		myList.setAdapter(myCursorAdapter);
        myCursorAdapter.swapCursor(cursor);

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
		switch(v.getId()){
		case  R.id.button_add:
			Intent intent = new Intent(this, TaskPane.class);

		break;
		}

	}

    @Override
    protected void onResume() {
        super.onResume();
       // myCursorAdapter.notifyDataSetInvalidated();
        myCursorAdapter.notifyDataSetChanged();
        myList.invalidateViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbAdapter.closeDB();
    }
}
