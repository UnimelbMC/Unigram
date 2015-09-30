package co.example.junjen.mobileinstagram.elements;

import android.text.Html;
import android.text.SpannableString;
import android.view.View;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Username objects.
 * Handles username clicks for profile popup.
 */

public class Username {

    private String username;
    private SpannableString username_link;

    public Username(String username) {
        this.username = username;

        String username_link = "<b>"+this.username+"</b>";

        this.username_link = StringFactory.createLink(Html.fromHtml(username_link), getOnClickListener());
    }

    private View.OnClickListener getOnClickListener(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:respond to click
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

    // TODO: onClickListener for username


}