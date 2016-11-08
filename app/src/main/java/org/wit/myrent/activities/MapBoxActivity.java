package org.wit.myrent.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.wit.android.helpers.MapHelper;
import org.wit.myrent.R;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Residence;

import static org.wit.android.helpers.IntentHelper.navigateUp;


public class MapBoxActivity extends Activity implements OnMapReadyCallback{

    private MapView mapView;
    private MapboxMap mapboxMap;
    Long resId; // The id of the residence associate with this map pane
    Residence residence; // The residence associated with this map pane
    LatLng residenceLatLng;
    MyRentApp app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token only needs to be configured once in your app
        MapboxAccountManager.start(this, "pk.eyJ1IjoiYXVzdGluY3VubmluZ2hhbSIsImEiOiJjaXY5bWZ1MTkwMDExMnpwZnhjeGphM3QxIn0.z33AOo3eeJHgAdcmRvPDGw");

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_mapbox);


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        resId = (Long) getIntent().getSerializableExtra(ResidenceFragment.EXTRA_RESIDENCE_ID);
        app = (MyRentApp) getApplication();
        residence = app.portfolio.getResidence(resId);
        if (residence != null) {
            residenceLatLng = new LatLng(MapHelper.latitude(residence.geolocation),
                    MapHelper.longitude(residence.geolocation));
        }
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    // OnMapReadyCallback interface method impl
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        positionCamera();
        setMarker();
    }

    private void setMarker() {

        MarkerViewOptions marker = new MarkerViewOptions().position(residenceLatLng);
        mapboxMap.addMarker(marker);
    }

    private void positionCamera() {
        CameraPosition position = new CameraPosition.Builder()
                .target(residenceLatLng) // Sets the new camera position
                .zoom(residence.zoom) // Sets the zoom
                .build(); // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position));
    }
}