package co.example.junjen.mobileinstagram.elements;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class handles user's profile images and post images.
 */
public class Image implements Serializable {

    private String imageString;

    public Image(String imageString){
        this.imageString = imageString;
    }

    public String getImageString() {
        return imageString;
    }

    public Image getImage(){
        // TODO: Fill appropriate image return type

        Image image = null;

        return image;
    }

    public static void setImage(ImageView imageView, Image image){


        // TODO: Determine set image type


        // test url: http://www.menucool.com/slider/jsImgSlider/images/image-slider-2.jpg
    }


}
