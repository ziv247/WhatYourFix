package com.exmp.ziv24.mapsproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String cmd = "CREATE TABLE " + Constants.TABLE_NAME + " (_id INTEGER PRIMARY KEY, " +
                    Constants.COLUMN_NAME_PLACE_JSON + " TEXT);";

            db.execSQL(cmd);
        } catch (SQLiteException e) {
            e.getCause();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
