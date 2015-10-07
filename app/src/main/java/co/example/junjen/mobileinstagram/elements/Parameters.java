package co.example.junjen.mobileinstagram.elements;

import android.content.Context;

/**
 * Created by junjen on 1/10/2015.
 */
public class Parameters {

    // Titles
    public static final String mainTitle = "UniGram";
    public static final String commentsTitle = "COMMENTS";
    public static final String likesTitle = "LIKERS";
    public static final float mainTitleSize = 20;
    public static final float subTitleSize = 15;

    // Login screen parameters
    public static final String usernameFieldHint = "Username";
    public static final String passwordFieldHint = "Password";
    public static final String loginUsername_key = "username";
    public static final String loginPassword_key = "password";
    public static final String loginUserImage_key = "loginUserImage";
    public static final String loginToken_key = "token";
    public static final int splashScreenDuration = 2500;    // in milliseconds

    // Post parameters
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
    public static Context context = null;
    public static final String navBarCreated = "navBarCreated";
    public static final int loginClickInBrowserCountMax = 2;
    public static final int urlCountMax = 2;
    public static final int logoutBrowserCountMax = 3;

}
