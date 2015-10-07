package co.example.junjen.mobileinstagram.network;

import android.util.Log;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.Profile;

/**
 * Created by Jaime on 10/4/2015.
 */
public class Network {
    // Object used to retrieve data from Instagram API
    private Instagram instagram;
    private UserInfoData thisUserData;
    public Network() {
        instagram = new Instagram(NetParams.ACCESS_TOKEN);
        boolean gotData = false;
        Log.v("NETWORK", "enterConstructor");
        while(gotData) {
            try {
                thisUserData = instagram.getCurrentUserInfo().getData();
                gotData = true;
                Log.v("NETWORK", "accesstoken succeeded");
            } catch (InstagramException e) {
                Log.v("NETWORK", "accesstoken faileddddddddddd " + e.getMessage());
            }
        }
    }

    public String getProfilePic(){
        if (thisUserData.getProfilePicture()!= null) {
            Log.v("NETWORK", "getProfilePic " + thisUserData.getProfilePicture());
            return thisUserData.getProfilePicture();
        }
        return "profilePic";
    }
    public String getUsername(){
        Log.v("NETWORK", "getUsername " + thisUserData.getUsername());
        return thisUserData.getUsername();
    }

    //Build profile view from userData
    public Profile getProfileFromAPI(){
        return getProfileFromAPI("");
    }
    public Profile getProfileFromAPI(String username){
    /*String username, String userimage, String profName, String profDescrp,
        String postsCount, String followersCount, String followingCount,ArrayList<Post> posts)*/
        UserInfoData uData;
        try {
            if (username == "") {
                uData = thisUserData;
            }else {
                uData = instagram.getUserInfo(username).getData();
            }
            return new Profile(uData.getUsername(), uData.getProfilePicture(), uData.getFirstName(),
                    uData.getBio(), uData.getCounts().getMedia(), uData.getCounts().getFollowedBy(),
                    uData.getCounts().getFollows(), buildPostList());
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Post> buildPostList(){
        return null;
    }

    public Post getPostById(int postId){
        return null;
    }
}
