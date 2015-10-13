package co.example.junjen.mobileinstagram.elements;

import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.UsersFragment;
import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ToggleButton;
import co.example.junjen.mobileinstagram.customLayouts.UserImageView;
import co.example.junjen.mobileinstagram.network.NetParams;


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
    private ToggleButton followButton;
    private TextView followingCountText;
    private int postIconCount = 0;

    private boolean privateProfile = false;

    public Profile(String userCount){
        // test constructor to create 'empty' Profile objects

        String imageLink = Parameters.default_emptyUserImageLink;
        if(userCount.equals(Parameters.default_username) ||
                userCount.equals(Parameters.default_userId) ||
                userCount.equals("")){
            userCount = "";
            imageLink = Parameters.default_loginUserImageLink;
        }

        this.username = new Username(Parameters.default_userId + userCount,
                Parameters.default_username + userCount);
        this.userImage = new Image(imageLink);
        this.profName = Parameters.default_profName + userCount;
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
                   int followingCount){

        this.username = user.getUsername();
        this.userImage = user.getUserImage();
        this.profName = user.getProfName();
        this.profDescrp = profDescrp;

        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;

        this.posts = new ArrayList<>();
    }

    // Profile type for private profiles
    public Profile(User user, String profDescrp){
        this.username = user.getUsername();
        this.userImage = user.getUserImage();
        this.profName = user.getProfName();
        this.profDescrp = profDescrp;
        this.postCount = 0;
        this.followerCount = 0;
        this.followingCount = 0;
        this.privateProfile = true;
        this.posts = new ArrayList<>();
    }

    // build the profile view
    public ExpandableScrollView getProfileView(LayoutInflater inflater){

        postIconCount = 0;

        profileView = (ExpandableScrollView)
                inflater.inflate(R.layout.fragment_profile, null, false);
        ArrayList<CharSequence> stringComponents = new ArrayList<>();
        String text;

        /** Fixed parameters **/

        // User image
        UserImageView userImage = (UserImageView)
                profileView.findViewById(R.id.profile_user_image);
        Image.setImage(userImage, this.userImage);

        // Profile name
        TextView profName = (TextView) profileView.findViewById(R.id.profile_name);
        profName.setText(this.profName);

        // Profile description
        TextView profDescrp = (TextView) profileView.findViewById(R.id.profile_description);
        if(this.profDescrp.equals("") || this.profDescrp == null){
            profDescrp.setVisibility(View.GONE);
        } else {
            profDescrp.setText(this.profDescrp);
        }

        // Post count
        TextView postCountText = (TextView) profileView.findViewById(R.id.profile_post_count);
        postCountText.setText(formatCount(this.postCount));

        // Follower count
        TextView followerCountText = (TextView) profileView.findViewById(R.id.profile_follower_count);
        text = formatCount(this.followerCount);
        SpannableString followerLink = StringFactory.createLink(text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<User> followers = new ArrayList<>();
                if(Parameters.dummyData) {
                    int i;
                    for (i = 0; i < 50; i++) {
                        followers.add(new User(Parameters.default_userId + (i + 1),
                                Parameters.default_username + (i + 1),
                                Parameters.default_emptyUserImageLink,
                                Parameters.default_profName));
                    }
                } else {
                    followers = NetParams.NETWORK.getFollowers();
                }
                // display followers
                Parameters.NavigationBarActivity.showFragment(UsersFragment.
                        newInstance(followers, Parameters.followersTitle));
            }
        });
        followerCountText.setText("");    // remove default text
        stringComponents.add(followerLink);
        StringFactory.stringBuilder(followerCountText, stringComponents);
        stringComponents.clear();

        // Following count
        buildFollowingCountView();

        // Follow button
        RadioGroup followButtonGroup = (RadioGroup)
                profileView.findViewById(R.id.profile_follow_button_group);
        if(username.getUsername().equals(Parameters.loginUsername)){
            followButtonGroup.setVisibility(View.GONE);
        } else {
            followButtonGroup.setVisibility(View.VISIBLE);
            followButton = (ToggleButton)
                    profileView.findViewById(R.id.profile_follow_button);

            if(!Parameters.dummyData) {
                checkIfFollowing(username.getUserId(), followButton);
            }

            // set listener to followButton
            followButton.setOnClickListener(new View.OnClickListener() {

                // Handle clicks for like button
                @Override
                public void onClick(View v) {
                    if (followButton.isChecked()) {
                        updateFollowingCount(true, username.getUserId());
                    } else {
                        updateFollowingCount(false, username.getUserId());
                    }
                }
            });
        }

        // No posts flag
        if(this.posts.size() == 0 && this.postCount == 0) {
            TextView postFlag = (TextView) profileView.findViewById(R.id.profile_no_post_flag);
            postFlag.setVisibility(View.VISIBLE);
        }

        // Private profile flag
        if(privateProfile){
            View privateProfileFlag = profileView.findViewById(R.id.private_profile_flag);
            privateProfileFlag.setVisibility(View.VISIBLE);
        }

        return profileView;
    }

    // builds following count display during initialisation as well as after follow button clicks
    public void buildFollowingCountView(){
        ArrayList<CharSequence> stringComponents = new ArrayList<>();
        followingCountText = (TextView) profileView.findViewById(R.id.profile_following_count);
        String text = formatCount(this.followingCount);
        SpannableString followingLink = StringFactory.createLink(text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<User> following = new ArrayList<>();
                if(Parameters.dummyData) {
                    int i;
                    for (i = 0; i < 50; i++) {
                        following.add(new User(Parameters.default_userId + (i + 1),
                                Parameters.default_username + (i + 1),
                                Parameters.default_emptyUserImageLink,
                                Parameters.default_profName));
                    }
                } else {
                    following = NetParams.NETWORK.getFollowing();
                }

                // update following list to remove users that current user has unfollowed
                Iterator<User> iter = following.iterator();
                User user;
                while (iter.hasNext()){
                    user = iter.next();
                    if(Parameters.userIdToUnfollow.contains(user.getUsername().getUserId())){
                        iter.remove();
                    }
                }

                // display followers
                Parameters.NavigationBarActivity.showFragment(UsersFragment.
                        newInstance(following, Parameters.followingTitle));
            }
        });
        followingCountText.setText("");    // remove default text
        stringComponents.add(followingLink);
        StringFactory.stringBuilder(followingCountText, stringComponents);
        stringComponents.clear();
    }

    // builds and displays post icons in profile view
    public void getPostIcons(LayoutInflater inflater){

        Log.w("test", Integer.toString(this.postCount) + "," + Integer.toString(this.posts.size()));

        int postsSize = this.posts.size();

        if (postIconCount < postCount) {
            ArrayList<Post> posts = new ArrayList<>();

            if (!Parameters.dummyData){
                if (postsSize == 0){
                    posts = NetParams.NETWORK.getProfileFeed(username.getUserId(), null, null);
                } else {
                    String maxId = this.posts.get(postsSize - 1).getPostId();
                    posts = NetParams.NETWORK.getProfileFeed(username.getUserId(), null, maxId);
                }
                this.posts.addAll(posts);
            } else {
                // if showing dummy data
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
            }
            Post.buildPostIcons(inflater,
                    (LinearLayout) profileView.findViewById(R.id.profile_post_icons),
                    posts, Parameters.postIconsPerRow, Parameters.postIconRowsToLoad);

            postIconCount += posts.size();
        }
    }

    // get relationship of this user to current user and set toggle accordingly
    public static void checkIfFollowing(String userId, ToggleButton followButton){

        // get relationship of this user to current user and set toggle accordingly
        String following = NetParams.NETWORK.checkIfFollowing(userId);

        if ((following.equals(Parameters.follows_key)
                || Parameters.userIdToFollow.contains(userId))){
            checkFollowButton(followButton, true);
        }
        else if ((!following.equals(Parameters.follows_key)
                || Parameters.userIdToUnfollow.contains(userId))){
            checkFollowButton(followButton, false);
        }
    }

    // update following counts as seen on current user profile if following or unfollowing a user
    public static void updateFollowingCount(boolean follow, String userId){

        int count = Parameters.loginProfile.getFollowingCount();

        if(follow){
            Log.w("test", "add following count");
            Parameters.loginProfile.setFollowingCount(count + 1);
        } else {
            Log.w("test", "subtract following count");
            Parameters.loginProfile.setFollowingCount(count - 1);
        }
        Parameters.loginProfile.buildFollowingCountView();
        updateFollowLists(userId, follow);
    }

    // update users to follow or not follow
    public static void updateFollowLists(String userId, boolean follow){
        if (follow){
            Parameters.userIdToUnfollow.remove(userId);
            Parameters.userIdToFollow.add(userId);
        }
        else if (!follow){
            Parameters.userIdToFollow.remove(userId);
            Parameters.userIdToUnfollow.add(userId);
        }
    }

    // updates follow button
    public static void checkFollowButton(ToggleButton followButton, boolean follow){
        if(followButton != null) {
            if (follow) {
                followButton.setChecked(true);
            } else {
                ((RadioGroup) followButton.getParent()).clearCheck();
            }
        }
    }

    // format counts into thousands or millions if applicable
    private String formatCount(int count){

        String formattedCount = Integer.toString(count);
        float convertedCount;
        int million = 1000000;
        int hundredThousand = 100000;
        int tenThousand = 10000;
        int thousand = 1000;

        // eg: 1.1m
        if (count >= million){
            convertedCount = (float)count/million;
            formattedCount = String.format("%.1f", convertedCount) + "m";
        }
        // eg: 111k
        else if (count >= hundredThousand){
            convertedCount = (float)count/thousand;
            formattedCount = String.format("%.0f", convertedCount) + "k";
        }
        // eg: 11.1k
        else if (count >= tenThousand){
            convertedCount = (float)count/thousand;
            formattedCount = String.format("%.1f", convertedCount) + "k";
        }
        return formattedCount;
    }

    public void setFollowingCount(int count){
        this.followingCount = count;
    }

    public Username getUsername() {
        return username;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getPostIconCount() {
        return postIconCount;
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
