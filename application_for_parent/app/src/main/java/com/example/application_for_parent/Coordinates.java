package com.example.application_for_parent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.Date;

public class Coordinates extends AppCompatActivity {

    Double LON;
    Double LAT;
    Long Time;
    String time;
    TextView tvEnabledGPS;
    TextView tvStatusGPS;
    TextView tvLocationGPS;
    TextView tvEnabledNet;
    TextView tvStatusNet;
    TextView tvLocationNet;
    Integer Status=0;
    DBHelper dbHelper;
    ContentValues contentValues;
    SQLiteDatabase database;
    final String LOG_TAG="myLogs";

    private String MAPKIT_API="4e300306-7520-48c4-8380-38d1599db7c0";
    MapView mapView;
    private final Point TARGET_LOCATION = new Point(59.945933, 30.320045);

    private LocationManager locationManager;

    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(MAPKIT_API);
        MapKitFactory.initialize(this);


        setContentView(R.layout.activity_coordinates);
        mapView=(MapView)findViewById(R.id.mapview);

        /**tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
         tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
         tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
         **/
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null);


        dbHelper=new DBHelper(this);
        database=dbHelper.getWritableDatabase();
        contentValues=new ContentValues();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        //          1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location.getProvider().equals(
                    LocationManager.NETWORK_PROVIDER))
                Status+=1;

            LON=location.getLongitude();
            LAT=location.getLatitude();
            Time=location.getTime(); // время в милисекундах

            Log.d(LOG_TAG, String.valueOf(Time));


            SimpleDateFormat formatter = new SimpleDateFormat("YYYY.MM.dd");
            //или new SimpleDateFormat("hh:mm:ss");
            // new SimpleDateFormat("dd.mm.yyyy HH:mm:ss a");
            String folderName = formatter.format(new Date(location.getTime()));

            Date date = new Date();

// час в текущей временной зоне
            Log.d(LOG_TAG, String.valueOf(date.getHours()));

            Date time1=new Date(location.getTime());
            Log.d(LOG_TAG, String.valueOf(time1));
            //time=Time.toString();
            //=String.format("%3$tF %3$tT", new Date(location.getTime()));
            contentValues.put(DBHelper.KEY_Lat,LAT);
            contentValues.put(DBHelper.KEY_Lon,LON);
            contentValues.put(DBHelper.KEY_Time,folderName);
            database.insert(DBHelper.TABLE_CONTACTS,null,contentValues);
            show(database);
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(Coordinates.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Coordinates.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider,int status, Bundle extras) {
            /**if (provider.equals(LocationManager.GPS_PROVIDER)) {
             tvStatusGPS.setText("Status: " + String.valueOf(Status));
             } else**/ if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(Status));
            }
        }
    };

    private void show(SQLiteDatabase database) {
        Cursor cursor=database.query(DBHelper.TABLE_CONTACTS,null,null,null,null,null,null);

        if (cursor.moveToFirst()){
            int idColIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int latColIndex = cursor.getColumnIndex(DBHelper.KEY_Lat);
            int lonColIndex = cursor.getColumnIndex(DBHelper.KEY_Lon);
            int timeColIndex = cursor.getColumnIndex(DBHelper.KEY_Time);
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG,
                        "ID = " + cursor.getInt(idColIndex) +
                                ", lon = " + cursor.getString(lonColIndex) +
                                ", lat = " + cursor.getString(latColIndex) +", time = "+ cursor.getString(timeColIndex));

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (cursor.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        cursor.close();

    }

    private void showLocation(Location location) {
        if (location == null) {
            return;
        }
        if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
            tvStatusNet.setText("Status: " + String.valueOf(Status));
        }
        // else if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
        //         tvLocationGPS.setText(formatLocation(location));
        // }

        mapView.getMap().move(
                new CameraPosition(new Point(location.getLatitude(),location.getLongitude()), 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null);
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        mapObjects.clear();
        Point resultLocation = new Point(location.getLatitude(),location.getLongitude());
        mapObjects.addPlacemark(resultLocation,
                ImageProvider.fromResource(this, R.drawable.search_result));

    }
    @Override
    protected void onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @SuppressLint("DefaultLocale")
    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    @SuppressLint("SetTextI18n")
    private void checkEnabled() {
        /**  tvEnabledGPS.setText("Enabled: "
         + locationManager
         .isProviderEnabled(LocationManager.GPS_PROVIDER));
         **/
        tvEnabledNet.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

}
