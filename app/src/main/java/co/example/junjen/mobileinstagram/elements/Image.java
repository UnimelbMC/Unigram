package co.example.junjen.mobileinstagram.elements;

import android.content.Context;
import android.widget.ImageView;
import co.example.junjen.mobileinstagram.R;

import com.squareup.picasso.Picasso;

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

    public static void setImage(ImageView imageView, Image image){

        Context context = Parameters.MainActivityContext;
        String imageLink = image.getImageString();

        if(imageView != null) {
            if (imageLink.equals(Parameters.default_loginUserImageLink)) {
                imageView.setImageResource(R.drawable.login_user_image);
            } else if (imageLink.equals(Parameters.default_emptyUserImageLink)) {
                imageView.setImageResource(R.drawable.empty_user_image);

            } else {
                // load image into ImageView with Picasso image library
                Picasso.with(context).load(image.getImageString()).into(imageView);
            }
        }
    }


}
