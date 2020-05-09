package com.example.application_for_children;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyService extends Service {
    NotificationManager notificationManager;
    private static final String CHANNEL_ID = "MyNotifiction";
    Integer Status = 0;
    final String LOG_TAG = "myLogs";
    int StartId;
    int PARENT_ID;
    LocationListener locationListener;
    LocationManager locationManager;
    SharedPreferences sharedPreferences;
    String answerHTTP;
    private String SERV_URL = "http://192.168.0.174:8080/person/getperson?id=3";
    //"http://192.168.0.174:8080/";


    public void onCreate() {
        super.onCreate();

        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(LOG_TAG, "memory");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        StartId = startId;
        Log.d(LOG_TAG, "onStartCommand");
        notificationManager = getSystemService(NotificationManager.class);


        if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            locationManager.removeUpdates(locationListener);
            stopForeground(true);
            stopSelf(startId); //должен удалять сервис
        } else if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {


            Intent notiIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notiIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("My attempt")
                    .setContentText("Coordinates")
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(startId, notification);

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My service", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);

            Log.d(LOG_TAG, "start someTask");

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    Log.d(LOG_TAG, "1");
                    if (location.getProvider().equals(
                            LocationManager.NETWORK_PROVIDER))
                        Status += 1;


                    showLocation(location);
                    new MyAsyncTask(location).execute("");

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
                    Log.d(LOG_TAG, "4");
                }
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListener);


        }
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
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
        if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            Log.d(LOG_TAG, "status = " + Status + "\n" + formatLocation(location));
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


    class MyAsyncTask extends AsyncTask<String, String, String> {

        Double latitude, longitude;
        String folderName;
        SimpleDateFormat formatter;
        SharedPreferences sharedPreferences;
        Location location;
        int PARENT_ID;


        public MyAsyncTask(Location location) {
            this.location = location;
        }

        @Override
        protected String doInBackground(String... params) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            /**RequestBody requsestBody = new MultipartBody.Builder()
             .setType(MultipartBody.FORM)
             .addFormDataPart("lastname", lastnameS)
             .addFormDataPart("firstname", firstnameS)
             .build();
             **/
            OkHttpClient client = new OkHttpClient();
            /**Request request = new Request.Builder()
             .url(SERV_URL)
             //.post(requsestBody)
             .build();
             **/
            MediaType mediaType = MediaType.parse("text/plain");
            formatter = new SimpleDateFormat("YYYY.MM.dd");
            //или new SimpleDateFormat("hh:mm:ss");
            // new SimpleDateFormat("dd.mm.yyyy HH:mm:ss a");
            sharedPreferences=getSharedPreferences("preference",MODE_PRIVATE);
            int ID=sharedPreferences.getInt("parent_id",MODE_PRIVATE);
            folderName = formatter.format(new Date(location.getTime()));
            RequestBody body = RequestBody.create("{\n\t\"idperson\":" + ID + ",\n\t\"latitude\":" + latitude + ",\n\t\"longitude\":" + longitude + ",\n\t\"date\":" + folderName + ",\n\t\"time\":" + location.getTime() + "\n}", mediaType);
            Request request = new Request.Builder()
                    .url("http://192.168.0.175:8080/location/addLocation")
                    .method("PUT", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();
            Response response = null;// client.newCall(request).execute();

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

                if (response != null && response.code() == 200) {
                    try {
                        answerHTTP = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
        }

        @Override
        protected void onPostExecute (String result){
            super.onPostExecute(result);
            //Log.d(LOG_TAG,answerHTTP);
            //lastnameF.setText(answerHTTP);
        }

    }
}