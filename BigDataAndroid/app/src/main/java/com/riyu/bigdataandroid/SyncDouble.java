package com.riyu.bigdataandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
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

/**
 * Created by Riyu on 4/6/15.
 */
public class SyncDouble extends AsyncTask<String, String, String> {

    private ArrayList<Stops> stops = new ArrayList<Stops>();
    private ArrayList<Routes> routes = new ArrayList<Routes>();
    private HashMap<Integer, Marker> bus_stops  = new HashMap<Integer, Marker>();
    private ArrayList<Polyline> route_paths  = new ArrayList<Polyline>();
    private ArrayList<String> routeNames = new ArrayList<String>();
    private int[] shown;
    private GoogleMap map;
    private ProgressDialog dialog;
    private ActionBarActivity activity;

    // set your json string url here
    String StopsUrl = "http://mbus.doublemap.com/map/v2/stops";
    String RoutesUrl ="http://mbus.doublemap.com/map/v2/routes?inactive=true";


    @Override
    protected void onPreExecute() {}

    SyncDouble(GoogleMap map_in){
        map = map_in;
    }

    @Override
    protected String doInBackground(String... arg0) {

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

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(String strFromDoInBg) {

        for (int i = 0; i < stops.size(); i++){
            int identification = stops.get(i).getId();
            bus_stops.put( identification ,
                    (map.addMarker(new MarkerOptions().position(stops.get(i).getCoordinates())
                    .title(stops.get(i).getName()) ) )
            );

            bus_stops.get(identification).setVisible(false);
        }
        shown = new int[bus_stops.size()];

        Log.e("Size of routes", "size " + routes.size() );
        for (int i = 0; i < routes.size(); i++){
            route_paths.add(map.addPolyline(new PolylineOptions().addAll(routes.get(i).getPath())
                                    .width(5)
                            /* Right Now the Colors are transparent for some reason .color(routes.get(i).getColor() )*/)
            );
            route_paths.get(i).setVisible(false);
        }

    }

    public void showRoute(int identification, Boolean show){
        route_paths.get(identification).setVisible(show);
        ArrayList<Integer> stopIds = routes.get(identification).getStops();
        for(int i = 0; i <  stopIds.size(); i++ ){

            if (show){
                shown[stopIds.get(i)] += 1;

                bus_stops.get(stopIds.get(i) ).setVisible(true);
            }
            else{
                shown[stopIds.get(i)] -= 1;
                if (shown[stopIds.get(i)] > 0){

                }
                else{

                    bus_stops.get(stopIds.get(i) ).setVisible(show);
                }
            }
        }
    }

    public ArrayList<String> getRoutes(){

        return routeNames;

    }
}