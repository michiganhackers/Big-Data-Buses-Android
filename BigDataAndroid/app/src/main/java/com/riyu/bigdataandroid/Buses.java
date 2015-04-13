package com.riyu.bigdataandroid;

import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by ankit on 4/10/15.
 */
public class Buses {
    private int id;
    private int route;
    private LatLng loc;

    public Buses(int idIn, int routeIn, LatLng locIn){
        id = idIn;
        route = routeIn;
        loc = locIn;
    }
    public void setLoc(LatLng in){
        loc = in;
    }
    public int getId(){ return  id; }
    public int getRoute(){ return route; }
    public LatLng getLoc(){ return loc; }
}
