package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Post objects.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.R;

public class Post {

    // post header
    private Image userImage;
    private Username username;
    private TimeSince timeSince;

    // post content
    private Image postImage;
    private String caption;
    private ArrayList<Like> likes;
    private ArrayList<Comment> comments;

    // post View
    private View postView;

    public Post(LayoutInflater inflater, ViewGroup parentView){
        // test constructor to create 'empty' Post objects

        this.userImage = new Image(Parameters.default_image);
        this.username = new Username(Parameters.default_username);
        this.timeSince = new TimeSince(Parameters.default_timeSince);
        this.postImage = new Image(Parameters.default_image);
        this.caption = Parameters.default_caption;

        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();

        int i;
        String username;
        String comment;

        // create 20 empty likes
        for (i = 0; i < 20; i++){
            username = Parameters.default_username + (i + 1);
            this.likes.add(new Like(username, new TimeSince(Parameters.default_timeSince)));
        }
        // create 10 comments
        for (i = 0; i < 10; i++){
            username = Parameters.default_username + (i + 1);
            comment = Parameters.default_comment + (i + 1);
            this.comments.add(new Comment(username, comment,
                    new TimeSince(Parameters.default_timeSince)));
        }

        this.postView = createPostView(inflater, parentView);
    }

    public Post(String userImage, String username, String timeSince, String postImage,
                String caption, String likes, String comments, LayoutInflater inflater,
                ViewGroup parentView){

        this.userImage = new Image(userImage);
        this.username = new Username(username);
        this.timeSince = new TimeSince(timeSince);
        this.postImage = new Image(postImage);
        this.caption = caption;

        this.likes = createLikesList(likes);
        this.comments = createCommentsList(comments);

        this.postView = createPostView(inflater, parentView);

    }

    private ArrayList<Like> createLikesList(String likes_string){
        ArrayList<Like> likes = new ArrayList<>();

        // TODO: method to convert likes_string into ArrayList<Like>

        return likes;
    }

    private ArrayList<Comment> createCommentsList(String comments_string){
        ArrayList<Comment> comments = new ArrayList<>();

        // TODO: method to convert comments_string into ArrayList<Comment>

        return comments;
    }

    public View createPostView(LayoutInflater inflater, ViewGroup parentView){

        // TODO: set 'onClickListener" for any applicable views
        // Example:
        // TextView t2 = (TextView) findViewById(R.id.text2);
        // t2.setMovementMethod(LinkMovementMethod.getInstance());

        RelativeLayout postView = (RelativeLayout) inflater.inflate(R.layout.post, parentView, false);
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        /** Fixed parameters **/

        // User image
        if(!this.userImage.getImageString().equals(Parameters.default_image)) {
            ImageView userImage = (ImageView) postView.findViewById(R.id.post_header_user_image);
            // TODO: Determine set image type
            userImage.setImageDrawable(this.userImage.getImage());
        }

        // Username
        TextView username = (TextView) postView.findViewById(R.id.post_header_username);
        username.setText("");   // remove default text
        stringComponents.add(this.username.getUsername_link());
        StringFactory.stringBuilder(username, stringComponents);
        stringComponents.clear();

        // Time since posted
        TextView timeSince = (TextView) postView.findViewById(R.id.post_header_time_since);
        timeSince.setText(this.timeSince.getTimeSince());

        // Post image
        if(!this.postImage.getImageString().equals(Parameters.default_image)) {
            ImageView postImage = (ImageView) postView.findViewById(R.id.post_image);
            // TODO: Determine set image type
            postImage.setImageDrawable(this.postImage.getImage());
        }

        // TODO: Handle clicks for like button

        /** Optional parameters **/

        // TODO: Confirm for 'null' return type if optional params do not exist

        // Likes
        RelativeLayout likeLine = (RelativeLayout) postView.findViewById(R.id.like_count_line);
        if (this.likes != null){
            TextView likes = (TextView) postView.findViewById(R.id.like_count);
            int likeCount = this.likes.size();
            int likeThreshold = Parameters.likeThreshold;
            if(likeCount > likeThreshold){
                likes.setText(likeCount + " likes");
            }
            else {
                likes.setText("");  // remove default text
                int i;
                for (i = 0; i < likeCount; i++){
                    stringComponents.add(this.likes.get(i).getUsername().getUsername_link());
                    stringComponents.add(", ");
                }
                stringComponents.remove(stringComponents.size() - 1);   // remove trailing comma
                StringFactory.stringBuilder(likes, stringComponents);
                stringComponents.clear();
            }
        } else {
            likeLine.setVisibility(View.GONE);
        }

        // Caption
        TextView caption = (TextView) postView.findViewById(R.id.post_caption);
        if (this.caption != null){
            caption.setText("");    // remove default text
            stringComponents.add(this.username.getUsername_link());
            stringComponents.add(" " + this.caption);
            StringFactory.stringBuilder(caption, stringComponents);
            stringComponents.clear();
        } else {
            caption.setVisibility(View.GONE);
        }

        // Comments

        TextView commentCountText = (TextView) postView.findViewById(R.id.post_comment_count);
        if (this.comments != null){

            // add link to show all comments is more than 3 comments
            int commentCount = this.comments.size();
            int commentThreshold = Parameters.commentThreshold;
            if (commentCount > commentThreshold){
                commentCountText.setText("View all " + commentCount + " comments");

                // TODO: onClickListener

            } else {
                commentCountText.setVisibility(View.GONE);
            }

            // dynamically add preview of maximum 3 comments below commentCountText
            int aboveID = commentCountText.getId();
            ViewGroup commentView = (ViewGroup) postView.findViewById(R.id.post_comments);
            ArrayList<TextView> commentViews = new ArrayList<>();

            int i;
            for (i = 0; i < commentThreshold; i++){
                if (commentCount > i) {

                    // get post_comment layout
                    View comments = inflater.inflate(R.layout.post_comment, null, false);
                    commentViews.add((TextView) comments.findViewById(R.id.post_comment));
                    TextView comment_test = commentViews.get(i);

                    // add layout params for comments to appear below the previous one
                    LayoutParams layoutParams = (LayoutParams) comment_test.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.BELOW, aboveID);

                    comment_test.setText("");    // remove default text
                    Comment comment = this.comments.get(i);
                    stringComponents.add(comment.getUsername().getUsername_link());
                    stringComponents.add(" " + comment.getComment());
                    StringFactory.stringBuilder(comment_test, stringComponents);
                    stringComponents.clear();
                    ((ViewGroup) comment_test.getParent()).removeView(comment_test);

                    // add comment below previous one
                    commentView.addView(comment_test, layoutParams);
                    commentViews.get(i).setId(aboveID + 1);
                    aboveID = commentViews.get(i).getId();
                } else {
                    break;
                }
            }
            // add comments block into postView
            ((ViewGroup) commentView.getParent()).removeView(commentView);
            LayoutParams layoutParams = (LayoutParams) commentView.getLayoutParams();
            postView.addView(commentView, layoutParams);
        }
        return postView;
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

    public ArrayList<Like> getLikes() {
        return likes;
    }

    public View getPostView() {
        return postView;
    }

}
