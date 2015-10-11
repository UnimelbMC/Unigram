package co.example.junjen.mobileinstagram.elements;

import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;

import java.io.Serializable;

import co.example.junjen.mobileinstagram.NavigationBar;
import co.example.junjen.mobileinstagram.ProfileFragment;
import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.network.NetParams;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Username objects.
 * Handles username clicks for profile popup.
 */

public class Username implements Serializable{

    private String userId;
    private String username;
    private SpannableString usernameLink;

    public Username(String userId, String username) {
        this.userId = userId;
        this.username = username;
        String username_link = "<b>"+this.username+"</b>";
        this.usernameLink = StringFactory.createLink(Html.fromHtml(username_link), getOnClickListener());
    }

    // on click action for username links to display user profile when clicked
    private View.OnClickListener getOnClickListener(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // show loading animation
                Parameters.NavigationBarActivity.
                        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                // display profile of username
                Parameters.NavigationBarActivity.
                        showFragment(ProfileFragment.newInstance(userId, true));

                Log.w("like",userId);
            }
        };
        return onClickListener;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId(){
        return userId;
    }

    public SpannableString getUsernameLink() {
        return usernameLink;
    }

    public String toString() {
        return this.username;
    }
}