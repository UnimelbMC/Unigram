package co.example.junjen.mobileinstagram.elements;

import android.graphics.drawable.Drawable;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class handles user's profile images and post images.
 */
public class Image {

    private String imageString;

    public Image(String imageString){
        this.imageString = imageString;
    }

    public String getImageString() {
        return imageString;
    }

    public Drawable getImage(){
        // TODO: Fill appropriate image return type

        Drawable image = null;

        return image;
    }
}
