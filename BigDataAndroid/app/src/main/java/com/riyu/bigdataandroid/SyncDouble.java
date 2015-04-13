package com.riyu.bigdataandroid;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.JsonReader;
import android.util.Log;
import android.util.Property;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Riyu on 4/6/15.
 */
public class SyncDouble extends AsyncTask<String, String, String> {

    private ArrayList<Stops> stops = new ArrayList<Stops>();
    private ArrayList<Routes> routes = new ArrayList<Routes>();
    private HashMap<Integer, Integer> routesID = new HashMap<>();
    private HashMap<Integer, Marker> bus_stops  = new HashMap<Integer, Marker>();
    private HashMap<Integer, Marker> bus_map = new HashMap<>();
    private HashMap<Integer, ArrayList<Marker> > route_pointer = new HashMap<>();
    private ArrayList<Polyline> route_paths  = new ArrayList<Polyline>();
    private ArrayList<String> routeNames = new ArrayList<String>();
    private int[] shown;
    private GoogleMap map;
    private ProgressDialog dialog;
    private ActionBarActivity activity;
    private Bitmap scaledBus;
    private SyncBuses SB = new SyncBuses();


    // set your json string url here
    String StopsUrl = "http://mbus.doublemap.com/map/v2/stops";
    String RoutesUrl ="http://mbus.doublemap.com/map/v2/routes";


    @Override
    protected void onPreExecute() {}

    SyncDouble(GoogleMap map_in, Bitmap scaledBusIn){
        map = map_in;
        scaledBus = scaledBusIn;
    }

    @Override
    protected String doInBackground(String... arg0) {
//        SB.execute();
        Log.e("Sync Double", "How many times does this run? bkg");
        try {

            // instantiate our json parser
            JsonParser jParser = new JsonParser();

            // get json string from url
            JSONArray json_stops = jParser.getJSONFromUrl(StopsUrl);

            // loop through all users
            for (int i = 0; i < json_stops.length(); i++) {

                JSONObject s = json_stops.getJSONObject(i);

                // Storing each json item in variable
                int id = s.getInt("id");
                String description = s.getString("description");
                String name = s.getString("name");
                Double lat = s.getDouble("lat");
                Double lon = s.getDouble("lon");


                stops.add(new Stops(id, name, description, lat, lon));
                // show the values in our logcat
                Log.e("TAG", "id: " + id
                        + ", description: " + description
                        + ", name: " + name);



            }

            JSONArray json_routes = jParser.getJSONFromUrl(RoutesUrl);
            for (int i = 0; i < json_routes.length(); i++) {
                JSONObject r = json_routes.getJSONObject(i);
                int id = r.getInt("id");
                String Name = r.getString("name");
                String ShortName = r.getString("short_name");
                String Description = r.getString("description");
                String C = r.getString("color");
                int color = Integer.parseInt(C, 16);
                Boolean active = r.getBoolean("active");

                routeNames.add(Name);

                routes.add(new Routes(id,Name,ShortName, Description, color, r.getJSONArray("path"), active, r.getJSONArray("stops") ));
                routesID.put(i,  id);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(String strFromDoInBg) {
        updateBuses();
//        Log.e("Sync Double", "What about this one? postex");

        for (int i = 0; i < stops.size(); i++){

            int identification = stops.get(i).getId();
            bus_stops.put( identification ,
                    (map.addMarker(new MarkerOptions().position(stops.get(i).getCoordinates())
                                    .title(stops.get(i).getName())
                    ) ) );

            bus_stops.get(identification).setVisible(false);
        }
        shown = new int[bus_stops.size() + 1];

        Log.e("Size of routes", "size " + routes.size() );
        for (int i = 0; i < routes.size(); i++){
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            route_paths.add(map.addPolyline(new PolylineOptions().addAll(routes.get(i).getPath())
                                    .width(10).color(color) )

            );
            route_paths.get(i).setVisible(false);
        }

    }
   /* public void getBuses(Routes routeObj){
        int routeID = routeObj.getId();
        try {
            JsonParser jParser = new JsonParser();
            JSONArray json_buses = jParser.getJSONFromUrl(StopsUrl);
            for (int i = 0; i < json_buses.length(); i++) {
                JSONObject obj = json_buses.getJSONObject(i);
                int route = obj.getInt("route");
                if (routeID!=route){
                    continue;
                }
                int id = obj.getInt("id");
                LatLng pos = new LatLng(obj.getDouble("lat"),obj.getDouble("lon"));
                active_buses.put(  id, map.addMarker( new MarkerOptions().position(pos)
                        .title(obj.getString("name"))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) )  );

                routeObj.addBusId(id);

            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }*/
    public void updateBuses(){
//        Looper.prepare();
        new CountDownTimer(3000, 1000){
            public void onTick(long millisUntilFinished){}
            public void onFinish(){
                ArrayList<Buses> BusList = SB.getActive_buses();
                SB = new SyncBuses();
                SB.execute();
                Boolean empty = bus_map.isEmpty();
                for(int i = 0; i < BusList.size(); i++){
                    if (empty){

                        bus_map.put(BusList.get(i).getId(), map.addMarker(new MarkerOptions().position(BusList.get(i).getLoc() ).icon(BitmapDescriptorFactory.fromBitmap(scaledBus) )
                        ));

                        bus_map.get(BusList.get(i).getId()).setVisible(false);

                        if (route_pointer.containsKey(BusList.get(i).getRoute() ) ){
                           route_pointer.get(BusList.get(i).getRoute()).add(bus_map.get(BusList.get(i).getId()));
                        }
                        else{
                            route_pointer.put(BusList.get(i).getRoute(), new ArrayList<Marker>());
                            route_pointer.get(BusList.get(i).getRoute()).add(bus_map.get(BusList.get(i).getId()));
                        }
                    }
                    else{
                        if (bus_map.containsKey(BusList.get(i).getId())) {
                            if (bus_map.get(BusList.get(i).getId()).isVisible()) {

                                animateMarker(bus_map.get(BusList.get(i).getId()), BusList.get(i).getLoc(), new LatLngInterpolator.Linear());

                            } else {
                                bus_map.get(BusList.get(i).getId()).setPosition(BusList.get(i).getLoc());
                            }
                        }
                    }
                }
                updateBuses();
            }
        }.start();
    }
    public void showRoute(int identification, Boolean show){
        route_paths.get(identification).setVisible(show);
        if (route_pointer.containsKey(routes.get(identification).getId())) {
            for (int i = 0; i < route_pointer.get(routes.get(identification).getId()).size(); i++) {

                route_pointer.get(routes.get(identification).getId()).get(i).setVisible(show);

            }
        }
        ArrayList<Integer> stopIds = routes.get(identification).getStops();
//        getBuses(routes.get(identification));
        for(int i = 0; i <  stopIds.size(); i++ ){

            if (show){
                shown[stopIds.get(i)] += 1;
                if (bus_stops.get(stopIds.get(i)) == null){
//                    Log.d("SyncDouble", " "+ (stopIds.get(i)-1) );
//                    continue;
                    throw new NullPointerException("" + (stopIds.get(i)));
                }
                bus_stops.get(stopIds.get(i)).setVisible(true);
            }
            else{
                shown[stopIds.get(i)] -= 1;
                if (shown[stopIds.get(i)] > 0){

                }
                else{

                    bus_stops.get(stopIds.get(i)).setVisible(show);
                }
            }
        }
    }

    public ArrayList<String> getRoutes(){

        return routeNames;

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
        animator.setDuration(3000);
        animator.start();

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("MapsActivity", "Animation Ended");
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
}
