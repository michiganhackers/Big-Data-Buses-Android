package com.riyu.bigdataandroid;

import android.graphics.Color;
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
 * Created by ankit on 4/2/15.
 */
public class Routes {

    private int id;
    private String Name;
    private String ShortName;
    private String Description;
    private int color;
    private ArrayList<LatLng> path = new ArrayList<LatLng>();
    private Boolean active;
    private ArrayList<Integer> stops = new ArrayList<Integer>();

    public Routes(int initId, String initName, String initShortName, String initDescription, int initColor, JSONArray initPath, Boolean initActive,JSONArray initStops) {
        id = initId;
        Name = initName;
        ShortName = initShortName;
        Description = initDescription;
        color = initColor;
        setUpPath(initPath);
        active = initActive;
        setUpStops(initStops);
    }



    private void setUpPath(JSONArray Path){
        try {
            for (int i = 0; i < Path.length()-1; i+=2){
                path.add(new LatLng(Path.getDouble(i), Path.getDouble(i+1)));
                //Log.d("Heyey", "" + Path.get(i) + ", " + Path.get(i+1));
            }
            Log.d("Kman", "pls");
        }
        catch(JSONException e){
            Log.d("LOL", e.getMessage());
        }
    }

    private void setUpStops(JSONArray Stops){
        try {
            for (int i = 0; i < Stops.length(); i++){
                stops.add(Stops.getInt(i));
            }
            Log.d("lol", "" + Stops.get(0));
        }
        catch(JSONException e){
            Log.d("LOL", e.getMessage());
        }
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return Name;
    }

    public String getDescription(){
        return Description;
    }

    public String getShortName(){
        return ShortName;
    }

    public int getColor(){
        return color;
    }

    public ArrayList<LatLng> getPath(){
        return path;
    }

    public Boolean getActive(){
        return active;
    }
    public ArrayList<Integer> getStops(){
        return stops;
    }

}
