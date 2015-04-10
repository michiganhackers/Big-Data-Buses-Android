package com.riyu.bigdataandroid;

/**
 * Created by Riyu on 4/6/15.
 */

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Stops {
    private int id;
    private String named;
    private String description;
    private LatLng Coordinates;

    public Stops(int init_id,String init_name, String init_descr, Double lat, Double lon)
    {
        id = init_id;
        named = init_name;
        description = init_descr;
        Coordinates = new LatLng(lat, lon);
    }
    public int getId(){return id;}
    public String getName(){return named;}
    public String getDescription(){return description;}
    public LatLng getCoordinates(){return Coordinates;}

}
