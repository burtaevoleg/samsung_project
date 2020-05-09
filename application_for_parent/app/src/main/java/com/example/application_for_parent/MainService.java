package com.example.application_for_parent;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainService extends AppCompatActivity {

    Button start;
    Button stop;
    final String LOG_TAG = "myLogs";
    SQLiteDatabase database;
    DBHelper dbHelper;

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_service);
        final String LOG_TAG = "myLogs";

        start=(Button)findViewById(R.id.start);
        stop=(Button)findViewById(R.id.stop);

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.start:

                        if (!isMyServiceRunning(MyService.class)) {
                            Log.d(LOG_TAG,"start Service");
                            startService(new Intent(MainService.this, MyService.class).setAction(Constants.ACTION.STARTFOREGROUND_ACTION));
                            //startService(new Intent(MainService.this,MyService.class));
                            //  }
                            //};
                            // t.start();
                        }
                        break;
                    case R.id.stop:
                        if(isMyServiceRunning(MyService.class)) {
                            Log.d(LOG_TAG,"stop Service");
                            //stopService(new Intent(MainService.this, MyService.class).setAction(Constants.ACTION.STOPFOREGROUND_ACTION));
                            startService(new Intent(MainService.this,MyService.class).setAction(Constants.ACTION.STOPFOREGROUND_ACTION));
                        }
                        break;
                }
            }
        };

        stop.setOnClickListener(listener);
        start.setOnClickListener(listener);
    }









/**
 public class DBHelper extends SQLiteOpenHelper {

 public static final int DATABASE_VERSION=1;
 public static final String DATABASE_NAME="projectDB";
 public static final String TABLE_CONTACTS="coordinates";

 public static final String KEY_ID="_id";
 public static final String KEY_Lat="lat";
 public static final String KEY_Lon="lon";

 public DBHelper(@Nullable Context context, @Nullable String name, int version) {
 super(context, DATABASE_NAME,null, DATABASE_VERSION);
 }

 @Override
 public void onCreate(SQLiteDatabase db) {
 db.execSQL("create table " + TABLE_CONTACTS + "(" + KEY_ID + " integer primary key,"
 + KEY_Lat + " real," + KEY_Lon + " real" + ")" );


 }

 @Override
 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 db.execSQL("drop table if exists "+ TABLE_CONTACTS);

 onCreate(db);
 }
 }
 **/

}
