package co.example.junjen.mobileinstagram.network;

import android.util.Log;

import com.google.gson.Gson;

import org.jinstagram.Instagram;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.entity.likes.LikesFeed;
import org.jinstagram.entity.relationships.RelationshipFeed;
import org.jinstagram.entity.users.basicinfo.Counts;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import co.example.junjen.mobileinstagram.elements.ActivityFollowing;
import co.example.junjen.mobileinstagram.elements.Comment;
import co.example.junjen.mobileinstagram.elements.Location;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.elements.TimeSince;
import co.example.junjen.mobileinstagram.elements.User;
import co.example.junjen.mobileinstagram.elements.Username;

/**
 * Created by Jaime on 10/4/2015.
 */
public class Network {
    // Object used to retrieve data from Instagram API
    private final int MAX_USER_FEED_POSTS =
            Parameters.postIconsPerRow * Parameters.postIconRowsToLoad;
    private final int MAX_ACTIVITY_FOLLOWING = Parameters.activityFollowingUsersToGet;
    private Instagram instagram;
    private UserInfoData thisUserData;
    private ArrayList<Post> fakePost = new ArrayList<>();
    private boolean first = true;

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
                return;
            } catch (InstagramException e) {
                Log.v("NETWORK", "accesstoken failed " + e.getMessage());
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
    //GEt instagram object
    public Instagram getInstagram(){
        return this.instagram;
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
    public Profile getUserProfileInfo(String userId){

        return getUserProfileInfo(userId, null, null);
    }

    public Profile getUserProfileInfo(String userId, String minId, String maxId){
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
            if(userId.equals(Parameters.login_key)) {
                userId = thisUserData.getId();
                username = thisUserData.getUsername();
                uImage = thisUserData.getProfilePicture();
                profName = thisUserData.getFullName();
                profDesc = thisUserData.getBio();
                postsCount = thisUserData.getCounts().getMedia();
                followersCount = thisUserData.getCounts().getFollowedBy();
                followingCount = thisUserData.getCounts().getFollows();
            }else{
                UserInfoData otherUser = instagram.getUserInfo(userId).getData();
                username = otherUser.getUsername();
                uImage = otherUser.getProfilePicture();
                profName = otherUser.getFullName();
                profDesc = otherUser.getBio();
                postsCount = otherUser.getCounts().getMedia();
                followersCount = otherUser.getCounts().getFollowedBy();
                followingCount = otherUser.getCounts().getFollows();
            }
            Log.v("NETWORK","thePosts size() "+Integer.toString(thePosts.size()));
            return new Profile(new User(userId, username, uImage, profName),
                    profDesc, postsCount, followersCount, followingCount);
        }catch(InstagramException e) {
            e.printStackTrace();
            Log.w("test", e.getMessage());
            return null;
        }
    }
    public ArrayList<Post> getProfileFeed(String userId, String minId, String maxId){
        return getProfileFeed(userId, 0, minId, maxId, null, null);
    }
    public ArrayList<Post> getProfileForActivityFeed(String userId, long maxDate, long minDate){

            Date max = null;
            Date min = null;
            if(maxDate != 0){
                max = new java.util.Date(maxDate*1000);
            }
            if(minDate!=0){
                min = new java.util.Date(maxDate*1000);
            }
        return getProfileFeed(userId,3,null,null,max,min);

    }
    // get a profile's feed
    public ArrayList<Post> getProfileFeed(String userId,int count, String minId, String maxId, Date minDate,Date maxDate){
        try {
            if (count == 0){
                count = MAX_USER_FEED_POSTS;
            }
            MediaFeed mediaFeed = instagram.getRecentMediaFeed(
                    userId, count,minId,maxId,maxDate,minDate);
            List<MediaFeedData> mediaFeeds = mediaFeed.getData();
            return getPostsList(mediaFeeds, true);
        }catch(InstagramException e) {
            e.printStackTrace();
            Log.w("test", e.getMessage());
            return null;
        }
    }

    public ArrayList<Post> getUserFeed(){
        return getUserFeed(null, null);
    }
    public ArrayList<Post> getUserFeed(String minId, String maxId){

        try {

            MediaFeed feed;
           /* if(first){
                feed = instagram.getUserFeeds();
                first = false;
            }else{*/
                feed = instagram.getUserFeeds(maxId, minId, Parameters.postsToLoad);
            //}

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

    //Get arrayList of liked Posts for ActivityYou
    public ArrayList<Post> getPostsLikedList(List<MediaFeedData> mediaFeeds,boolean thumb){
        ArrayList<Post> thePosts = new ArrayList<>();
        int i =0;
        for (MediaFeedData thisPost : mediaFeeds) {
            // omit posts that are in posts to unlike set (from UI clicks)
            if(!Parameters.postIdToUnlike.contains(thisPost.getId())) {
                Post post = buildPost(thisPost, thumb);
                Parameters.postIdToLike.remove(post.getPostId());
                thePosts.add(post);
            }
            i++;
        }
        // add posts that are in the posts to like set (from UI clicks)
        for (String postId : Parameters.postIdToLike){
            thePosts.add(0, getPostById(postId));
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

        Location loc = null;// new Location();
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
            imgUrl = postData.getImages().getLowResolution().getImageUrl();
        }

        // get likes
        int likeCount = postData.getLikes().getCount();
        ArrayList<User> likes = new ArrayList<>();
        if (likeCount <= Parameters.likePreviewThreshold + 1){
            likes = getLikesByPostId(postData.getId());
        }

        Post post = new Post(postData.getId(),
                new Username(postData.getUser().getId(), postData.getUser().getUserName()),
                postData.getUser().getProfilePictureUrl(),loc,
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

    // get current user's followers
    public ArrayList<User> getFollowers(){
        //String username, Image userImage, String profName, TimeSince timeSince
        try {
            UserFeed feed = instagram.getUserFollowedByList(thisUserData.getId());
            List<org.jinstagram.entity.users.feed.UserFeedData> users = feed.getUserList();
            ArrayList<User> result = new ArrayList<>();
            for (UserFeedData u : users){
                User newFollower = new User(u.getId(), u.getUserName(),u.getProfilePictureUrl(),u.getFullName());
                result.add(newFollower);
            }

            return result;
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }

    // get current user's following
    public ArrayList<User> getFollowing(){
        //String username, Image userImage, String profName, TimeSince timeSince
        try {
            UserFeed feed = instagram.getUserFollowList(thisUserData.getId());
            List<UserFeedData> users = feed.getUserList();
            ArrayList<User> result = new ArrayList<>();
            for (UserFeedData u : users){
                // omit users to unfollow
                if(!Parameters.userIdToUnfollow.contains(u.getId())) {
                    User newFollowing = new User(u.getId(), u.getUserName(),
                            u.getProfilePictureUrl(), u.getFullName());
                    result.add(newFollowing);
                }
            }

            return result;
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }

    // check if current user is following a given user
    public String checkIfFollowing(String userId){
        if(!Parameters.dummyData) {
            try {
                Log.w("like", "network checkIfFollowing: " + userId);
                if (!userId.startsWith(Parameters.default_userId)) {
                    RelationshipFeed feed = instagram.getUserRelationship(userId);
                    return feed.getData().getOutgoingStatus();
                }
            } catch (InstagramException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    // search for user info by user ID
    public Profile searchUserProfileById(String userId){
        try {
            UserInfo feed = instagram.getUserInfo(userId);
            return new Profile(new User(userId, feed.getData().getUsername(),
                    feed.getData().getProfilePicture(), feed.getData().getFullName()),
                    feed.getData().getBio());
        } catch (InstagramException e) {
            e.printStackTrace();
            return null;
        }
    }

    // search for user info by user ID
    public User searchUserInfoById(String userId){
        try {
            UserInfo feed = instagram.getUserInfo(userId);
            return new User(userId, feed.getData().getUsername(),
                    feed.getData().getProfilePicture(), feed.getData().getFullName());
        } catch (InstagramException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Build list of activity objects for activity feed
    public ArrayList<ActivityFollowing> getActivityFeedFollowing(String minTime,String maxTime){
        ArrayList<User> following = getFollowing();
        ArrayList<Post> recentPosts = new ArrayList<>();
        ArrayList<ActivityFollowing> actFollowing = new ArrayList<>();
        //MIN is later than
        long week = Parameters.week; // seconds
        long month  = Parameters.month; //seconds
        long now = System.currentTimeMillis() / 1000L;
        long minTimeSec = now - month;  // last month
        long maxTimeSec = now - 3600;   //last minute;
        if (minTime != null){
            minTimeSec = Long.parseLong(minTime);
            maxTimeSec = 0;
            Log.v("NET min",minTime);
        }else if (maxTime != null){
            maxTimeSec = Long.parseLong(maxTime);
            minTimeSec = 0;
            Log.v("NET max",maxTime);
        }else{
            maxTimeSec = 0;
        }

        for (User followee : following){
            String userId = followee.getUsername().getUserId();
            ArrayList<Post> userPost = getProfileForActivityFeed(userId, minTimeSec, maxTimeSec);
            recentPosts.addAll(userPost);
        }
        Collections.sort(recentPosts, new Comparator<Post>() {
            @Override
            public int compare(Post p1, Post p2) {
                int t1 = Integer.parseInt(p1.getTimeSince().getTimeSince());
                int t2 = Integer.parseInt(p2.getTimeSince().getTimeSince());
                //   return t1-t2; // Ascending
                return t2 - t1; // Descending
            }
        });
        int rpSize = recentPosts.size();
        ArrayList<Post> tmpPost = new ArrayList<>();
        int count = (rpSize< MAX_ACTIVITY_FOLLOWING)? rpSize : MAX_ACTIVITY_FOLLOWING;
        for (int i = 0; i < count ; i++) {
            Post p1;
            String u1,u2;
            p1 = recentPosts.get(i);

            if (i != (rpSize - 1)) {
                u1 = p1.getUsername().getUsername();
                u2 = recentPosts.get(i + 1).getUsername().getUsername();

                tmpPost.add(p1);
                if (!u1.equalsIgnoreCase(u2)) {
                    //Push the ActivityFollowing object into result list. Then Clear Posts list.
                    ActivityFollowing aF = new ActivityFollowing(tmpPost.size(),tmpPost);
                    actFollowing.add(aF);
                    tmpPost= new ArrayList<>();
                    continue;
                }
            }else{
                //Special case for last post in arraylist
                tmpPost.add(p1);
                ActivityFollowing aF = new ActivityFollowing(tmpPost.size(),tmpPost);
                actFollowing.add(aF);
            }

        }
        Log.v("NETWORK",Integer.toString(actFollowing.size()));
        return actFollowing;
    }


    //GET MEDIA A USER LIKED
    public ArrayList<Post> getMediaUserLikes() {
        return getMediaUserLikes(0,0);
    }

    public ArrayList<Post> getMediaUserLikes(long id,int count){

        MediaFeed mediaFeed = null;
        try {
            if(id ==0){
                mediaFeed = instagram.getUserLikedMediaFeed();
            }else{
                mediaFeed = instagram.getUserLikedMediaFeed(id,count);
            }

            List<MediaFeedData> mediaFeeds = mediaFeed.getData();
            Log.v("NET LIKES",Integer.toString(mediaFeeds.size()));
            return getPostsLikedList(mediaFeeds, true);
        } catch (InstagramException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<User> searchUser(String username){
        return searchUser(username, 0);
    }
    public ArrayList<User> searchUser(String username,int count){
        UserFeed userFeed = null;
        try {
            if(count ==0){
                userFeed = instagram.searchUser(username);
            }else{
                userFeed = instagram.searchUser(username, count);
            }
            List<UserFeedData> users = userFeed.getUserList();
            ArrayList<User> result = new ArrayList<>();
            for (UserFeedData u : users){
                User newFollower = new User(u.getId(), u.getUserName(),u.getProfilePictureUrl(),u.getFullName());
                result.add(newFollower);
            }
            return result;
        } catch (InstagramException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isPostLikedByLoginUser(String postId){
        boolean liked;
        try {
            liked = instagram.getMediaInfo(postId).getData().isUserHasLiked();
            return liked;
        } catch (InstagramException e) {
            e.printStackTrace();
            return false;
        }
    }
}


