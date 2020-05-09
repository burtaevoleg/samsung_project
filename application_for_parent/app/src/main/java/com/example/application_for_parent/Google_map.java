package com.example.application_for_parent;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class Google_map extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    GoogleMap googleMap;
    MapView mapView;
    ArrayList<LatLng> list;
    DBHelper dbHelper;
    Button show,clear;
    SQLiteDatabase database;


    final String LOG_TAG="myLogs";
    //координаты для маркера
    private static final double TARGET_LATITUDE = 17.893366;
    private static final double TARGET_LONGITUDE = 19.511868;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        show = (Button) findViewById(R.id.show);
        clear = (Button) findViewById(R.id.clear);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        list = new ArrayList<>();
        list.add(new LatLng(0, 0));
        list.add(new LatLng(78.7589, 12.57765));
        list.add(new LatLng(-34.364, 147.891));
        list.add(new LatLng(-33.501, 150.217));
        list.add(new LatLng(-32.306, 149.248));
        list.add(new LatLng(-32.491, 147.309));

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.show:
                        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

                        if (cursor.moveToFirst()) {
                            int idColIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                            int latColIndex = cursor.getColumnIndex(DBHelper.KEY_Lat);
                            int lonColIndex = cursor.getColumnIndex(DBHelper.KEY_Lon);
                            do {
                                list.add(new LatLng(cursor.getDouble(lonColIndex), cursor.getDouble(latColIndex)));

                                /**Log.d(LOG_TAG,
                                 "ID = " + cursor.getInt(idColIndex) +
                                 ", lon = " + cursor.getString(lonColIndex) +
                                 ", lat = " + cursor.getString(latColIndex));
                                 **/
                                // получаем значения по номерам столбцов и пишем все в лог
                            } while (cursor.moveToNext());
                        } else
                            Log.d(LOG_TAG, "0 rows");
                        cursor.close();
                        break;
                    case R.id.clear:
                        Log.d(LOG_TAG, "clear data");
                        database.delete(DBHelper.TABLE_CONTACTS, null, null);
                        break;

                }

            }
        };
        show.setOnClickListener(listener);
        clear.setOnClickListener(listener);

        //добавляем на карту свое местоположение
        //googleMap.setMyLocationEnabled(true);
    }

    //добавляем маркер на карту
    private void addMarker(){

        double lat = TARGET_LATITUDE;
        double lng = TARGET_LONGITUDE;
        //устанавливаем позицию и масштаб отображения карты
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(15)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);

        if(null != googleMap){
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title("Mark")
                    .draggable(false)
            );
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "11111");
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
        Cursor cursor=database.query(DBHelper.TABLE_CONTACTS,null,null,null,null,null,null);

        if (cursor.moveToFirst()){
            int idColIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int latColIndex = cursor.getColumnIndex(DBHelper.KEY_Lat);
            int lonColIndex = cursor.getColumnIndex(DBHelper.KEY_Lon);
            do {
                list.add(new LatLng(cursor.getDouble(latColIndex),cursor.getDouble(lonColIndex)));

                /**Log.d(LOG_TAG,
                 "ID = " + cursor.getInt(idColIndex) +
                 ", lon = " + cursor.getString(lonColIndex) +
                 ", lat = " + cursor.getString(latColIndex));
                 **/
                // получаем значения по номерам столбцов и пишем все в лог
            } while (cursor.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        cursor.close();
        googleMap.addPolyline(new PolylineOptions().clickable(true)
                .addAll(list));
        /**
         googleMap.addPolyline(new PolylineOptions().clickable(true)

         .add(new LatLng(-35.016, 143.321),
         new LatLng(-34.747, 145.592),
         new LatLng(-34.364, 147.891),
         new LatLng(-33.501, 150.217),
         new LatLng(-32.306, 149.248),
         new LatLng(-32.491, 147.309)));
         **/
        ///Location location=googleMap.getMyLocation();
        // Log.d(LOG_TAG, String.valueOf(location.getLatitude()));
        //double lat=googleMap.getMyLocation().getLatitude();
        //double lon =googleMap.getMyLocation().getLongitude();
        // Log.d(LOG_TAG, String.valueOf(lat)+" "+lon);

    }
}