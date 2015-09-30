package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Comment objects.
 */
public class Comment {

    private String username;
    private String comment;
    private TimeSince timeSince;

    public Comment(){
        // test constructor to create 'empty' comment objects

        this.username = "<username>";
        this.comment = "<comment>";
        this.timeSince = new TimeSince();
    }

    public Comment(String username, String comment, TimeSince timeSince){
        this.username = username;
        this.comment = comment;
        this.timeSince = timeSince;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public TimeSince getTimeSince() {
        return timeSince;
    }
}
