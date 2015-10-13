package co.example.junjen.mobileinstagram.elements;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
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

    private ToggleButton followButton;

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

    public void buildUserElement(View userElement){

        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        ImageView userImage = (ImageView) userElement.findViewById(R.id.user_user_image);
        TextView username = (TextView) userElement.findViewById(R.id.user_username);
        TextView profName = (TextView) userElement.findViewById(R.id.user_prof_name);
        followButton = (ToggleButton)
                userElement.findViewById(R.id.user_follow_button);

        username.setText("");   // remove default text
        stringComponents.add(this.username.getUsernameLink());
        StringFactory.stringBuilder(username, stringComponents);
        stringComponents.clear();

        if(!Parameters.dummyData) {
            Profile.checkIfFollowing(this.username.getUserId(), followButton);
        }

        // set listener to followButton if user is not login user
        if(!this.username.getUsername().equals(Parameters.loginUsername)){
            followButton.setOnClickListener(followOnClickListener());
        } else {
            ((RadioGroup) followButton.getParent()).setVisibility(View.GONE);
        }


        // set user image
        Image.setImage(userImage, this.userImage);

        // set user profile name
        String text = this.profName;
        if (text == null || text.equals("")) {
            profName.setVisibility(View.GONE);
        } else {
            profName.setText(text);
        }
    }

    // listener for follow button
    public View.OnClickListener followOnClickListener(){
        View.OnClickListener followOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followButton.isChecked()) {
                    Profile.updateFollowingCount(true, User.this.getUsername().getUserId());
                } else {
                    Profile.updateFollowingCount(false, User.this.getUsername().getUserId());
                }
            }
        };
        return followOnClickListener;
    }

    // updates the follow button
    public void updateFollowButton(){
        if(Parameters.userIdToFollow.contains(username.getUserId())){
            Profile.checkFollowButton(followButton, true);
        } else if (Parameters.userIdToUnfollow.contains(username.getUserId())){
            Profile.checkFollowButton(followButton, false);
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

    public ToggleButton getFollowButton() {
        return followButton;
    }
}
