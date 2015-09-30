package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class handles user's profile images and post images.
 */
public class Image {

    private String imageString;

    public Image(){
        // test constructor to create 'empty' Image objects

        this.imageString = "<image>";
    }

    public Image(String imageString){
        this.imageString = imageString;
    }

    public String getImageString() {
        return imageString;
    }
}
