package com.example.application_for_parent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;

public class Yandex_map extends AppCompatActivity {

    private final String MAPKIT_API_KEY = "4e300306-7520-48c4-8380-38d1599db7c0";
    private final Point TARGET_LOCATION = new Point(59.945933,30.320045);
    private MapView mapView;
    Polyline polyline;
    PolylineOptions polylineOptions;
    ArrayList<LatLng> points = new ArrayList<>();
    final String LOG_TAG="myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        points.add(new LatLng(74.9879,74.2342));
        points.add(new LatLng(73.1234,73.2342));
        polylineOptions=new PolylineOptions().addAll(points);







        /**
         * Set the api key before calling initialize on MapKitFactory.
         * It is recommended to set api key in the Application.onCreate method,
         * but here we do it in each activity to make examples isolated.
         */
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        /**
         * Initialize the library to load required native libraries.
         * It is recommended to initialize the MapKit library in the Activity.onCreate method
         * Initializing in the Application.onCreate method may lead to extra calls and increased battery use.
         */
        MapKitFactory.initialize(this);
        // Now MapView can be created.
        setContentView(R.layout.activity_yandex_map);
        super.onCreate(savedInstanceState);
        mapView = (MapView)findViewById(R.id.mapview);

        // And to show what can be done with it, we move the camera to the center of Saint Petersburg.
        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null);
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
}
