package co.example.junjen.mobileinstagram.elements;

import android.content.res.Resources;

import java.io.Serializable;

import co.example.junjen.mobileinstagram.R;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates TimeSince objects.
 * Handles the sorting of time tagged objects based on their TimeSince object values.
 */
public class TimeSince implements Serializable{

    private String timeSince;
    private String timeSinceDisplay;

    public TimeSince(){
        this.timeSince = Long.toString(System.currentTimeMillis() / 1000L);
        this.timeSinceDisplay = formatTime(this.timeSince);
    }

    public TimeSince(String timeSince){
        this.timeSince = timeSince;
        this.timeSinceDisplay = timeSince;
    }

    public String formatTime(String timeSince){
        return "";
    }

    public String getTimeSince() {
        return timeSince;
    }

    public String getTimeSinceDisplay(){
        return timeSinceDisplay;
    }

    // TODO: convert time since from Data Object to appropriate variables

    public static double timeDiff(TimeSince timeFrom, TimeSince timeTo){
        // TODO: method for calculating time difference

        double timeDiff = 0;

        return timeDiff;
    }
}
