package co.example.junjen.mobileinstagram.elements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Profile objects.
 */
public class Profile {

    // Profile content
    private String username;
    private String profName;
    private String profDescrp;
    private ArrayList<Post> posts;
    private ArrayList<Username> followers;
    private ArrayList<Username> following;

    // Profile view
    private View profileView;

    public Profile(LayoutInflater inflater, ViewGroup parentView){



    }
}
