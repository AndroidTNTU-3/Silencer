package com.example.silencer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
	
	
	public static final String KEY_FROM_TIME = "timeStart";
	public static final String KEY_TO_TIME = "timeStop";
	public static final String KEY_SOUND = "sound";
	public static final String KEY_SOUND_AFTER = "soundAfter";
	
	public static final String DATABASE_NAME = "taskDB";
	public static final String DATABASE_TABLE = "mytable";
	public static final int DATABASE_VERSION = 1;
	
	public static final String KEY_ROWID = "_id";
	public static final int COL_ROWID = 0;
	
	private final Context context;
	
	private DBHelper myDBHelper;
	private SQLiteDatabase db;
	
	public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_FROM_TIME, KEY_TO_TIME, KEY_SOUND, KEY_SOUND_AFTER};
	
	public DBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DBHelper(context);
	}
	
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		myDBHelper.close();
	}
	
	public Cursor getAllRows() {
		String where = null;
		Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, 
							where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
}
