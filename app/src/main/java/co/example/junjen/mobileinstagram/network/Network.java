package co.example.junjen.mobileinstagram.network;

import android.util.Log;

import org.jinstagram.Instagram;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.entity.likes.LikesFeed;
import org.jinstagram.entity.relationships.RelationshipData;
import org.jinstagram.entity.relationships.RelationshipFeed;
import org.jinstagram.entity.users.basicinfo.Counts;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.ArrayList;
import java.util.List;

import co.example.junjen.mobileinstagram.elements.Comment;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.User;
import co.example.junjen.mobileinstagram.elements.Location;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.elements.TimeSince;
import co.example.junjen.mobileinstagram.suggestion.Classification;

/**
 * Created by Jaime on 10/4/2015.
 */
public class Network {
    // Object used to retrieve data from Instagram API
    private final int MAX_USER_FEED_POSTS = 9;
    private Instagram instagram;
    private UserInfoData thisUserData;
    private ArrayList<Post> fakePost = new ArrayList<>();

    public Network() {
        instagram = new Instagram(NetParams.ACCESS_TOKEN);
        int gotData = 0;
        fakePost.add(new Post());
        Log.v("NETWORK", "enterConstructor");

        while(gotData<10) {
            try {
                thisUserData = instagram.getCurrentUserInfo().getData();
                Log.v("NETWORK", "accesstoken success");
                gotData = 100;
                Classification cls = new Classification("self");
                return;
            } catch (InstagramException e) {
                Log.v("NETWORK", "accesstoken faileddddddddddd " + e.getMessage());
                gotData += 1;
            }
        }

        thisUserData = new UserInfoData();
        thisUserData.setUsername(Parameters.default_username);
        thisUserData.setId(Parameters.default_userId);
        thisUserData.setBio(Parameters.default_profDescrp);
        thisUserData.setCounts(new Counts());
        thisUserData.setProfilePicture(Parameters.default_image);
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

    private ArrayList<Post> buildPostList(){
        return new ArrayList<Post>();
    }
    public Profile getUserProfileFeed(String userId){
        return getUserProfileFeed(userId, null, null);
    }

    public Profile getUserProfileFeed(String userId, String minId, String maxId){
        String username;
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
            if(userId.equals(Parameters.login_key)) {
                mediaFeed = instagram.getRecentMediaFeed(
                        "self", MAX_USER_FEED_POSTS,minId,maxId,null,null);
                userId = thisUserData.getId();
                username = thisUserData.getUsername();
                uImage = thisUserData.getProfilePicture();
                profName = thisUserData.getFullName();
                profDesc = thisUserData.getBio();
                postsCount = thisUserData.getCounts().getMedia();
                followersCount = thisUserData.getCounts().getFollowedBy();
                followingCount = thisUserData.getCounts().getFollows();
            }else{
                mediaFeed = instagram.getRecentMediaFeed(
                        userId, MAX_USER_FEED_POSTS,minId,maxId,null,null);
                UserInfoData otherUser = instagram.getUserInfo(userId).getData();
                username = otherUser.getUsername();
                uImage = otherUser.getProfilePicture();
                profName = otherUser.getFullName();
                profDesc = otherUser.getBio();
                postsCount = otherUser.getCounts().getMedia();
                followersCount = otherUser.getCounts().getFollowedBy();
                followingCount = otherUser.getCounts().getFollows();
            }
            List<MediaFeedData> mediaFeeds = mediaFeed.getData();
            thePosts = getPostsList(mediaFeeds, false);
            Log.v("NETWORK","thePosts size() "+Integer.toString(thePosts.size()));
            return new Profile(new User(userId, username, uImage, profName),
                  profDesc, postsCount, followersCount, followingCount, thePosts);
        }catch(InstagramException e) {
            e.printStackTrace();
            return new Profile(Parameters.default_username);
        }

    }
    public ArrayList<Post> getUserFeed(){
        return getUserFeed(null, null);
    }
    public ArrayList<Post> getUserFeed(String minId, String maxId){

        try {
            MediaFeed feed = instagram.getUserFeeds(maxId, minId, Parameters.postsToLoad);
            List<MediaFeedData> userFeed = feed.getData();
            return getPostsList(userFeed,false);
        } catch (InstagramException e) {
            e.printStackTrace();
           return fakePost;
        }
    }
    //Get arrayList of Posts
    public ArrayList<Post> getPostsList(List<MediaFeedData> mediaFeeds,boolean thumb){
        ArrayList<Post> thePosts = new ArrayList<>();
        for (MediaFeedData thisPost : mediaFeeds) {
            Post post = buildPost(thisPost, thumb);
            thePosts.add(post);
        }
        Log.v("NETWORK","size of the post from ingram"+Integer.toString(thePosts.size()));
        return thePosts;
    }

    //Get a media from instagram and return Post object for layout
    public Post getPostById(String postId){
      //  int postId, String userId, String userImage, String username, String location, String timeSince,
        //        String postImage, String caption, ArrayList< User > likes, ArrayList< Comment > comments
        try {
            MediaFeedData thisPost = instagram.getMediaInfo(postId).getData();
            Post post = buildPost(thisPost, false);
            return post;
        } catch (InstagramException e) {
            e.printStackTrace();
            return new Post();
        }
    }

    // creates Post object based on post data
    public Post buildPost(MediaFeedData postData, boolean thumb){

        Location loc = null;
        String cap = null;
        String imgUrl;
        if (postData.getLocation()!= null){
            loc = new Location(postData.getLocation().getId(), postData.getLocation().getName(),
                    postData.getLocation().getLatitude(), postData.getLocation().getLongitude());
        }
        if (postData.getCaption()!= null){
            cap = postData.getCaption().getText();
        }
        if (thumb){
            imgUrl = postData.getImages().getThumbnail().getImageUrl();
        } else {
            imgUrl = postData.getImages().getStandardResolution().getImageUrl();
        }

        // get likes
        int likeCount = postData.getLikes().getCount();
        ArrayList<User> likes = new ArrayList<>();
        if (likeCount <= Parameters.likeThreshold){
            likes = getLikesByPostId(postData.getId());
        }

        Post post = new Post(postData.getId(), postData.getUser().getId(),
                postData.getUser().getProfilePictureUrl(), postData.getUser().getUserName(),loc,
                postData.getCreatedTime(), imgUrl, cap, likeCount,
                postData.getComments().getCount(), likes,
                getCommentsByPostId(postData.getId(), true));

        return post;
    }

    //Build Comments list to return to layout
    public ArrayList<Comment> getCommentsByPostId(String postId, boolean preview){
        //String userId, String username, Image userImage, String comment, TimeSince timeSince)
        try {
            MediaCommentsFeed commentsFeed = instagram.getMediaComments(postId);
            List<CommentData> comments;

            // preview of comments displayed with each post
            if(preview){
                int size = commentsFeed.getCommentDataList().size();
                int startIndex;
                if(size >= Parameters.commentPreviewThreshold){
                    startIndex = size - Parameters.commentPreviewThreshold;
                } else {
                    startIndex = 0;
                }
                comments = commentsFeed.getCommentDataList().subList(startIndex, size);
            } else {
                comments = commentsFeed.getCommentDataList();
            }
            ArrayList<Comment> result = new ArrayList<>();
            for (CommentData c : comments){
                Comment newComment = new Comment(c.getCommentFrom().getId(), c.getCommentFrom().getUsername(),
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
                User newLike = new User(u.getId(), u.getUserName(),u.getProfilePictureUrl(),u.getFullName());
                result.add(newLike);
            }

            return result;
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }

    // check if current user is following a given user
    public RelationshipData checkIfFollowing(String userId){
        try {
            RelationshipFeed feed = instagram.getUserRelationship(userId);
            return feed.getData();
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }
}
