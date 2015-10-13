package co.example.junjen.mobileinstagram.elements;

import android.util.Log;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Created by junjen on 2/10/2015.
 *
 * Creates Location objects for location tags
 */

public class Location implements Serializable{

    public JSONArray toJson() {
        String mStringArray[] = { Double.toString(latitude),Double.toString(longitude),
            location,locationId};
        JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
        return mJSONArray;
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
    public String getLocationId() {
        return locationId;
    }
    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }
}
