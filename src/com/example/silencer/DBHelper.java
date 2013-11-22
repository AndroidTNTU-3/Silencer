package com.example.silencer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	
	final String LOG_TAG = "myLogs";
	public static final String DATABASE_NAME = "taskDB";
	public static final String DATABASE_TABLE = "mytable";
	public static final int DATABASE_VERSION = 1;	
	
	public static final String KEY_ROWID = "_id";
	
	private SQLiteDatabase db;
	
	public DBHelper(Context context) {
	      super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }

	    @Override
	    public void onCreate(SQLiteDatabase db) {
	      Log.d(LOG_TAG, "--- onCreate database ---");
	      
	      // create table
	      /*db.execSQL("create table mytable ("
	          + "id integer primary key autoincrement," 
	          + "hourStart int,"
	          + "minutStart int,"
	          + "hourStop int,"
	          + "minutStop int,"
	          + "sound boolean,"
	          + "soundAfter boolean" + ");");*/
	     db.execSQL("create table " + DATABASE_TABLE + " ( "
          + KEY_ROWID + " integer primary key autoincrement," 
          + "timeStart long,"
          + "timeStop long,"
          + "sound boolean,"
          + "soundAfter boolean,"
          + "enabled boolean" + ");");
	    }
	    
	    
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	    }
	    
}
