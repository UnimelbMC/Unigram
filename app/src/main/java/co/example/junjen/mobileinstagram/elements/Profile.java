package co.example.junjen.mobileinstagram.elements;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.UserImageView;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Profile objects.
 */
public class Profile implements Serializable{

    // Profile content
    private Username username;
    private Image userImage;
    private String profName;
    private String profDescrp;
    private ArrayList<Post> posts;
    private ArrayList<Username> followers;
    private ArrayList<Username> following;

    // Profile view
    private ExpandableScrollView profileView;
    private int postIconCount = 0;


    public Profile(){
        // test constructor to create 'empty' Profile objects

        this.username = new Username(Parameters.default_username);
        this.userImage = new Image(Parameters.default_image);
        this.profName = Parameters.default_profName;
        this.profDescrp = Parameters.default_profDescrp;

        this.posts = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();

        int i;
        Post post;
        String username;

        // create 10 empty posts
        for (i = 0; i < 50; i++){
            post = new Post();
            this.posts.add(post);
        }
        // create 10 empty followers
        for (i = 0; i < 10; i++){
            username = Parameters.default_username + (i + 1);
            this.followers.add(new Username(username));
        }
        // create 10 empty following
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

        postIconCount = 0;

        profileView = (ExpandableScrollView)
                inflater.inflate(R.layout.fragment_profile, null, false);

        /** Fixed parameters **/

        // User image
        if(!this.userImage.getImageString().equals(Parameters.default_image)) {
            UserImageView userImage = (UserImageView)
                    profileView.findViewById(R.id.profile_user_image);
            Image.setImage(userImage, this.userImage.getImage());
        }

        // Profile name
        TextView profName = (TextView) profileView.findViewById(R.id.profile_name);
        profName.setText(this.profName);

        // Profile description
        TextView profDescrp = (TextView) profileView.findViewById(R.id.profile_description);
        profDescrp.setText(this.profDescrp);

        // Post count
        TextView postCount = (TextView) profileView.findViewById(R.id.profile_post_count);
        int postsSize = this.posts.size();
        postCount.setText(Integer.toString(postsSize));

        // Follower count
        TextView followerCount = (TextView) profileView.findViewById(R.id.profile_follower_count);
        followerCount.setText(Integer.toString(this.followers.size()));

        // Following count
        TextView followingCount = (TextView) profileView.findViewById(R.id.profile_following_count);
        followingCount.setText(Integer.toString(this.following.size()));

        // Add post icons
        if(postsSize > 0){
            TextView postFlag = (TextView) profileView.findViewById(R.id.profile_no_post_flag);
            postFlag.setVisibility(View.GONE);
            getPostIcons(inflater);
        }

        return profileView;
    }

    public void getPostIcons(LayoutInflater inflater){

        int postsSize = this.posts.size();

        if (postIconCount < postsSize) {

            ArrayList<Post> posts = new ArrayList<>();

            int i;
            int index;
            int maxPostIcons = Parameters.postIconsPerRow * Parameters.postIconRowsToLoad;
            for (i = 0; i < maxPostIcons; i++) {
                index = i + postIconCount;
                if (index < postsSize) {
                    posts.add(this.posts.get(index));
                } else {
                    break;
                }
            }

            int postIconRowCount = postIconCount / Parameters.postIconsPerRow;

            Post.getPostIcons(inflater,
                    (LinearLayout) profileView.findViewById(R.id.profile_post_icons),
                    posts);

            postIconCount += i;
        }
    }

    public void setUsername(String username) {
        this.username = new Username(username);
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
