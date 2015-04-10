package com.riyu.bigdataandroid;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ankit on 4/2/15.
 */
public interface LatLngInterpolator {
    public LatLng interpolate (float fraction, LatLng a, LatLng b);

    public class Linear implements  LatLngInterpolator{
        public LatLng interpolate(float fraction, LatLng a, LatLng b){
            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
            double lng = (b.longitude - a.longitude) * fraction + a.longitude;
            return new LatLng(lat, lng);
        }
    }
}
