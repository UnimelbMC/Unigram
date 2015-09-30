package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Like objects.
 */
public class Like {

    private String username;
    private TimeSince timeSince;

    public Like(){
        // test constructor to create 'empty' Like objects

        this.username = "<username>";
        this.timeSince = new TimeSince();
    }

    public Like(String username, TimeSince timeSince){
        this.username = username;
        this.timeSince = timeSince;
    }

    public String getUsername() {
        return username;
    }

    public TimeSince getTimeSince() {
        return timeSince;
    }
}
