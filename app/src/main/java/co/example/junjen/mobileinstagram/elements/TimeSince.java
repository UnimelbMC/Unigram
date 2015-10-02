package co.example.junjen.mobileinstagram.elements;

import android.content.res.Resources;

import co.example.junjen.mobileinstagram.R;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates TimeSince objects.
 * Handles the sorting of time tagged objects based on their TimeSince object values.
 */
public class TimeSince {

    private String timeSince;

    public TimeSince(String timeSince){
        this.timeSince = timeSince;
    }

    public String getTimeSince() {
        return timeSince;
    }

    // TODO: convert time since from Data Object to appropriate variables

    public static double timeDiff(TimeSince timeFrom, TimeSince timeTo){
        // TODO: method for calculating time difference

        double timeDiff = 0;

        return timeDiff;
    }
}
