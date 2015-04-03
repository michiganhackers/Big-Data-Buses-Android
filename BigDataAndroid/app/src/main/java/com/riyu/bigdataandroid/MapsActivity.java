package com.riyu.bigdataandroid;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Property;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private UiSettings mUISettings;
    private LatLng ANN_ARBOR = new LatLng(42.2818294, -83.7317954);
    private LatLng FINAL_POS = new LatLng(43, -82);
    private float Lat = (float) 42.2818294;
    private float Long = (float) -83.7317954;
//    private LatLng someLat = new LatLng(42.2818294, -83.7317954);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        SupportMapFragment mapFragment = ((SupportMapFragment)getSupportFragmentManager().
                findFragmentById(R.id.map));
//        assert(mapFragment!=null);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mUISettings = mMap.getUiSettings();
        CameraPosition camPos = new CameraPosition(ANN_ARBOR,(float)14.5,0,0);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
        mMap.setMyLocationEnabled(true);
//        mUISettings.setZoomControlsEnabled(true);
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//        addingBuses();
    }

    @Override
    public void onMapReady(GoogleMap map){
//        addingBuses();
        Bitmap origBus = BitmapFactory.decodeResource(getResources(), R.drawable.temp_bus);
        Bitmap scaledBus = Bitmap.createScaledBitmap(origBus, origBus.getWidth()/10, origBus.getHeight()/10, false);
        Marker marker = map.addMarker(new MarkerOptions().position(ANN_ARBOR)
                .title("Swishigan")
                .icon(BitmapDescriptorFactory.fromBitmap(scaledBus)));
        animateMarker(marker, FINAL_POS, new LatLngInterpolator.Linear());
        Log.d("test", "yee");
    }


    static void animateMarker(Marker marker, LatLng finalPos, final LatLngInterpolator latLngInterpolator){
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPos);
        animator.setDuration(30000);
        animator.start();
    }

   /* private void addingBuses(){
        GroundOverlayOptions bus1 = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.temp_bus))
                .position(ANN_ARBOR, 100, 100);
        GroundOverlay imageOverlay = mMap.addGroundOverlay(bus1);
        imageOverlay.setVisible(true);
//        imageOverlay.setTransparency(1);
//        for(int i =0; i<10000; i++){
//            Lat+=.001;
//            Long+=.001;
//            bus1.position(new LatLng(Lat, Long), 100, 100);
//            GroundOverlay imageOverlay = mMap.addGroundOverlay(bus1);
//            imageOverlay.setVisible(true);
//        }*/
}
