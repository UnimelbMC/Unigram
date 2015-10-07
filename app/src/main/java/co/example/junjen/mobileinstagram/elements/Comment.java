package co.example.junjen.mobileinstagram.elements;

import java.io.Serializable;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Comment objects.
 */
public class Comment implements Serializable{

    private Username username;
    private Image userImage;
    private String comment;
    private TimeSince timeSince;

    public Comment(String username, String userImage, String comment, TimeSince timeSince){
        this.username = new Username(username);
        this.userImage = new Image(userImage);
        this.comment = comment;
        this.timeSince = timeSince;
    }

    public Username getUsername() {
        return username;
    }

    public Image getUserImage() {
        return userImage;
    }

    public String getComment() {
        return comment;
    }

    public TimeSince getTimeSince() {
        return timeSince;
    }
}
