package co.example.junjen.mobileinstagram.network;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.basicinfo.UserInfo;
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
        instagram = new Instagram(Params.ACCESS_TOKEN);
        try {
            thisUserData = instagram.getCurrentUserInfo().getData();
        } catch (InstagramException e) {
            e.printStackTrace();
        }
    }

    public String getProfilePic(){
        return thisUserData.getProfilePicture();
    }
    public String getUsername(){
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
