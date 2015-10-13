package co.example.junjen.mobileinstagram.elements;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.NavigationBar;
import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;

/**
 * Created by junjen on 1/10/2015.
 *
 * Holds parameters used in layout generation
 */
public class Parameters {

    // Login parameters
    public static boolean dummyData = false;
    public static final String login_key = "@thisIsMyLogin";
    public static String loginUserId = null;
    public static String loginUsername = null;
    public static User loginUser = null;
    public static Profile loginProfile = null;
    public static ExpandableScrollView loginProfileView = null;

    // Titles
    public static final String mainTitle = "UniGram";
    public static final String commentsTitle = "COMMENTS";
    public static final String likesTitle = "LIKERS";
    public static final String followersTitle = "FOLLOWERS";
    public static final String followingTitle = "FOLLOWING";
    public static final String activityTitle = "ACTIVITY";
    public static final String discoverTitle = "DISCOVER";
    public static final String postTitle = "PHOTO";
    public static final float mainTitleSize = 20;
    public static final float subTitleSize = 15;

    // Login screen parameters
    public static final int splashScreenDuration = 2500;    // in milliseconds

    // Post parameters
    public static final String default_postId = "#";
    public static final String default_userId = "#";
    public static final String default_username = "#username";
    public static final String default_location = "Location";
    public static final String default_caption = "Caption";
    public static final String default_comment = "Comment";
    public static final String default_image = "#Image";
    public static final int default_likeCount = 20;
    public static final int default_commentCount = 50;
    public static final int likePreviewThreshold = 10;
    public static final int commentPreviewThreshold = 3;
    public static final int postsToLoad = 10;
    public static final int loadCommentThreshold = 20;
    public static final int loadUserThreshold = 100;
    public static final int maxUsers = 300;

    // Discover parameters
    public static final int default_suggestions = 10;
    public static final int default_usersToSearch = 10;
    public static final int searchedUsersToReturn = 19;

    // Activity Feed parameters
    public static final String activityFollowing_key = "following";
    public static final String activityYou_key = "you";
    public static final String initialActivityFeed = activityYou_key;

    // ActivityFollowingFragment parameters
    public static final int default_recentPostCount = 10;
    public static final int activityFollowingUsersToGet = 50;
    public static final int activityFollowingPostsToGet = 3;
    public static final int activityFollowingToLoad = 10;
    public static final int activityFollowingIconsPerRow = 6;
    public static final int activityFollowingRowsToLoad = 1;

    // ActivityYouFragment parameters
    public static final int activityYouIconsPerRow = 3;
    public static final int activityYouRowsToLoad = 5;

    // Like feedback animation parameters
    public static final int likeAppearDuration = 100;
    public static final int likeStayDuration = 800;
    public static final int likeDisappearDuration = 100;
    public static final float animationStartEndScale = 0;

    // Like flags
    public static final String checkLike = "check";
    public static final String like = "like";
    public static final String unlike = "unlike";

    // Post icon parameters
    public static final int postIconsPerRow = 3;
    public static final int postIconRowsToLoad = 5;

    // Profile parameters
    public static final String default_profName = "Username";
    public static final String default_profDescrp = "My Profile";
    public static final int default_postCount = 50;
    public static final int default_followerCount = 100;
    public static final int default_followingCount = 75;

    // Image Links
    public static final String default_loginUserImageLink = "0";
    public static final String default_emptyUserImageLink = "1";

    // Swipe parameters
    public static final int SWIPE_MIN_DISTANCE = 120;
    public static final int SWIPE_MAX_OFF_PATH = 50;
    public static final int SWIPE_THRESHOLD_VELOCITY = 200;

    // Application helpers
    public static Context MainActivityContext = null;
    public static NavigationBar NavigationBarActivity = null;
    public static Context NavigationBarContext = null;
    public static final int loginClickInBrowserCountMax = 2;
    public static final int urlCountMax = 2;
    public static final int logoutBrowserCountMax = 3;
    public static int NavigationViewHeight = 0;
    public static int loadingAnimationDelay = 0;
    public static int refreshReturnDelay = 2000;
    public static float refreshThreshold = 10;
    public static View NavigationBarView;

    // Follow UI helper
    public static final String follows_key = "follows";
    public static ArrayList<String> usersToFollow = new ArrayList<>();
    public static ArrayList<String> usersToUnfollow = new ArrayList<>();
    public static ArrayList<String> postsToLike = new ArrayList<>();
    public static ArrayList<String> postsToUnlike = new ArrayList<>();

    // Time constants
    public static final int minute = 60;
    public static final int hour = 60 * minute;
    public static final int day = 24 * hour;
    public static final int week = 7 * day;
    public static final int month = 30 * day;

    public static void setTitle(AppCompatActivity activity, String title, float titleSize){
        View actionBar = activity.getSupportActionBar().getCustomView();
        if (actionBar != null) {
            TextView titleTextView = (TextView) actionBar.findViewById(R.id.action_bar_title);
            titleTextView.setText(title);
            titleTextView.setTextSize(titleSize);
        }
    }
}