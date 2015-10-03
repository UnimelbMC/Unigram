package co.example.junjen.mobileinstagram.elements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Profile objects.
 */
public class Profile {

    // Profile content
    private Username username;
    private Image userImage;
    private String profName;
    private String profDescrp;
    private ArrayList<Post> posts;
    private ArrayList<Username> followers;
    private ArrayList<Username> following;

    public Profile(){
        // test constructor to create 'empty' Profile objects

        this.username = new Username(Parameters.default_username);
        this.userImage = new Image(Parameters.default_image);
        this.profName = Parameters.default_username;
        this.profDescrp = Parameters.default_profDescrp;

        this.posts = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();

        int i;
        Post post;
        String username;

        // create 10 empty posts
        for (i = 0; i < 20; i++){
            post = new Post();
            this.posts.add(post);
        }
        // create 10 empty followers
        for (i = 0; i < 10; i++){
            username = Parameters.default_username + (i + 1);
            this.followers.add(new Username(username));
        }
        // create 20 empty following
        for (i = 0; i < 10; i++){
            username = Parameters.default_username + (i + 1);
            this.following.add(new Username(username));
        }
    }

    public Profile(String username, String userimage, String profName, String profDescrp,
                   String posts, String followers, String following){

        this.username = new Username(username);
        this.userImage = new Image(userimage);
        this.profName = profName;
        this.profDescrp = profDescrp;
        this.posts = createPostsList(posts);
        this.followers = createUsernameList(followers);
        this.following = createUsernameList(following);

    }

    private ArrayList<Post> createPostsList(String posts_string){
        ArrayList<Post> posts = new ArrayList<>();

        // TODO: method to convert JSON posts_string into ArrayList<Like>

        return posts;
    }

    private ArrayList<Username> createUsernameList(String usernames_string){
        ArrayList<Username> usernames = new ArrayList<>();

        // TODO: method to convert JSON usernames_string into ArrayList<Like>

        return usernames;
    }

    public ExpandableScrollView getProfileView(LayoutInflater inflater){

        ExpandableScrollView profileView = (ExpandableScrollView)
                inflater.inflate(R.layout.fragment_profile, null, false);

//        /** Fixed parameters **/
//
//        // User image
//        if(!this.userImage.getImageString().equals(Parameters.default_image)) {
//            ImageView userImage = (ImageView) profileView.findViewById(R.id.profile_user_image);
//            // TODO: Determine set image type
//            userImage.setImageDrawable(this.userImage.getImage());
//        }
//
//        // Username
//        TextView username = (TextView) postView.findViewById(R.id.post_header_username);
//        username.setText("");   // remove default text
//        stringComponents.add(this.username.getUsername_link());
//        StringFactory.stringBuilder(username, stringComponents);
//        stringComponents.clear();
//
//        // Time since posted
//        TextView timeSince = (TextView) postView.findViewById(R.id.post_header_time_since);
//        timeSince.setText(this.timeSince.getTimeSince());
//
//        // Post image
//        if(!this.postImage.getImageString().equals(Parameters.default_image)) {
//            ImageView postImage = (ImageView) postView.findViewById(R.id.post_image);
//            // TODO: Determine set image type
//            postImage.setImageDrawable(this.postImage.getImage());
//        }
//
//        // TODO: Handle clicks for like button



        return profileView;
    }

    public void getPostIcons(LayoutInflater inflater){

    }

    public Username getUsername() {
        return username;
    }

    public String getProfName() {
        return profName;
    }

    public String getProfDescrp() {
        return profDescrp;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public ArrayList<Username> getFollowers() {
        return followers;
    }

    public ArrayList<Username> getFollowing() {
        return following;
    }
}
