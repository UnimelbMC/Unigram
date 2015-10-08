package co.example.junjen.mobileinstagram.network;

import android.util.Log;

import org.jinstagram.Instagram;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.entity.likes.LikesFeed;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;
import java.util.List;

import co.example.junjen.mobileinstagram.elements.Comment;
import co.example.junjen.mobileinstagram.elements.User;
import co.example.junjen.mobileinstagram.elements.Location;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.elements.TimeSince;
import co.example.junjen.mobileinstagram.suggestion.Suggestion;

/**
 * Created by Jaime on 10/4/2015.
 */
public class Network {
    // Object used to retrieve data from Instagram API
    private Instagram instagram;
    private UserInfoData thisUserData;
    public Network() {
        instagram = new Instagram(NetParams.ACCESS_TOKEN);
        boolean gotData = true;
        Log.v("NETWORK", "enterConstructor");
        while(gotData) {
            try {
                thisUserData = instagram.getCurrentUserInfo().getData();
                gotData = false;
                Log.v("NETWORK", "accesstoken succeeded");
            } catch (InstagramException e) {
                Log.v("NETWORK", "accesstoken faileddddddddddd " + e.getMessage());
            }
        }
        Suggestion su = new Suggestion();

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
        return new ArrayList<Post>();
    }

    public Profile getUserProfileFeed(String username){
        String uImage;
        String profName;
        String profDesc;
        int postsCount;
        int followersCount;
        int followingCount;
        ArrayList<Post> thePosts = new ArrayList<>();
                  /*String username, String userimage, String profName, String profDescrp,
        String postsCount, String followersCount, String followingCount,ArrayList<Post> posts)*/

        try {
            MediaFeed mediaFeed;
            if(username=="") {
                mediaFeed = instagram.getRecentMediaFeed("self");
                username = thisUserData.getUsername();
                uImage = thisUserData.getProfilePicture();
                profName = thisUserData.getFullName();
                profDesc = thisUserData.getBio();
                postsCount = thisUserData.getCounts().getMedia();
                followersCount = thisUserData.getCounts().getFollowedBy();
                followingCount = thisUserData.getCounts().getFollows();
            }else{
                mediaFeed = instagram.getRecentMediaFeed(username);
                UserInfoData otherUser = instagram.getUserInfo(username).getData();

                uImage = otherUser.getProfilePicture();
                profName = otherUser.getFullName();
                profDesc = otherUser.getBio();
                postsCount = otherUser.getCounts().getMedia();
                followersCount = otherUser.getCounts().getFollowedBy();
                followingCount = otherUser.getCounts().getFollows();
            }
            List<MediaFeedData> mediaFeeds = mediaFeed.getData();
            Log.v("POST1",Integer.toString(mediaFeeds.size()));
            for (MediaFeedData thisPost : mediaFeeds) {
                Log.v("POST2",thisPost.toString());
                Location loc;
                if (thisPost.getLocation()== null){
                    loc = null;
                }else{
                    loc = new Location(thisPost.getLocation().getId(), thisPost.getLocation().getName(),
                            thisPost.getLocation().getLatitude(), thisPost.getLocation().getLongitude());
                }
                Post post = new Post(thisPost.getId(),thisPost.getUser().getProfilePictureUrl(),
                        thisPost.getUser().getUserName(),loc,
                        thisPost.getCreatedTime(), thisPost.getImages().getThumbnail().getImageUrl()
                        ,thisPost.getCaption().getText(),getLikesByPostId(thisPost.getId()),
                        getCommentsByPostId(thisPost.getId()));
                thePosts.add(post);
            }
            return new Profile(username, uImage, profName,
                  profDesc,postsCount, followersCount,
                    followingCount, thePosts);
        }catch(InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }
    //Get a media from instagram and return Post object for layout
    public Post getPostById(String postId){
      //  int postId, String userImage, String username, String location, String timeSince,
        //        String postImage, String caption, ArrayList< User > likes, ArrayList< Comment > comments
        try {
            MediaFeedData thisPost = instagram.getMediaInfo(postId).getData();
            Location loc;
            if (thisPost.getLocation()== null){
                loc = null;
            }else{
                loc = new Location(thisPost.getLocation().getId(), thisPost.getLocation().getName(),
                        thisPost.getLocation().getLatitude(), thisPost.getLocation().getLongitude());
            }
            return new Post(thisPost.getId(),thisPost.getUser().getProfilePictureUrl(),
                    thisPost.getUser().getUserName(),loc,
                    thisPost.getCreatedTime(), thisPost.getImages().getStandardResolution().getImageUrl(),
                    thisPost.getCaption().getText(),getLikesByPostId(postId),
                    getCommentsByPostId(postId));
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }
    //Build Comments list to return to layout
    public ArrayList<Comment> getCommentsByPostId(String postId){
        //String username, Image userImage, String comment, TimeSince timeSince)
        try {
            MediaCommentsFeed commentsFeed = instagram.getMediaComments(postId);
            List<CommentData> comments = commentsFeed.getCommentDataList();
            ArrayList<Comment> result = new ArrayList<>();
            for (CommentData c : comments){
                Comment newComment = new Comment(c.getCommentFrom().getUsername(),
                        c.getCommentFrom().getProfilePicture(),c.getText(),new TimeSince(
                        c.getCreatedTime()));
                result.add(newComment);
            }
            return result;
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }
    //Build Likes list to return to layout
    public ArrayList<User> getLikesByPostId(String postId){
        //String username, Image userImage, String profName, TimeSince timeSince
        try {
            LikesFeed feed = instagram.getUserLikes(postId);
            List<org.jinstagram.entity.common.User> users = feed.getUserList();
            ArrayList<User> result = new ArrayList<>();
            for (org.jinstagram.entity.common.User u : users){
                User newLike = new User(u.getUserName(),u.getProfilePictureUrl(),u.getFullName());
                result.add(newLike);
            }
            return result;
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }
}
