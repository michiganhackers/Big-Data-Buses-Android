package com.riyu.bigdataandroid;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private UiSettings mUISettings;
    private LatLng ANN_ARBOR = new LatLng(42.2818294, -83.7317954);
    private LatLng FINAL_POS = new LatLng(43, -82);
    private float Lat = (float) 42.2818294;
    private float Long = (float) -83.7317954;
    private SupportMapFragment fragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private boolean[] selected;
    private Toolbar mToolbar;
    private Polyline line;

//    private LatLng someLat = new LatLng(42.2818294, -83.7317954);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        setUpMapIfNeeded();
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = new String[] {"Bursley Baits","Northwood","Commuter South","Commuter North"};
        selected = new boolean[mPlanetTitles.length];
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setSelector(R.drawable.list_selector);
        //mDrawerList.setItemsCanFocus(false);
        SupportMapFragment mapFragment = ((SupportMapFragment)getSupportFragmentManager().
                findFragmentById(R.id.map));
//        assert(mapFragment!=null);
        mapFragment.getMapAsync(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Magic Bus");
        mToolbar.setTitleTextColor(Color.WHITE);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        //Lets get a shadow object later
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.draw_item, mPlanetTitles));

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());



        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            //selectItem(0);
        }

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
        Routes bBaits = new Routes();
        line = map.addPolyline(new PolylineOptions()
                .addAll(bBaits.b_Baits)
                .width(5)
                .color(Color.GREEN));
        line.setVisible(false);
        Bitmap origBus = BitmapFactory.decodeResource(getResources(), R.drawable.temp_bus);
        Bitmap scaledBus = Bitmap.createScaledBitmap(origBus, origBus.getWidth()/10, origBus.getHeight()/10, false);
        Marker marker = map.addMarker(new MarkerOptions().position(bBaits.b_Baits.get(0))
                .title("Swishigan")
                .icon(BitmapDescriptorFactory.fromBitmap(scaledBus)));
//        for(int i = 0; i < bBaits.b_Baits.size()-1; i++){
//            marker.setPosition(bBaits.b_Baits.get(i));
//            animateMarker(marker, bBaits.b_Baits.get(i+1), new LatLngInterpolator.Linear());
//        }

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

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        // update selected item and title, then close the drawer
        //mDrawerList.setItemChecked(position, true);
        selected[position] ^= true;
        if (selected[0]){
            if (line != null){
                line.setVisible(true);
            }
        }
        else{
            if (line != null){
                line.setVisible(false);
            }
        }
        mDrawerList.setItemChecked(position, selected[position]);
        //mDrawerLayout.closeDrawer(mDrawerList);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
