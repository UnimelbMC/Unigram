package co.example.junjen.mobileinstagram.elements;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    public static boolean dummyData = true;
    public static final String login_key = "@thisIsMyLogin";
    public static String loginUserId = null;
    public static String loginUsername = null;
    public static User loginUser = null;
    public static Profile loginProfile = null;
    public static ExpandableScrollView loginProfileView = null;

    // Titles
    public static final String mainTitleBuffer = "     ";
    public static final String titleBuffer = "       ";
    public static final String mainTitleText = "UniGram";
    public static final String mainTitle = mainTitleBuffer+mainTitleText;
    public static final String commentsTitle = titleBuffer+"COMMENTS";
    public static final String likesTitle = titleBuffer+"LIKERS";
    public static final String followersTitle = titleBuffer+"FOLLOWERS";
    public static final String followingTitle = titleBuffer+"FOLLOWING";
    public static final String activityTitle = titleBuffer+"ACTIVITY";
    public static final String discoverTitle = titleBuffer+"DISCOVER";
    public static final String postTitle = titleBuffer+"PHOTO";
    public static final float mainTitleSize = 20;
    public static final float subTitleSize = 15;

    // Login screen parameters
    public static final int splashScreenDuration = 2500;    // in milliseconds

    // User feed parameters
    public static final boolean default_sortByTime = true;

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
    public static final String swipe_toast_message = "Post swiped!";

    // Swipe parameters
    public static final int SWIPE_MIN_DISTANCE = 120;
    public static final int SWIPE_MAX_OFF_PATH = 50;
    public static final int SWIPE_THRESHOLD_VELOCITY = 200;
    public static final String default_swipeText = "Swiped from ";

    // Discover parameters
    public static final int default_suggestions = 10;
    public static final int default_usersToSearch = 10;
    public static final int searchedUsersToReturn = 14;

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
    public static Set<String> userIdToFollow = new HashSet<>();
    public static Set<String> userIdToUnfollow = new HashSet<>();
    public static Set<String> postIdToLike = new HashSet<>();
    public static Set<String> postIdToUnlike = new HashSet<>();

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

    //Device location params
    public static double DEV_LATITUDE = -37.8138434;
    public static double DEV_LONGITUDE = 144.9595481;
    public static boolean LOC_DONE = false;

}