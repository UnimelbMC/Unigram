package co.example.junjen.mobileinstagram.elements;

import android.content.res.Resources;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Arrays;

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
        this.timeSinceDisplay = formatTime(timeSince);
    }

    public String formatTime(String timeSince){

        // get time duration that has pass from timeSince activity till now
        long time = (System.currentTimeMillis() / 1000L) - Long.parseLong(timeSince);

        String formattedTime = Long.toString(time) + "s";  // default eg: 11s
        int convertedTime;
        int minute = Parameters.minute;
        int hour = Parameters.hour;
        int day = Parameters.day;
        int week = Parameters.week;

        int absTime = Math.abs((int) time);

        // eg: 11w
        if (absTime >= week){
            convertedTime = (int) Math.floor(time/week);
            formattedTime = Integer.toString(convertedTime) + "w";
        }
        // eg: 11d
        else if (absTime >= day){
            convertedTime = (int) Math.floor(time/day);
            formattedTime = Integer.toString(convertedTime) + "d";
        }
        // eg: 11h
        else if (absTime >= hour){
            convertedTime = (int) Math.floor(time/hour);
            formattedTime = Integer.toString(convertedTime) + "h";
        }
        // eg: 11m
        else if (absTime >= minute){
            convertedTime = (int) Math.floor(time/minute);
            formattedTime = Integer.toString(convertedTime) + "m";
        }
        return formattedTime;
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


    public JSONArray toJson() {
        String mStringArray[] = { timeSince,timeSinceDisplay};

        JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
        return mJSONArray;
    }
}
