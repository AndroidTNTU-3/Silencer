package com.example.silencer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBAdapter {
	
	
	public static final String KEY_FROM_TIME = "timeStart";
	public static final String KEY_TO_TIME = "timeStop";
	public static final String KEY_DATE = "date";
    public static final String KEY_ENABLED = "enabled";
	
	public static final String DATABASE_NAME = "taskDB";
	public static final String DATABASE_TABLE = "mytable";
	public static final int DATABASE_VERSION = 1;
	
	public static final String KEY_ROWID = "_id";
	public static final int COL_ROWID = 0;
	
	private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    final static String LOG_TAG = "myLogs";
	
	public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_FROM_TIME, KEY_TO_TIME, KEY_DATE, KEY_ENABLED};

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE + " ( "
                    + KEY_ROWID + " integer primary key autoincrement,"
                    + "timeStart long,"
                    + "timeStop long,"
                    + "date int,"
                    + "enabled boolean" + ");";
	
	public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
	}
	
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		myDBHelper.close();
	}

    public long insertRow(long timeStart, long timeStop, int date, boolean enabler) {

        // Create row's data:

        ContentValues initialValues = new ContentValues();
        initialValues.put("timeStart", timeStart);
        initialValues.put("timeStop", timeStop);
        initialValues.put("date", date);
        initialValues.put("enabled", enabler);


        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
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

    public Cursor getRowQuery(String _query) {
        String query = _query;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public boolean updateRow(long rowId, long timeStart, long timeStop, int date, int enable) {
        String where = KEY_ROWID + "=" + rowId;

        // Create row's data:
        ContentValues newValues = new ContentValues();

        newValues.put("timeStart", timeStart);
        newValues.put("timeStop", timeStop);
        newValues.put("date", date);
        newValues.put("enabled", enable);
        long rowID = db.update(DATABASE_TABLE, newValues, "_id = ?",
                new String[] { String.valueOf(rowId) });
        Log.d(LOG_TAG, "row inserted, ID = " + rowID);

        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(LOG_TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
