package co.example.junjen.mobileinstagram.elements;

import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;

import java.io.Serializable;

import co.example.junjen.mobileinstagram.NavigationBar;
import co.example.junjen.mobileinstagram.ProfileFragment;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Username objects.
 * Handles username clicks for profile popup.
 */

public class Username implements Serializable{

    private String username;
    private SpannableString username_link;

    public Username(String username) {
        this.username = username;
        String username_link = "<b>"+this.username+"</b>";
        this.username_link = StringFactory.createLink(Html.fromHtml(username_link), getOnClickListener());
    }

    // on click action for username links to display user profile when clicked
    private View.OnClickListener getOnClickListener(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Profile profile = null;
                NavigationBar navActivity = ((NavigationBar) v.getContext());

                if(username.startsWith(Parameters.default_username)){
                    profile = new Profile();
                    profile.setUsername(username);
                } else {
                    // TODO:respond to click by creating profile object from username
                }

                // display profile of username
                navActivity.showFragment(ProfileFragment.newInstance(profile, true));
            }
        };
        return onClickListener;
    }

    public String getUsername() {
        return username;
    }

    public SpannableString getUsername_link() {
        return username_link;
    }

    public String toString() {
        return this.username;
    }
}