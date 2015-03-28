package com.vandervidi.butler.butlertaskmanager;

import java.io.Serializable;

public class LatLngPointSerializable implements Serializable{
    private static final long serialVersionUID = 1L;
    private double lat,lng;

    public LatLngPointSerializable() {
        lat=0;
        lng=0;
    }

    public LatLngPointSerializable(double lat, double lng) {
        // TODO Auto-generated constructor stub
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }



}