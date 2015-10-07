package co.example.junjen.mobileinstagram.elements;

import java.io.Serializable;

/**
 * Created by junjen on 2/10/2015.
 *
 * Creates Location objects for location tags
 */

public class Location implements Serializable{

    private String location;

    public Location(String location){
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    // TODO: convert location from Data Object to appropriate variables

    public static double locationDiff(Location locationFrom, Location locationTo){
        // TODO: method for calculating time difference

        double locationDiff = 0;

        return locationDiff;
    }
}
