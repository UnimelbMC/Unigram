package co.example.junjen.mobileinstagram.elements;

import android.util.Log;

import java.io.Serializable;


/**
 * Created by junjen on 2/10/2015.
 *
 * Creates Location objects for location tags
 */

public class Location implements Serializable{
    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", locationId='" + locationId + '\'' +
                ", location='" + location + '\'' +
                ", longitude=" + longitude +
                '}';
    }

    private String locationId;
    private String location;
    private double latitude;
    private double longitude;

    public Location(String location){
        this.location = location;
    }

    public Location(String locationId, String location, double latitude, double longitude){
        this.locationId = locationId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getLocation() {
        return location;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

}
