package com.riyu.bigdataandroid;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Ankit on 4/2/15.
 */
public class Routes {

    private ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
    private String name;
    private String color;
    private String description;
    private boolean active;
    private JSONArray stops;


    public Routes(String jSON) {
        setUp(jSON);
    }
    public ArrayList<LatLng> getCoordinates(){ return coordinates; }
    public String getName(){ return name; }
    public String getColor(){return color; }
    public String getDescription(){ return description; }
    public boolean getActive(){ return active; }
    public JSONArray getStops(){ return stops; }




    private void setUp(String jSON){
        try {
            JSONObject jObject = new JSONObject(jSON);
            JSONArray latLongArray = jObject.getJSONArray("path");
            for (int i = 0; i < latLongArray.length()-1; i+=2){
                coordinates.add(new LatLng(latLongArray.getDouble(i), latLongArray.getDouble(i+1)));
            }
            name = jObject.getString("name");
            color = jObject.getString("color");
            active = jObject.getBoolean("active");
            description = jObject.getString("description");
            stops = jObject.getJSONArray("stops");
        }
        catch(JSONException e){
            Log.d("Get rekt", e.getMessage());
        }
    }
}
