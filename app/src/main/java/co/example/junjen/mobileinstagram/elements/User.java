package co.example.junjen.mobileinstagram.elements;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.ToggleButton;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates User objects.
 */
public class User implements Serializable{

    private Username username;
    private Image userImage;
    private String profName;

    public User(String count){
        this.username = new Username(Parameters.default_userId + count,
                Parameters.default_username + count);
        this.userImage =  new Image(Parameters.default_emptyUserImageLink);;
        this.profName = Parameters.default_profName;
    }

    public User(String userId, String username, String userImage, String profName){
        this.username = new Username(userId, username);
        this.userImage =  new Image(userImage);;
        this.profName = profName;
    }

    public static void buildUserElement(final User user, View userElement){

        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        ImageView userImage = (ImageView) userElement.findViewById(R.id.user_user_image);
        final TextView username = (TextView) userElement.findViewById(R.id.user_username);
        TextView profName = (TextView) userElement.findViewById(R.id.user_prof_name);
        final ToggleButton followButton = (ToggleButton)
                userElement.findViewById(R.id.user_follow_button);


        username.setText("");   // remove default text
        stringComponents.add(user.getUsername().getUsernameLink());
        StringFactory.stringBuilder(username, stringComponents);
        stringComponents.clear();

        Profile.checkIfFollowing(user.getUsername().getUserId(), followButton);

        // set listener to followButton
        followButton.setOnClickListener(new View.OnClickListener() {

            // Handle clicks for like button
            @Override
            public void onClick(View v) {
                if (followButton.isChecked()) {
                    Profile.updateFollowingCount(true, user.getUsername().getUserId());
                } else {
                    Profile.updateFollowingCount(false, user.getUsername().getUserId());
                }
            }
        });

        // set user image
        Image.setImage(userImage, user.getUserImage());

        // set user profile name
        String text = user.getProfName();
        if (text == null || text.equals("")) {
            profName.setVisibility(View.GONE);
        } else {
            profName.setText(text);
        }

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
}
