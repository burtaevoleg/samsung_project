package com.example.application_for_parent;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class Google_map extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    boolean map_on;
    GoogleMap gMap;
    Double last_latitude=0.0,last_longitude=0.0;
    MapView mapView;
    ArrayList<LatLng> list;
    DBHelper dbHelper;
    SharedPreferences sharedPreferences;
    ImageButton getAllLocations;
    ImageButton changeType,getLastLocation;
    View myView;
    SQLiteDatabase database;
    Marker position;
    Polyline polyline;
    String url_server;
    String answer;


    final String LOG_TAG="myLogs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG,"create_view");
        myView =inflater.inflate(R.layout.activity_google_map,container,false);
        map_on=true;
        url_server=getString(R.string.url_server);
        changeType=(ImageButton)myView.findViewById(R.id.changeType);
        getAllLocations=(ImageButton)myView.findViewById(R.id.getLocations);
        getLastLocation=(ImageButton)myView.findViewById(R.id.getLastLocation);
        sharedPreferences=this.getActivity().getSharedPreferences("preference",Context.MODE_PRIVATE);
        int ID =sharedPreferences.getInt("parent_id",-1);
        String un=sharedPreferences.getString("unique_code","");
        Log.d(LOG_TAG, un);

        Log.d(LOG_TAG, String.valueOf(ID)+" on create");

        return myView;
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) myView.findViewById(R.id.mapView);
//        mapView.getMapAsync(this);
        if(mapView!=null){

            mapView.onCreate(null);
            mapView.onResume();

            mapView.getMapAsync(this);
        }


        list = new ArrayList<>();
        /**
        list.add(new LatLng(0, 0));
        list.add(new LatLng(78.7589, 12.57765));
        list.add(new LatLng(-34.364, 147.891));
        list.add(new LatLng(-33.501, 150.217));
        list.add(new LatLng(-32.306, 149.248));
        list.add(new LatLng(-32.491, 147.309));
**/
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG,"on_create");
        super.onCreate(savedInstanceState);

        //добавляем на карту свое местоположение
        //googleMap.setMyLocationEnabled(true);
    }

    //добавляем маркер на карту
    private void addMarker() {


        String lat = sharedPreferences.getString("last_latitude", "");
        String lon = sharedPreferences.getString("last_longitude", "");

        Double lat_ = Double.valueOf(lat);
        Double lon_ = Double.valueOf(lon);

        //устанавливаем позицию и масштаб отображения карты

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat_, lon_))
                .zoom(15)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        gMap.animateCamera(cameraUpdate);
        //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if(null != gMap){
            if(position!=null) {
                position.remove();
            }
            position=gMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat_, lon_))
                    .title("Child")
                    .draggable(false)
            );
        }
    }

    private void addPolyline(){
        if(polyline!=null){
            polyline.remove();
        }
        ArrayList<LatLng> list;
        list = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int latColIndex = cursor.getColumnIndex(DBHelper.KEY_Lat);
            int lonColIndex = cursor.getColumnIndex(DBHelper.KEY_Lon);
            do {
                list.add(new LatLng(cursor.getDouble(latColIndex), cursor.getDouble(lonColIndex)));

                Log.d(LOG_TAG,
                 "ID = " + cursor.getInt(idColIndex) +
                 ", lon = " + cursor.getString(lonColIndex) +
                 ", lat = " + cursor.getString(latColIndex));
            } while (cursor.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        cursor.close();

        polyline=gMap.addPolyline(new PolylineOptions().clickable(true)
                .clickable(true)
                .color(Color.YELLOW)
                .addAll(list));
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }





    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        gMap=googleMap;
        String lat=sharedPreferences.getString("last_latitude","");
        String lon=sharedPreferences.getString("last_longitude","");
        if(!lat.equals("")){
            Double lat_=Double.valueOf(lat);
            Double lon_=Double.valueOf(lon);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat_, lon_))
                    .zoom(15)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            gMap.animateCamera(cameraUpdate);
            position=gMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat_, lon_))
                    .title("Marker")
                    .draggable(true));
        }
        //googleMap.setMyLocationEnabled(true); значок для обновления своего местопооложение
        gMap.setOnMyLocationButtonClickListener(this);
        gMap.setOnMyLocationClickListener(this);
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.changeType:
                        if (gMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                            gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        } else {
                            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        break;
                    case R.id.getLocations:
                        if(map_on) {
                            new MyAsyncTask().execute("getAllLocations");
                            if(answer!=null) {
                                addPolyline();
                            }
                            getAllLocations.setImageResource(R.drawable.ic_cancel_black_24dp);
                            map_on=false;
                        }
                        else {
                            if(polyline!=null) {
                                polyline.remove();
                            }
                            getAllLocations.setImageResource(R.drawable.ic_directions_black_24dp);
                            map_on=true;
                        }
                        break;
                    case R.id.getLastLocation:
                        new MyAsyncTask().execute("getLastLocation");
                        if(answer!=null) {
                            addMarker();
                        }
                        break;
                }
            }
        };
        changeType.setOnClickListener(listener);
        getAllLocations.setOnClickListener(listener);
        getLastLocation.setOnClickListener(listener);

/**
        if (cursor.moveToFirst()) {
            //int idColIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int latColIndex = cursor.getColumnIndex(DBHelper.KEY_Lat);
            int lonColIndex = cursor.getColumnIndex(DBHelper.KEY_Lon);
            do {
                list.add(new LatLng(cursor.getDouble(latColIndex), cursor.getDouble(lonColIndex)));

                Log.d(LOG_TAG,
                 "ID = " + cursor.getInt(idColIndex) +
                 ", lon = " + cursor.getString(lonColIndex) +
                 ", lat = " + cursor.getString(latColIndex));

            } while (cursor.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
 **/

        cursor.close();
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        polyline=gMap.addPolyline(new PolylineOptions().clickable(true)
                .clickable(true)
                .color(Color.YELLOW)
                .addAll(list));
    }


    class MyAsyncTask extends AsyncTask<String, String, String> {

        String answerHTTP;
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

        @Override
        protected String doInBackground(String... params) {

            if (params[0].equals("getAllLocations")) {
                OkHttpClient client = new OkHttpClient();
                int ID = sharedPreferences.getInt("parent_id", Context.MODE_PRIVATE); // уникальный ID ребенка
                String day_before = format.format(new Date()) + " 00:00:00.0";
                String day_after = format.format(new Date()) + " 23:59:59.0";
                Request request = new Request.Builder()
                        .url(url_server+"/location/getlocations?idperson="+ID+"&date_after=" + day_after + "&date_before=" + day_before + "")
                        .method("GET", null)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                answerHTTP=null;
                if (response != null && response.code() == 200) {
                    try {
                        answerHTTP = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    answer=answerHTTP;
                    return null;
                }

                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(answerHTTP).getAsJsonArray();

                SQLiteDatabase database = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                database.delete(DBHelper.TABLE_CONTACTS,null,null);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    String latitude = jsonObject.get("latitude").getAsString();
                    String longitude = jsonObject.get("longitude").getAsString();
                    String timezone = jsonObject.get("timezone").getAsString();

                    contentValues.put(DBHelper.KEY_Lat, latitude);
                    contentValues.put(DBHelper.KEY_Lon, longitude);
                    database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                    Log.d(LOG_TAG, latitude + "  " + longitude + "  " + timezone);
                }
            }
            if (params[0].equals("getLastLocation")) {
                OkHttpClient client = new OkHttpClient();
                int ID = sharedPreferences.getInt("parent_id", Context.MODE_PRIVATE); // уникальный ID ребенка
                Request request = new Request.Builder()
                        .url(url_server+"/location/getlastlocations?idperson="+ID)
                        .method("GET", null)
                        .build();

                Response response = null;
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
                else{
                    return null;
                }

                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(answerHTTP).getAsJsonArray();

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                    String latitude = jsonObject.get("latitude").getAsString();
                    String longitude = jsonObject.get("longitude").getAsString();
                    String timezone = jsonObject.get("timezone").getAsString();

                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("last_longitude",longitude);
                    editor.putString("last_latitude",latitude);
                    editor.commit();
                    last_latitude = Double.valueOf(latitude);
                    last_longitude = Double.valueOf(longitude);
                    Log.d(LOG_TAG, latitude + "  " + longitude + "  " + timezone);

                    Log.d(LOG_TAG, latitude + "  " + longitude + "  " + timezone);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.d(LOG_TAG,answerHTTP);
        }
    }
}