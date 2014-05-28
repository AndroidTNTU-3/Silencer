package com.example.silencer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.silencer.entity.Rule;

public class DBAdapter {

    private SQLiteDatabase db;

    final static String LOG_TAG = "myLogs";
    DBHelper openHelper;

    public DBAdapter(Context context) {
        openHelper = DBHelper.getInstance(context);
        db = openHelper.getWritableDatabase();
    }
	
	public void close() {
		db.close();
	}

    public long insertRow(Rule rule){

        ContentValues values = getValues(rule);
        return db.insert(DBHelper.DATABASE_TABLE, null, values);

    }

    public boolean updateRow(int rowId, Rule rule){

        String where = DBHelper.Table._ID + "=" + rowId;
        ContentValues newValues = getValues(rule);

        // Insert it into the database.
        return db.update(DBHelper.DATABASE_TABLE, newValues, where, null) != 0;

    }

    private ContentValues getValues(Rule rule) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.Table.COLUMN_NAME_TIME_START, rule.getStartTime());
        values.put(DBHelper.Table.COLUMN_NAME_TIME_STOP, rule.getStopTime());
        values.put(DBHelper.Table.COLUMN_NAME_DAYS, rule.getDays());
        values.put(DBHelper.Table.COLUMN_NAME_VIBRATE, rule.getVibrate());
        values.put(DBHelper.Table.COLUMN_NAME_ENABLED, rule.getEnabled());
        return values;
    }

	
	public Cursor getAllRows() {
		String where = null;
		Cursor c = 	db.query(true, DBHelper.DATABASE_TABLE, null,
							where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

    public Rule getRule(int id) {

        String where = DBHelper.Table._ID + "=" + id;
        Rule rule = new Rule();
        Cursor cursor = db.query(true, DBHelper.DATABASE_TABLE, null,
                where, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            rule.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.Table._ID)));
            rule.setStartTime(cursor.getLong(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_TIME_START )));
            rule.setStopTime(cursor.getLong(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_TIME_STOP)));
            rule.setDays(cursor.getInt(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_DAYS)));
            rule.setEnable(cursor.getInt(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_ENABLED )));
            rule.setVibrate(cursor.getInt(cursor.getColumnIndex( DBHelper.Table.COLUMN_NAME_VIBRATE )));
        }

        if (cursor != null) cursor.close();
        return rule;

    }

    public Cursor getRowQuery(String _query) {
        String query = _query;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(int rowId) {
        String where = DBHelper.Table._ID + "=" + rowId;
        return db.delete(DBHelper.DATABASE_TABLE, where, null) != 0;
    }

    public void closeDB(){
        openHelper.close();
    }
}
