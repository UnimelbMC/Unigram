package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Post objects.
 */

import android.text.SpannableString;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import co.example.junjen.mobileinstagram.CommentsFragment;
import co.example.junjen.mobileinstagram.PostFragment;
import co.example.junjen.mobileinstagram.UsersFragment;
import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.ToggleButton;
import co.example.junjen.mobileinstagram.customLayouts.PostImageListener;
import co.example.junjen.mobileinstagram.customLayouts.SquareImageView;
import co.example.junjen.mobileinstagram.customLayouts.UserImageView;
import co.example.junjen.mobileinstagram.network.NetParams;

public class Post implements Serializable{

    protected String postId;
    protected Post post = this;

    // post header
    protected Image userImage;
    protected Username username;
    protected Location location;
    protected TimeSince timeSince;

    // post content
    protected Image postImage;
    protected String caption;
    protected int likeCount;
    protected int commentCount;
    protected ArrayList<User> likes;
    protected ArrayList<Comment> comments;

    // post view
    protected RelativeLayout postView;
    protected ToggleButton likeButton;

    // swiped post variables
    protected Username swipedFrom;
    protected Image swipedPostImage;

    // helper variables
    protected String liked = Parameters.checkLike;
    protected double locDiff = 10000;

    public Post(){
        // test constructor to create 'empty' Post objects

        this.postId = Parameters.default_postId;
        this.userImage = new Image(Parameters.default_emptyUserImageLink);
        this.username = new Username(Parameters.default_userId, Parameters.default_username);
        this.location = new Location(Parameters.default_location);
        this.timeSince = new TimeSince();
        this.postImage = new Image(Parameters.default_image);
        this.caption = Parameters.default_caption;

        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();

        int i;
        String userId;
        String username;
        String comment;

        this.likeCount = Parameters.default_likeCount;
        this.commentCount = Parameters.default_commentCount;

        // create empty likes
        for (i = 0; i < Parameters.default_likeCount; i++){
            userId = Parameters.default_userId + (i + 1);
            username = Parameters.default_username + (i + 1);
            this.likes.add(new User(userId, username, Parameters.default_emptyUserImageLink,
                    Parameters.default_profName));
        }
        // create empty comments
        for (i = 0; i < Parameters.default_commentCount; i++){
            userId = Parameters.default_userId + (i + 1);
            username = Parameters.default_username + (i + 1);
            comment = Parameters.default_comment + (i + 1);
            this.comments.add(new Comment(userId, username,
                    Parameters.default_emptyUserImageLink, comment, new TimeSince()));
        }

        // check if post was liked or unliked
        if(!Parameters.postIdToUnlike.contains(postId)) {
            if (Parameters.postIdToLike.contains(postId)){
                liked = Parameters.like;
                this.likeCount++;
                likes.add(0, Parameters.loginUser);
            }
        } else {
            liked = Parameters.unlike;
        }

    }

    // constructor for actual instagram post data
    public Post(String postId, Username poster, String userImage, Location location,
                String timeSince, String postImage, String caption, int likeCount, int commentCount,
                ArrayList<User> likes, ArrayList<Comment> comments){

        this.postId = postId;
        this.userImage = new Image(userImage);
        this.username = poster;
        this.location = location;
        this.timeSince = new TimeSince(timeSince);
        this.postImage = new Image(postImage);
        this.caption = caption;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likes = likes;
        this.comments = comments;

        // check if post was liked or unliked
        if(!Parameters.postIdToUnlike.contains(postId)) {

            // update values accordingly for UI purposes
            boolean likedByLoginUser = NetParams.NETWORK.isPostLikedByLoginUser(postId);
            if (Parameters.postIdToLike.contains(postId) || likedByLoginUser){
                liked = Parameters.like;
                if (Parameters.postIdToLike.contains(postId) && !likedByLoginUser){
                    this.likeCount++;
                }
            }
            if (NetParams.NETWORK.isPostLikedByLoginUser(postId)) {
                liked = Parameters.like;
            }
        } else {
            liked = Parameters.unlike;
        }

        if (this.location!= null){
            locDiff = getDiff(location.getLatitude(),location.getLongitude());
        }
    }

    // constructor for swiped posts
    public Post(String postId, Username poster, String userImage, Location location,
                String timeSince,String postImage, String caption, Username swipedFrom){

        this.postId = postId;
        this.userImage = new Image(userImage);
        this.username = poster;
        this.location = location;
        this.timeSince = new TimeSince(timeSince);
        this.swipedPostImage =new Image(postImage);
        this.caption = caption;

        this.swipedFrom = swipedFrom;

    }

    // get full view of post
    public View getPostView(LayoutInflater inflater) {

        postView = (RelativeLayout) inflater.inflate(R.layout.post, null, false);
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        /** Fixed parameters **/

        // User image
        if (!this.userImage.getImageString().equals(Parameters.default_image)) {
            UserImageView userImage = (UserImageView)
                    postView.findViewById(R.id.post_header_user_image);
            Image.setImage(userImage, this.userImage);
        }

        // Username
        TextView username = (TextView) postView.findViewById(R.id.post_header_username);
        username.setText("");   // remove default text
        stringComponents.add(this.username.getUsernameLink());
        StringFactory.stringBuilder(username, stringComponents);
        stringComponents.clear();

        // Time since posted
        TextView timeSince = (TextView) postView.findViewById(R.id.post_header_time_since);
        timeSince.setText(this.timeSince.getTimeSinceDisplay());

        // Post image
        ImageView postImage = (ImageView) postView.findViewById(R.id.post_image);
        if (!this.postImage.getImageString().equals(Parameters.default_image)) {
            Image.setImage(postImage, this.postImage);
        }
        // set listener to handle double tap likes on post image
        new PostImageListener(postImage, this);

        // Like Feedback
        ImageView likeFeedback = (ImageView) postView.findViewById(R.id.like_feedback);
        ViewGroup.LayoutParams layoutParams = likeFeedback.getLayoutParams();
        likeFeedback.setLayoutParams(layoutParams);
        likeFeedback.setVisibility(View.INVISIBLE);

        // Like Button
        likeButton = (ToggleButton) postView.findViewById(R.id.like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {

            // Handle clicks for like button
            @Override
            public void onClick(View v) {
                if (likeButton.isChecked()) {
                    likePost(true);
                } else {
                    likePost(false);
                }
            }
        });

        // Comment Button
        ImageView commentButton = (ImageView) postView.findViewById(R.id.comment_button);
        commentButton.setOnClickListener(this.commentButtonOnClickListener());

        /** Optional parameters **/

        // TODO: Confirm for 'null' return type if optional params do not exist

        // Location
        TextView location = (TextView) postView.findViewById(R.id.post_header_location);
        if (this.location != null) {
            location.setText("");    // remove default text

            String locText = this.location.getLocation();
            if(locText.equals("")){
                locText = "Lat: " + this.location.getLatitude() + ", " +
                        "Long: " + this.location.getLongitude();
            }

            stringComponents.add(locText);
            StringFactory.stringBuilder(location, stringComponents);
            stringComponents.clear();
        } else {
            location.setVisibility(View.GONE);
        }

        // Likes
        if (likes != null) {
            updateLikes();
        }

        // check like button if post is from activity feed (list of current user's likes)
        checkLikeButton();

        // Caption
        TextView caption = (TextView) postView.findViewById(R.id.post_caption);
        if (this.caption != null) {
            caption.setText("");    // remove default text
            stringComponents.add(this.username.getUsernameLink());
            stringComponents.add(" " + this.caption);
            StringFactory.stringBuilder(caption, stringComponents);
            stringComponents.clear();
        } else {
            caption.setVisibility(View.GONE);
        }

        // Comments
        buildCommentView(inflater);

        return postView;
    }

    // build minimal swiped post view
    public View getSwipedPostView(LayoutInflater inflater) {

        postView = (RelativeLayout) inflater.inflate(R.layout.post, null, false);
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        /** Fixed parameters **/

        // User image
        if (!this.userImage.getImageString().equals(Parameters.default_image)) {
            UserImageView userImage = (UserImageView)
                    postView.findViewById(R.id.post_header_user_image);
            Image.setImage(userImage, this.userImage);
        }

        // Username
        TextView username = (TextView) postView.findViewById(R.id.post_header_username);
        username.setText("");   // remove default text
        stringComponents.add(this.username.getUsernameLink());
        StringFactory.stringBuilder(username, stringComponents);
        stringComponents.clear();

        // Time since posted
        TextView timeSince = (TextView) postView.findViewById(R.id.post_header_time_since);
        timeSince.setText(this.timeSince.getTimeSinceDisplay());

        // Post image
        ImageView postImage = (ImageView) postView.findViewById(R.id.post_image);
       // Image.setSwipedImage(this.swipedPostImage, postImage);

        // set listener to handle double tap likes on post image
        new PostImageListener(postImage, this);

        // Like Feedback
        ImageView likeFeedback = (ImageView) postView.findViewById(R.id.like_feedback);
        ViewGroup.LayoutParams layoutParams = likeFeedback.getLayoutParams();
        likeFeedback.setLayoutParams(layoutParams);
        likeFeedback.setVisibility(View.INVISIBLE);

        // Hide Like Comments
        postView.findViewById(R.id.like_button_group).setVisibility(View.GONE);
        postView.findViewById(R.id.comment_button).setVisibility(View.GONE);
        postView.findViewById(R.id.post_comment_count).setVisibility(View.GONE);
        postView.findViewById(R.id.post_comments).setVisibility(View.GONE);
        postView.findViewById(R.id.like_count_line).setVisibility(View.GONE);

        // Show Swipe Tag
        RelativeLayout swipeTag = (RelativeLayout) postView.findViewById(R.id.swipe_tag);
        swipeTag.setVisibility(View.VISIBLE);
        TextView swipedFrom = (TextView) swipeTag.findViewById(R.id.swipe_text);
        swipedFrom.setText("");   // remove default text
        stringComponents.add(Parameters.default_swipeText);
        stringComponents.add(this.swipedFrom.getUsernameLink());
        StringFactory.stringBuilder(swipedFrom, stringComponents);
        stringComponents.clear();

        /** Optional parameters **/

        // Location
        TextView location = (TextView) postView.findViewById(R.id.post_header_location);
        if (this.location != null) {
            location.setText("");    // remove default text

            String locText = this.location.getLocation();
            if(locText.equals("")){
                locText = "Lat: " + this.location.getLatitude() + ", " +
                        "Long: " + this.location.getLongitude();
            }

            stringComponents.add(locText);
            StringFactory.stringBuilder(location, stringComponents);
            stringComponents.clear();
        } else {
            location.setVisibility(View.GONE);
        }

        // Caption
        TextView caption = (TextView) postView.findViewById(R.id.post_caption);
        if (this.caption != null) {
            caption.setText("");    // remove default text
            stringComponents.add(this.username.getUsernameLink());
            stringComponents.add(" " + this.caption);
            StringFactory.stringBuilder(caption, stringComponents);
            stringComponents.clear();
        } else {
            caption.setVisibility(View.GONE);
        }

        return postView;
    }

    // builds the view for the comments portion in a post
    public void buildCommentView(LayoutInflater inflater){

        ArrayList<CharSequence> stringComponents = new ArrayList<>();
        TextView commentCountText = (TextView) postView.findViewById(R.id.post_comment_count);
        if (this.comments != null){

            // add link to show all comments if more than 3 comments
            int commentThreshold = Parameters.commentPreviewThreshold;
            if (commentCount > commentThreshold){
                commentCountText.setVisibility(View.VISIBLE);

                String formatStr = NumberFormat.getNumberInstance(Locale.US).format(commentCount);

                String text = "View all " + formatStr + " comments";

                SpannableString commentsLink = StringFactory.createLink(text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!Parameters.dummyData) {
                            comments = NetParams.NETWORK.getCommentsByPostId(postId, false);
                        }

                        // display post's comments
                        Parameters.NavigationBarActivity.showFragment(CommentsFragment.
                                newInstance(post));
                    }
                });
                commentCountText.setText("");    // remove default text
                stringComponents.add(commentsLink);
                StringFactory.stringBuilder(commentCountText, stringComponents);
                stringComponents.clear();
            } else {
                commentCountText.setVisibility(View.GONE);
            }

            // dynamically add preview of maximum 3 comments below commentCountText
            ViewGroup commentsView = (ViewGroup) postView.findViewById(R.id.post_comments);
            commentsView.removeAllViews();  // clear previous comments when rebuilding

            int i;
            int index;
            int counter = 0;
            int commentsSize = comments.size();

            for (i = 0; i < commentThreshold; i++){
                index = commentsSize - 1 - counter;

                if (index > commentsSize - 1 || index < 0) break;

                View commentPreview = inflater.inflate(R.layout.post_comment, commentsView, false);
                TextView commentText = (TextView) commentPreview.findViewById(R.id.post_comment);
                Comment comment = comments.get(index);

                commentText.setText("");    // remove default text
                stringComponents.add(comment.getUsername().getUsernameLink());
                stringComponents.add(" " + comment.getComment());
                StringFactory.stringBuilder(commentText, stringComponents);
                stringComponents.clear();

                commentsView.addView(commentPreview, 0);
                counter++;
            }
        } else {
            commentCountText.setVisibility(View.GONE);
        }
    }

    // get layout of post icons to be added to the bottom of a fragment
    public static void buildPostIcons(LayoutInflater inflater, ViewGroup postIconList,
                                      ArrayList<Post> posts, int iconsPerRow, int rowsToLoad){

        int postsSize = posts.size();
        int postIconsPerRow = iconsPerRow;
        int postIconRowsToLoad = rowsToLoad;

        int rows = (int) Math.ceil((double) postsSize / postIconsPerRow);
        if (rows < postIconRowsToLoad){
            postIconRowsToLoad = rows;
        }

        int i;
        int index;
        for (i = 0; i < postIconRowsToLoad; i++){

            LinearLayout postIconRow = (LinearLayout)
                    inflater.inflate(R.layout.post_icon_row, null, false);

            int j;
            for (j = 0; j < postIconsPerRow; j++){
                index = i * postIconsPerRow + j;

                // get post_icon_row layout
                View postIconLayout = inflater.inflate(R.layout.post_icon, null, false);
                SquareImageView imageView = (SquareImageView)
                        postIconLayout.findViewById(R.id.post_icon);
                ((ViewGroup) imageView.getParent()).removeView(imageView);

                if (postsSize - index > 0) {
                    Post post = posts.get(index);
                    if (!post.getPostImage().getImageString().equals(Parameters.default_image)) {
                        Image.setImage(imageView, post.getPostImage());
                    }

                    imageView.setContentDescription(post.getPostId());
                    imageView.setOnClickListener(post.postIconOnClickListener());

                } else {
                    imageView.setImageDrawable(null);
                }
                // add post icon into row
                postIconRow.addView(imageView, postIconRow.getChildCount());
            }
            // add row to list
            postIconList.addView(postIconRow, postIconList.getChildCount());
        }
    }

    // updates list of Likes based on like button clicks or post image double taps
    public void likePost(boolean like){
        if(likeCount <= Parameters.likePreviewThreshold + 1) {
            if (!like && liked.equals(Parameters.like)) {
                Log.w("test", "unlike");
                Iterator<User> iter;
                for (iter = likes.listIterator(); iter.hasNext(); ) {
                    User thisUser = iter.next();
                    if (thisUser.getUsername().getUsername().equals(Parameters.loginUsername)) {
                        iter.remove();
                        likeCount--;
                        liked = Parameters.unlike;
                        updatePostLikeSet(postId, false);
                        updateLikes();
                        break;
                    }
                }
            }
            else if (like && liked.equals(Parameters.unlike)) {
                Log.w("test", "like");
                likes.add(0, Parameters.loginUser);
                likeCount++;
                liked = Parameters.like;
                updatePostLikeSet(postId, true);
                updateLikes();
            }
        } else {
            if(like && !liked.equals(Parameters.like)){
                likeCount++;
                liked = Parameters.like;
                updatePostLikeSet(postId, true);
            } else {
                likeCount--;
                liked = Parameters.unlike;
                updatePostLikeSet(postId, false);
            }
            updateLikes();
        }
    }

    // update posts marked as liked or unliked
    public void updatePostLikeSet(String postId, boolean add){
        if(add){
            Parameters.postIdToLike.add(postId);
            Parameters.postIdToUnlike.remove(postId);
        } else {
            Parameters.postIdToUnlike.add(postId);
            Parameters.postIdToLike.remove(postId);
        }
    }

    // update likes list and like button
    public void updateLikes(){

        // check if current user liked this post
        if (liked.equals(Parameters.checkLike)){
            liked = Parameters.unlike;
            for(User user : likes){
                if(user.getUsername().getUsername().equals(Parameters.loginUsername)){
                    liked = Parameters.like;
                    break;
                }
            }
        }

        ArrayList<CharSequence> stringComponents = new ArrayList<>();
        RelativeLayout likeLine = (RelativeLayout) postView.findViewById(R.id.like_count_line);

        if (this.likeCount != 0){
            likeLine.setVisibility(View.VISIBLE);
            TextView likeCountText = (TextView) postView.findViewById(R.id.like_count);
            int likeCount = this.likeCount;
            int likeThreshold = Parameters.likePreviewThreshold;

            // add link to all likes if more than threshold
            if (likeCount > likeThreshold) {
                String formatStr = NumberFormat.getNumberInstance(Locale.US).format(likeCount);
                String text = formatStr + " likes";

                SpannableString likeLink = StringFactory.createLink(text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!Parameters.dummyData) {
                            likes = NetParams.NETWORK.getLikesByPostId(postId);

                            if(liked.equals(Parameters.like)
                                    && !likes.contains(Parameters.loginUser)){
                                likes.add(0,Parameters.loginUser);
                            }

                        }

                        // display post's likes
                        Parameters.NavigationBarActivity.showFragment
                                (UsersFragment.newInstance(likes, Parameters.likesTitle));
                    }
                });
                likeCountText.setText("");    // remove default text
                stringComponents.add(likeLink);
                StringFactory.stringBuilder(likeCountText, stringComponents);
                stringComponents.clear();
            } else {
                likeCountText.setText("");  // remove default text
                int i;
                for (i = 0; i < likeCount; i++) {
                    Log.w("test","add to like bar: "+this.likes.get(i).getUsername().getUsername());
                    stringComponents.add(this.likes.get(i).getUsername().getUsernameLink());
                    stringComponents.add(", ");
                }
                if (likeCount > 0) {
                    stringComponents.remove(stringComponents.size() - 1);   // remove trailing comma
                }
                StringFactory.stringBuilder(likeCountText, stringComponents);
                stringComponents.clear();
            }
        } else {
            likeLine.setVisibility(View.GONE);
        }
        checkLikeButton();
    }

    // updates like button
    public void checkLikeButton(){
        if(liked.equals(Parameters.like)){
            ((RadioGroup) likeButton.getParent()).check(R.id.like_button);

        } else {
            ((RadioGroup) likeButton.getParent()).clearCheck();
        }
    }

    // OnClickListener for post icon clicks
    public View.OnClickListener postIconOnClickListener(){

        View.OnClickListener postIconOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postId = (String) v.getContentDescription();

                boolean like;
                if (liked.equals(Parameters.like)){
                    like = true;
                } else {
                    like = false;
                }

                // display post
                Parameters.NavigationBarActivity.
                        showFragment(PostFragment.newInstance(postId, like));
            }
        };
        return postIconOnClickListener;
    }

    // OnClickListener for post icon clicks
    public View.OnClickListener commentButtonOnClickListener(){

        View.OnClickListener commentButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // display post's comments
                Parameters.NavigationBarActivity.showFragment(CommentsFragment.
                        newInstance(post));
            }
        };

        return commentButtonOnClickListener;
    }
    //Sort post list by location
    public static void  sortPostByLocation(ArrayList<Post> list){
        Log.v("SORT","byloc");
        Collections.sort(list, new Comparator<Post>() {
            @Override
            public int compare(Post p1, Post p2) {

                double t1 = p1.getLocDiff();
                double t2 = p2.getLocDiff();

                if (t1 < t2) return -1;
                if (t1 > t2) return 1;
               // return t1.compareTo(t2);
                return 0; // Ascending
                //return t2 - t1; // Descending
            }
        });
    }
    //Sort post list by time
    public static void sortPostByTime(ArrayList<Post> list){
        Collections.sort(list, new Comparator<Post>() {
            @Override
            public int compare(Post p1, Post p2) {
                String t1 = p1.getTimeSince().getTimeSince();
                String t2 = p2.getTimeSince().getTimeSince();
                return t2.compareTo(t1);
                //   return t1-t2; // Ascending
                //return t2 - t1; // Descending
            }
        });
    }

    public double getLocDiff(){
        return this.locDiff;
    }
    private double getDiff(double lat, double lon){
        //Euclidean distance
        double x1 = lat;
        double y1 = lon;
        double x2 = Parameters.DEV_LATITUDE;
        double y2 = Parameters.DEV_LONGITUDE;
        Log.v("sort",Double.toString(x1)+" "+ Double.toString(x2));
        Log.v("sort",Double.toString(y1)+" "+ Double.toString(y2));
        double  xDiff = Math.abs(x1-x2);
        double  xSqr  = Math.pow(xDiff, 2);

        double yDiff = Math.abs(y1-y2);
        double ySqr = Math.pow(yDiff, 2);

        double output   = Math.sqrt(xSqr + ySqr);
        Log.v("sort - diff", Double.toString(output));
        return output;
    }
    //Conver post to Json object
    public String toJson(Username from) {
        String locId="",location= "";
        String lat="0.0",lon= "0.0";
       if(this.location!=null) {
           locId =this.location.getLocationId();
           location= this.location.getLocation();
           lat = Double.toString(this.location.getLatitude());
           lon = Double.toString(this.location.getLongitude());
       }
        String posImg =this.getPostImage().getImageString();// (postImage != null)? Arrays.toString(postImage): "";

        String mStringArray[] = { this.postId,this.username.getUserId(),this.username.toString(),this.userImage.getImageString(),locId,location,lat,lon,
            this.timeSince.getTimeSince(),posImg,this.caption,from.getUserId(),from.getUsername()};

        JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
      return mJSONArray.toString();
    }
    //Generate post object from json obj received through BT
    public  static Post fromJson(String jsonString) {
        JSONArray jsonA = null;
        try {
            jsonA = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<String> list = new ArrayList<String>();
        if (jsonA != null) {
            int len = jsonA.length();
            for (int x=0;x<len;x++){
                try {
                    list.add(jsonA.get(x).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] json = new String[list.size()];
        json = list.toArray(json);

        String postId = json[0];
        Username poster = new Username(json[1],json[2]);
        String userImage = json[3];
        Location location = new Location(json[4], json[5],Double.parseDouble(json[6]),Double.parseDouble(json[7]));
        String timeSince = json[8];
        String postImage = json[9];
        String caption=json[10];
        Username swipedFrom = new Username(json[11],json[12]);

        return new Post(postId,poster,userImage,location,timeSince,postImage,caption,swipedFrom);
    }

    public void setLiked(String like){
        this.liked = like;
    }

    public String getPostId() {
        return postId;
    }

    public Image getUserImage() {
        return userImage;
    }

    public Username getUsername() {
        return username;
    }

    public TimeSince getTimeSince() {
        return timeSince;
    }

    public Image getPostImage() {
        return postImage;
    }

    public String getCaption() {
        return caption;
    }

    public ArrayList<User> getLikes() {
        return likes;
    }

    public ArrayList<Comment> getComments () {
        return comments;
    }

    public RelativeLayout getPostView() {
        return postView;
    }

    public Location getLocation(){
        return this.location;
    }
}
