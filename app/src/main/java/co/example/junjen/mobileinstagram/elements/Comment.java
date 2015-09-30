package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Comment objects.
 */
public class Comment {

    private Username username;
    private String comment;
    private TimeSince timeSince;

    public Comment(){
        // test constructor to create 'empty' comment objects

        this.username = new Username("username");
        this.comment = "comment";
        this.timeSince = new TimeSince();
    }

    public Comment(String username, String comment, TimeSince timeSince){
        this.username = new Username(username);
        this.comment = comment;
        this.timeSince = timeSince;
    }

    public Username getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public TimeSince getTimeSince() {
        return timeSince;
    }
}
