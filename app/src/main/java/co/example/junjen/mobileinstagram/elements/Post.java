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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import co.example.junjen.mobileinstagram.CommentsFragment;
import co.example.junjen.mobileinstagram.PostFragment;
import co.example.junjen.mobileinstagram.UsersFragment;
import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.ToggleButton;
import co.example.junjen.mobileinstagram.customLayouts.LikeListener;
import co.example.junjen.mobileinstagram.customLayouts.SquareImageView;
import co.example.junjen.mobileinstagram.customLayouts.UserImageView;
import co.example.junjen.mobileinstagram.network.NetParams;

public class Post implements Serializable{

    private String postId;
    private Post post = this;

    // post header
    private Image userImage;
    private Username username;
    private Location location;
    private TimeSince timeSince;

    // post content
    private Image postImage;
    private String caption;
    private int likeCount;
    private int commentCount;
    private ArrayList<User> likes;
    private ArrayList<Comment> comments;

    // post view
    private RelativeLayout postView;
    private ToggleButton likeButton;

    private String liked = Parameters.checkLike;

    public Post(){
        // test constructor to create 'empty' Post objects

        this.postId = Parameters.default_postId;
        this.userImage = new Image(Parameters.default_image);
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
            this.likes.add(new User(userId, username, Parameters.default_image,
                    Parameters.default_profName));
        }
        // create empty comments
        for (i = 0; i < Parameters.default_commentCount; i++){
            userId = Parameters.default_userId + (i + 1);
            username = Parameters.default_username + (i + 1);
            comment = Parameters.default_comment + (i + 1);
            this.comments.add(new Comment(userId, username,
                    Parameters.default_image, comment, new TimeSince()));
        }
    }

    public Post(String postId, String userId, String userImage, String username, Location location,
                String timeSince, String postImage, String caption, int likeCount, int commentCount,
                ArrayList<User> likes, ArrayList<Comment> comments){

        this.postId = postId;
        this.userImage = new Image(userImage);
        this.username = new Username(userId, username);
        this.location = location;
        this.timeSince = new TimeSince(timeSince);
        this.postImage = new Image(postImage);
        this.caption = caption;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.likes = likes;
        this.comments = comments;
    }

    public View getPostView(LayoutInflater inflater) {

        try {

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
            new LikeListener(postImage, this);

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
                stringComponents.add(this.location.getLocation());
                StringFactory.stringBuilder(location, stringComponents);
                stringComponents.clear();
            } else {
                location.setVisibility(View.GONE);
            }

            // Likes
            if (likes != null) {
                updateLikes();
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

            // Comments
            buildCommentView(inflater);

        } catch (InflateException e) {
            Log.w("test", "InflateException at getPostView()");
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

                        comments = NetParams.NETWORK.getCommentsByPostId(postId, false);

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

    // get layout of post icons to be added to the bottom of a profile fragment
    public static void buildPostIcons(LayoutInflater inflater, ViewGroup postIconList,
                                      ArrayList<Post> posts){

        int postsSize = posts.size();
        int postIconsPerRow = Parameters.postIconsPerRow;
        int postIconRowsToLoad = Parameters.postIconRowsToLoad;

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

    // updates list of Likes based or like button clicks or post image double taps
    public void likePost(boolean like){
        if(like && liked.equals(Parameters.unlike) || !like && liked.equals(Parameters.like)) {

            Iterator<User> iter;
            for (iter = likes.listIterator(); iter.hasNext();) {
                User thisUser = iter.next();
                if (thisUser.getUsername().getUsername().equals(Parameters.loginUsername)) {
                    if (!like) {
                        iter.remove();
                        liked = Parameters.unlike;
                        updateLikes();
                    }
                    break;
                }
            }
            if (like) {
                likes.add(0, Parameters.loginUser);
                liked = Parameters.like;
                updateLikes();
            }
        }
    }

    // update likes list and like button
    public void updateLikes(){

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

                        likes = NetParams.NETWORK.getLikesByPostId(postId);

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
            likeButton.setChecked(true);
        } else {
            likeButton.setChecked(false);
        }
    }

    // OnClickListener for post icon clicks
    public View.OnClickListener postIconOnClickListener(){

        View.OnClickListener postIconOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postId = (String) v.getContentDescription();

                // display post
                Parameters.NavigationBarActivity.showFragment(PostFragment.newInstance(postId));
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
}
