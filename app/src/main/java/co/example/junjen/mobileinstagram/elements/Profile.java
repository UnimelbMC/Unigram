package co.example.junjen.mobileinstagram.elements;

import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import co.example.junjen.mobileinstagram.CommentsFragment;
import co.example.junjen.mobileinstagram.NavigationBar;
import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.UsersFragment;
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

    private int postCount;
    private int followerCount;
    private int followingCount;

    private ArrayList<Post> posts;

    // Profile view
    private ExpandableScrollView profileView;
    private int postIconCount = 0;


    public Profile(String username){
        // test constructor to create 'empty' Profile objects

        this.username = new Username(username);
        this.userImage = new Image(Parameters.default_image);
        this.profName = Parameters.default_profName;
        this.profDescrp = Parameters.default_profDescrp;

        this.postCount = Parameters.default_postCount;
        this.followerCount = Parameters.default_followerCount;
        this.followingCount = Parameters.default_followingCount;

        this.posts = new ArrayList<>();

        int i;
        Post post;

        // create empty posts
        for (i = 0; i < this.postCount; i++){
            post = new Post();
            this.posts.add(post);
        }
    }

    public Profile(User user, String profDescrp, int postCount, int followerCount,
                   int followingCount, ArrayList<Post> posts){

        this.username = user.getUsername();
        this.userImage = user.getUserImage();
        this.profName = user.getProfName();
        this.profDescrp = profDescrp;

        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.posts = posts;

    }

    public ExpandableScrollView getProfileView(LayoutInflater inflater){

        postIconCount = 0;

        profileView = (ExpandableScrollView)
                inflater.inflate(R.layout.fragment_profile, null, false);
        ArrayList<CharSequence> stringComponents = new ArrayList<>();
        String text;

        /** Fixed parameters **/

        // User image
        if(!this.userImage.getImageString().equals(Parameters.default_image)) {
            UserImageView userImage = (UserImageView)
                    profileView.findViewById(R.id.profile_user_image);
            Image.setImage(userImage, this.userImage);
        }

        // Profile name
        TextView profName = (TextView) profileView.findViewById(R.id.profile_name);
        profName.setText(this.profName);

        // Profile description
        TextView profDescrp = (TextView) profileView.findViewById(R.id.profile_description);
        profDescrp.setText(this.profDescrp);

        // Post count
        TextView postCount = (TextView) profileView.findViewById(R.id.profile_post_count);
        postCount.setText(Integer.toString(this.postCount));

        // Follower count
        TextView followerCount = (TextView) profileView.findViewById(R.id.profile_follower_count);
        text = Integer.toString(this.followerCount);
        SpannableString followerLink = StringFactory.createLink(text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // TODO: get followers from Network
                ArrayList<User> followers = new ArrayList<>();
                int i;
                for(i = 0; i < 50; i++){
                    String username = Parameters.default_username+i;
                    followers.add(new User(username, Parameters.default_emptyUserImageLink,
                            Parameters.default_profName));
                }


                // display followers
                Parameters.NavigationBarActivity.showFragment(UsersFragment.
                        newInstance(followers, Parameters.followersTitle));
            }
        });
        followerCount.setText("");    // remove default text
        stringComponents.add(followerLink);
        StringFactory.stringBuilder(followerCount, stringComponents);
        stringComponents.clear();

        // Following count
        TextView followingCount = (TextView) profileView.findViewById(R.id.profile_following_count);
        text = Integer.toString(this.followingCount);
        SpannableString followingLink = StringFactory.createLink(text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // TODO: get followers from Network
                ArrayList<User> following = new ArrayList<>();
                int i;
                for(i = 0; i < 50; i++){
                    String username = Parameters.default_username+i;
                    following.add(new User(username, Parameters.default_emptyUserImageLink,
                            Parameters.default_profName));
                }


                // display followers
                Parameters.NavigationBarActivity.showFragment(UsersFragment.
                        newInstance(following, Parameters.followingTitle));
            }
        });
        followingCount.setText("");    // remove default text
        stringComponents.add(followingLink);
        StringFactory.stringBuilder(followingCount, stringComponents);
        stringComponents.clear();

        // Add post icons
        if(this.posts.size() == 0 && this.postCount == 0){
            TextView postFlag = (TextView) profileView.findViewById(R.id.profile_no_post_flag);
            postFlag.setVisibility(View.VISIBLE);
            getPostIcons(inflater);
        }
        return profileView;
    }

    public void getPostIcons(LayoutInflater inflater){

        Log.w("test", Integer.toString(this.postCount)+","+Integer.toString(this.posts.size()));

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

}
