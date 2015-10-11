package co.example.junjen.mobileinstagram.elements;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import junit.framework.Test;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.UserImageView;

/**
 * Created by junjen on 11/10/2015.
 *
 * Stores following activity data.
 */
public class ActivityFollowing {

    private Username username;
    private Image userImage;
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

        this.username = postIcons.get(0).getUsername();
        this.userImage = postIcons.get(0).getUserImage();
    }

    public ActivityFollowing(int recentPostCount, ArrayList<Post> postIcons){
        this.recentPostCount = recentPostCount;
        this.postIcons = postIcons;
        this.username = postIcons.get(0).getUsername();
        this.userImage = postIcons.get(0).getUserImage();
    }

    // builds activity following view
    public void buildActivityView(LayoutInflater inflater){

        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        activityView = inflater.inflate(R.layout.activity_following_element, null, false);
        TextView usernameView = (TextView) activityView.findViewById(R.id.activity_following_username);
        UserImageView userImageView = (UserImageView) activityView.findViewById(R.id.activity_following_user_image);
        TextView timeSinceView = (TextView) activityView.findViewById(R.id.activity_following_time_since);
        LinearLayout postIconsList = (LinearLayout) activityView.findViewById(R.id.activity_following_post_icons);

        // set views
        Image.setImage(userImageView, this.userImage);
        usernameView.setText("");   // remove default text
        stringComponents.add(this.username.getUsernameLink());
        StringFactory.stringBuilder(usernameView, stringComponents);
        stringComponents.clear();
        timeSinceView.setText(postIcons.get(0).getTimeSince().getTimeSinceDisplay());

        Post.buildPostIcons(inflater, postIconsList, postIcons);
    }

    public Username getUsername() {
        return username;
    }

    public Image getUserImage() {
        return userImage;
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
