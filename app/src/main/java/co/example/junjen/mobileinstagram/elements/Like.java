package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Like objects.
 */
public class Like {

    private Username username;
    private TimeSince timeSince;

    public Like(){
        // test constructor to create 'empty' Like objects

        this.username = new Username("username");
        this.timeSince = new TimeSince();
    }

    public Like(String username, TimeSince timeSince){
        this.username = new Username(username);
        this.timeSince = timeSince;
    }

    public Username getUsername() {
        return username;
    }

    public TimeSince getTimeSince() {
        return timeSince;
    }
}
