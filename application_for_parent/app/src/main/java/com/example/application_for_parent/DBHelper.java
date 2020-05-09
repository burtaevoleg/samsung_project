package com.example.application_for_parent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION=6;
    public static final String DATABASE_NAME="projectDB";
    public static final String TABLE_CONTACTS="coordinatess";

    public static final String KEY_ID="_id";
    public static final String KEY_Lat="lat";
    public static final String KEY_Lon="lon";
    public static final String KEY_Time="time";
    final String LOG_TAG = "myLogs";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CONTACTS + "(" + KEY_ID + " integer primary key,"
                + KEY_Lat + " text," + KEY_Lon + " text," + KEY_Time +" text"+ ")" );

        Log.d(LOG_TAG,"create data");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+ TABLE_CONTACTS);

        onCreate(db);
    }
}