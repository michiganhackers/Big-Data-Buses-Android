package com.riyu.bigdataandroid;

import android.animation.Animator;
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
        Routes bBaits = new Routes(b_BaitsString);
        line = map.addPolyline(new PolylineOptions()
                .addAll(bBaits.getCoordinates())
                .width(5)
                .color(Color.GREEN));
        line.setVisible(false);
        Bitmap origBus = BitmapFactory.decodeResource(getResources(), R.drawable.temp_bus);
        Bitmap scaledBus = Bitmap.createScaledBitmap(origBus, origBus.getWidth()/10, origBus.getHeight()/10, false);
        Marker marker = map.addMarker(new MarkerOptions().position(bBaits.getCoordinates().get(0))
                .title("Swishigan")
                .icon(BitmapDescriptorFactory.fromBitmap(scaledBus)));
        int count = 0;
        long currentTime = System.currentTimeMillis();
        animateMarker(marker, bBaits.getCoordinates().get(0), new LatLngInterpolator.Linear());
//        while(count < bBaits.getCoordinates().size()-1){
//            if (System.currentTimeMillis() - currentTime > 3000){
//                animateMarker(marker, bBaits.getCoordinates().get(count+1), new LatLngInterpolator.Linear());
//                count++;
//                currentTime = System.currentTimeMillis();
//            }
//        }
        for(int i = 0; i < bBaits.getCoordinates().size()-1; i++){
//            marker.setPosition(bBaits.getCoordinates().get(i));
//            Marker marker = map.addMarker(new MarkerOptions().position(bBaits.getCoordinates().get(i))
//                    .title("Swishigan")
//                    .icon(BitmapDescriptorFactory.fromBitmap(scaledBus)));
            animateMarker(marker, bBaits.getCoordinates().get(i+1), new LatLngInterpolator.Linear());
        }

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
        animator.setDuration(15000);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        marker.setPosition(finalPos);
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            Log.d("Thread", e.getMessage());
//        }
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

    String b_BaitsString = "{\"id\":311," +
            "\"name\":\"Bursley-Baits\"," +
            "\"short_name\":\"BB\"," +
            "\"description\":\"Bursley-Baits provides service between the Central Campus Transit Center, Bursley Hall and Vera Baits Houses on North Campus. Bursley-Baits operates seven days per week during Fall and Winter terms only. The evening and weekend Bursley-Baits route also provides outbound service to Stockwell Hall and the Cardiovascular Center along Observatory.\"," +
            "\"color\":\"007F00\"," +
            "\"path\":[42.277904,-83.735084,42.277913,-83.735069,42.277495,-83.734548,42.277392,-83.73428,42.277454,-83.732487,42.277454,-83.732487,42.27748,-83.731675,42.277254,-83.730915,42.277254,-83.730915,42.277271,-83.730974,42.278672,-83.731027,42.278672,-83.731027,42.281721,-83.731096,42.28227,-83.73102,42.282451,-83.730987,42.282426,-83.731249,42.282361,-83.732026,42.282361,-83.732026,42.282296,-83.733537,42.283135,-83.73366,42.283204,-83.733832,42.28317,-83.734987,42.28317,-83.734987,42.283152,-83.735664,42.283998,-83.735741,42.283998,-83.735741,42.284729,-83.7356,42.285401,-83.735006,42.286045,-83.733643,42.286045,-83.733643,42.286457,-83.732463,42.286544,-83.731286,42.286817,-83.729564,42.286927,-83.727389,42.286927,-83.727389,42.287085,-83.726011,42.287206,-83.721468,42.287367,-83.718972,42.288179,-83.719035,42.288179,-83.719035,42.28939,-83.71933,42.289915,-83.719017,42.290205,-83.718422,42.29267,-83.718738,42.29267,-83.718738,42.29486,-83.719218,42.294642,-83.720761,42.294642,-83.720761,42.294535,-83.723221,42.294344,-83.723915,42.294104,-83.724256,42.293776,-83.724495,42.292745,-83.724487,42.29239,-83.724489,42.291628,-83.723781,42.291548,-83.723395,42.291906,-83.723406,42.292532,-83.723953,42.292659,-83.724382,42.292727,-83.724484,42.293596,-83.724547,42.294104,-83.724256,42.294464,-83.723608,42.294561,-83.722663,42.294561,-83.722663,42.294645,-83.720713,42.294839,-83.719747,42.29486,-83.719218,42.29227,-83.718672,42.29227,-83.718672,42.290385,-83.718475,42.290265,-83.719206,42.28983,-83.71975,42.28983,-83.71975,42.289336,-83.719814,42.288301,-83.719363,42.287568,-83.719225,42.287568,-83.719225,42.287347,-83.719187,42.287206,-83.721468,42.287075,-83.728139,42.287075,-83.728139,42.287033,-83.729528,42.286633,-83.732031,42.286457,-83.732463,42.286141,-83.733416,42.286141,-83.733416,42.285401,-83.735006,42.284937,-83.735464,42.284353,-83.735739,42.281335,-83.735556,42.281334,-83.735567,42.281334,-83.735567,42.281286,-83.737225,42.280812,-83.737192,42.280812,-83.737192,42.278568,-83.73707,42.278568,-83.736355,42.278568,-83.736355,42.278474,-83.73585,42.27796,-83.735127]," +
            "\"schedule_url\":\"http://www.pts.umich.edu/maps/bursley_baits.pdf\"," +
            "\"active\":true," +
            "\"stops\":[137,3,5,8,10,128,100,92,134,133,84,83,101,48,32,44]}";

}
