package com.example.silencer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

    private static DBHelper mInstance = null;
	
	final String LOG_TAG = "myLogs";
	public static final String DATABASE_NAME = "taskDB";
	public static final String DATABASE_TABLE = "mytable";
	public static final int DATABASE_VERSION = 1;	
	
	public static final String KEY_ROWID = "_id";
	
	private SQLiteDatabase db;

    public static abstract class Table implements BaseColumns {

        public static final String COLUMN_NAME_TIME_START = "timeStart";
        public static final String COLUMN_NAME_TIME_STOP = "timeStop";
        public static final String COLUMN_NAME_DAYS = "days";
        public static final String COLUMN_NAME_VIBRATE = "vibrate";
        public static final String COLUMN_NAME_ENABLED = "enabled";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE
                + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME_TIME_START + " LONG," + COLUMN_NAME_TIME_STOP + " LONG, "
                + COLUMN_NAME_DAYS + " INT, " + COLUMN_NAME_VIBRATE + " BOOLEAN, " + COLUMN_NAME_ENABLED
                + " INT);";
    }

    public static DBHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

	
	private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	    @Override
	    public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            db.execSQL(Table.CREATE_TABLE);
        }
	    
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Table.CREATE_TABLE);
	    }
	    
}
