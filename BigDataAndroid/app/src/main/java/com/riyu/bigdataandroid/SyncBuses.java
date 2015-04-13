package com.riyu.bigdataandroid;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ankit on 4/11/15.
 */
public class SyncBuses extends AsyncTask<String, String, String> {
    String BusesUrl = "http://mbus.doublemap.com/map/v2/buses";
//    private GoogleMap map;
    private HashMap<Integer, ArrayList<Buses>> active_buses = new HashMap<>();
    private final String TAG = "SyncBuses";
    @Override
    protected void onPreExecute(){}

    public SyncBuses(){}


    @Override
    protected String doInBackground(String ... arg0){
        getJSON();
        printStuff();
        Log.d(TAG, "doInBkg");
        return null;
    }

    @Override
    protected void onPostExecute(String strFromDoInBg){
//        while (true){
//            try{
//                Thread.sleep(3000);
//            }catch(InterruptedException e){
//                e.printStackTrace();
//            }
            printStuff();
//        }
    }

    private void getJSON(){
        try {
            JsonParser jParser = new JsonParser();
            JSONArray json_buses = jParser.getJSONFromUrl(BusesUrl);
            for (int i = 0; i < json_buses.length(); i++) {
                JSONObject obj = json_buses.getJSONObject(i);
                int route = obj.getInt("route");
                int id = obj.getInt("id");
                LatLng pos = new LatLng(obj.getDouble("lat"),obj.getDouble("lon"));
                if (active_buses.containsKey(route)){
                    active_buses.get(route).add(new Buses(id, route, pos));
                }
                else {
                    active_buses.put( route, new ArrayList<Buses>() );
                    active_buses.get(route).add(new Buses(id, route, pos));
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void printStuff(){ Log.d(TAG, "stuff");}

}
