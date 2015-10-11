package co.example.junjen.mobileinstagram.elements;

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

    // TODO: convert location from Data Object to appropriate variables

    public static double locationDiff(Location locationFrom, Location locationTo){
        // TODO: method for calculating location difference

        double locationDiff = 0;

        return locationDiff;
    }
}
