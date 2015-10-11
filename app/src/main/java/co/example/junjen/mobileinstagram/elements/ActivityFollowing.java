package co.example.junjen.mobileinstagram.elements;

import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;

/**
 * Created by junjen on 11/10/2015.
 */
public class ActivityFollowing {

    private int recentPostCount;
    private ArrayList<Post> postIcons;

    private View activityView;

    public ActivityFollowing(){
        this.recentPostCount = Parameters.default_recentPostCount;

        this.postIcons = new ArrayList<>();
        int i;
        Post post;

        // create empty posts
        for (i = 0; i < Parameters.activity_postIconsToShow; i++){
            post = new Post();
            this.postIcons.add(post);
        }
    }

    public ActivityFollowing( int recentPostCount, ArrayList<Post> postIcons){
        this.recentPostCount = recentPostCount;
        this.postIcons = postIcons;
    }

    public View buildActivityView(LayoutInflater inflater){
        return null;
    }

    public int getRecentPostCount() {
        return recentPostCount;
    }

    public ArrayList<Post> getPostIcons() {
        return postIcons;
    }

    public View getActivityView(){
        return activityView;
    }
}
