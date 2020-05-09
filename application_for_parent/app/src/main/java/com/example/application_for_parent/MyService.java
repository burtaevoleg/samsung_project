package com.example.application_for_parent;


import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.util.Date;

public class MyService extends Service {
    NotificationManager notificationManager;
    private static final String CHANNEL_ID="MyNotifiction";
    Integer Status = 0;
    final String LOG_TAG = "myLogs";
    int StartId;
    Double LON;
    Double LAT;

    DBHelper dbHelper;
    ContentValues contentValues;
    SQLiteDatabase database;


    LocationListener locationListener;
    LocationManager locationManager;


    public void onCreate() {
        super.onCreate();

        // класс используется для добавления новых строк
        //MainService mainService=new MainService();
        dbHelper=new DBHelper(this);
        database=dbHelper.getWritableDatabase();

        contentValues=new ContentValues();

        // SQLiteDatabase database=dbHelper.getWritableDatabase(); // создаем объект для управления базой данных
// открываем и возвращаем экземпляр баз данных
        // ContentValues contentValues=new ContentValues(); // класс используется для добавления новых строк


        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(LOG_TAG,"memory");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        StartId=startId;
        Log.d(LOG_TAG, "onStartCommand");
        notificationManager = getSystemService(NotificationManager.class);

        if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            locationManager.removeUpdates(locationListener);
            stopForeground(true);
            stopSelf(startId); //должен удалять сервис
        } else if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {

            try {
                Intent notiIntent = new Intent(this, MainService.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notiIntent, 0);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("My attempt")
                        .setContentText("Coordinates")
                        .setContentIntent(pendingIntent)
                        .build();

                startForeground(startId, notification);

                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My service", NotificationManager.IMPORTANCE_DEFAULT);
                //NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);

                Log.d(LOG_TAG,"start someTask");

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                locationListener = new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d(LOG_TAG,"1");
                        if (location.getProvider().equals(
                                LocationManager.NETWORK_PROVIDER))
                            Status += 1;

                        showLocation(location);
                        LON = location.getLongitude();
                        LAT = location.getLatitude();
                        contentValues.put(DBHelper.KEY_Lat, LAT);
                        contentValues.put(DBHelper.KEY_Lon, LON);
                        database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);

                        show(database);


                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.d(LOG_TAG,"2");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d(LOG_TAG,"3");
                        if (ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        showLocation(locationManager.getLastKnownLocation(provider));
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d(LOG_TAG,"4");

                    }
                };

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0,
                        locationListener);

                //someTask();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
        //Toast.makeText(this, "onTaskRemoved", Toast.LENGTH_SHORT).show();
        //}
        // someTask();

        /**
         Intent activityIntent = new Intent(this, MainActivity.class);
         PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
         activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

         // This always shows up in the notifications area when this Service is running.
         // TODO: String localization
         Notification not = new Notification.Builder(this).
         setContentTitle(getText(R.string.app_name)).
         setContentInfo("Doing stuff in the background...").setSmallIcon(R.mipmap.ic_launcher).
         setContentIntent(pendingIntent).build();
         startForeground(1, not);
         **/

        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }


    String someTask() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        Log.d(LOG_TAG,"111111111111111111");
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (location.getProvider().equals(
                        LocationManager.NETWORK_PROVIDER))
                    Status += 1;

                showLocation(location);
                /** LON = location.getLongitude();
                 LAT = location.getLatitude();
                 contentValues.put(DBHelper.KEY_Lat, LAT);
                 contentValues.put(DBHelper.KEY_Lon, LON);
                 database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);

                 show(database);
                 **/

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                if (ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                showLocation(locationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);
        return "end";
    }

    private void show(SQLiteDatabase database) {
        Cursor cursor=database.query(DBHelper.TABLE_CONTACTS,null,null,null,null,null,null);

        if (cursor.moveToFirst()){
            int idColIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int latColIndex = cursor.getColumnIndex(DBHelper.KEY_Lat);
            int lonColIndex = cursor.getColumnIndex(DBHelper.KEY_Lon);
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG,
                        "ID = " + cursor.getInt(idColIndex) +
                                ", lon = " + cursor.getString(lonColIndex) +
                                ", lat = " + cursor.getString(latColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (cursor.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        cursor.close();
    }


    public String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void showLocation(Location location) {
        if (location == null) {
            return;
        }
        // database=dbHelper.getWritableDatabase();
        // dbHelper.getWritableDatabase(); // создаем объект для управления базой данных
// открываем и возвращаем экземпляр баз данных

        if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            Log.d(LOG_TAG, "status = " + Status+"\n"+ formatLocation(location));
        }
        // else if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
        //         tvLocationGPS.setText(formatLocation(location));
        // }
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

}
