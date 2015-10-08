package co.example.junjen.mobileinstagram.elements;

import java.io.Serializable;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates User objects.
 */
public class User implements Serializable{

    private Username username;
    private Image userImage;
    private String profName;

    public User(String username, String userImage, String profName){
        this.username = new Username(username);
        this.userImage =  new Image(userImage);;
        this.profName = profName;
    }

    public Username getUsername() {
        return username;
    }

    public Image getUserImage() {
        return userImage;
    }

    public String getProfName() {
        return profName;
    }
}
