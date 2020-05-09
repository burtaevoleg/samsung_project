package com.example.application_for_parent;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.PolylinePosition;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;

public class Current_Location extends AppCompatActivity implements CurrentUserLocation, UserLocationObjectListener {

    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    private String MAPKIT_API="4e300306-7520-48c4-8380-38d1599db7c0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PolylineMapObject polylineMapObject;
        MapKitFactory.setApiKey(MAPKIT_API);
        PolylinePosition polylinePosition=new PolylinePosition();
        Polyline polyline=new Polyline();
        List <Point> points = new ArrayList<>();
        points.add(new Point(74.9879,74.2342));
        points.add(new Point(73.1234,73.2342));


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current__location);
        MapKitFactory.initialize(this);
        mapView=(MapView)findViewById(R.id.mapview);
        mapView.getMap().setRotateGesturesEnabled(true);

        mapView.getMap().move(new CameraPosition(new Point(0, 0), 14, 0, 0));


        MapKit mapKit=MapKitFactory.getInstance();
        userLocationLayer=mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener((UserLocationObjectListener) this);

    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, R.drawable.user_arrow));

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(this, R.drawable.search_result),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE);
    }

    @Override
    public void onObjectRemoved(UserLocationView view) {
    }

    @Override
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {
    }



}
