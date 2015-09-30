package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates TimeSince objects.
 * Handles the sorting of time tagged objects based on their TimeSince object values.
 */
public class TimeSince {

    private String timeSince;

    public TimeSince(){
        // test constructor to create 'empty' TimeSince objects

        this.timeSince = "<timeSince";
    }

    public TimeSince(String timeSince){
        this.timeSince = timeSince;
    }

    public String getTimeSince() {
        return timeSince;
    }
}
