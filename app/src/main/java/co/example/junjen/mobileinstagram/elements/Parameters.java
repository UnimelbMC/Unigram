package co.example.junjen.mobileinstagram.elements;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import co.example.junjen.mobileinstagram.NavigationBar;
import co.example.junjen.mobileinstagram.R;

/**
 * Created by junjen on 1/10/2015.
 */
public class Parameters {

    // Titles
    public static final String mainTitle = "UniGram";
    public static final String commentsTitle = "COMMENTS";
    public static final String likesTitle = "LIKERS";
    public static final String followersTitle = "FOLLOWERS";
    public static final String followingTitle = "FOLLOWING";
    public static final String postTitle = "PHOTO";
    public static final float mainTitleSize = 20;
    public static final float subTitleSize = 15;

    // Login screen parameters
    public static final String usernameFieldHint = "Username";
    public static final String passwordFieldHint = "Password";
    public static final String loginUsername_key = "username";
    public static final String loginPassword_key = "password";
    public static final String loginUserImage_key = "loginUserImage";
    public static final int splashScreenDuration = 2500;    // in milliseconds

    // Post parameters
    public static final String default_postId = "";
    public static final String default_username = "#username";
    public static final String default_location = "Location";
    public static final String default_timeSince = "TimeSince";
    public static final String default_caption = "Caption";
    public static final String default_comment = "Comment";
    public static final String default_image = "#Image";
    public static final int likeThreshold = 10;
    public static final int commentThreshold = 3;
    public static final int postsToLoad = 10;
    public static final int loadCommentThreshold = 20;
    public static final int loadLikeThreshold = 100;
    public static final int maxLikes = 300;

    // Post icon parameters
    public static final int postIconsPerRow = 3;
    public static final int postIconRowsToLoad = 5;

    // Profile parameters
    public static final String default_profName = "Username";
    public static final String default_profDescrp = "My Profile";

    // Image Links
    public static final String default_loginUserImageLink = "0";
    public static final String default_emptyUserImageLink = "1";

    // Application helpers
    public static Context MainActivityContext = null;
    public static NavigationBar NavigationBarActivity = null;
    public static final int loginClickInBrowserCountMax = 2;
    public static final int urlCountMax = 2;
    public static final int logoutBrowserCountMax = 3;
    public static int NavigationViewHeight = 0;


    public static void setTitle(AppCompatActivity activity, String title, float titleSize){
        View actionBar = activity.getSupportActionBar().getCustomView();
        if (actionBar != null) {
            TextView titleTextView = (TextView) actionBar.findViewById(R.id.action_bar_title);
            titleTextView.setText(title);
            titleTextView.setTextSize(titleSize);
        }
    }

}
