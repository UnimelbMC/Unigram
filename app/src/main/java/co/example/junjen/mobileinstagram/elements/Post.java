package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Post objects.
 */

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        this.userImage = new Image();
        this.username = new Username("username");
        this.timeSince = new TimeSince();
        this.postImage = new Image();
        this.caption = "caption";

        this.likes = new ArrayList<>();
        this.likes.add(new Like("username1", new TimeSince()));
        this.likes.add(new Like("username2", new TimeSince()));
        this.likes.add(new Like("username3", new TimeSince()));

        this.comments = new ArrayList<>();
        this.comments.add(new Comment("username4", "comment1", new TimeSince()));
        this.comments.add(new Comment("username5", "comment2", new TimeSince()));
        this.comments.add(new Comment("username6", "comment3", new TimeSince()));

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

        ViewGroup postView = (ViewGroup) inflater.inflate(R.layout.post, parentView, false);
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        /** Fixed parameters **/

        // User image
        if(this.userImage.getImageString() != null) {
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
        if(this.postImage.getImageString() != null) {
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
            if(likeCount > 10){
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
        TextView comment1 = (TextView) postView.findViewById(R.id.post_comment_1);
        TextView comment2 = (TextView) postView.findViewById(R.id.post_comment_2);
        TextView comment3 = (TextView) postView.findViewById(R.id.post_comment_3);
        TextView[] comments = {comment1, comment2, comment3};
        if (this.comments != null){

            // add link to show all comments is more than 3 comments
            int commentCount = this.comments.size();
            if (commentCount > 3){
                commentCountText.setText("View all " + commentCount + " comments");

                // TODO: onClickListener

            } else {
                commentCountText.setVisibility(View.GONE);
            }
            int i;
            for (i = 0; i < 3; i++){
                if (commentCount > i) {
                    comments[i].setText("");    // remove default text
                    Comment comment = this.comments.get(i);
                    stringComponents.add(comment.getUsername().getUsername_link());
                    stringComponents.add(" " + comment.getComment());
                    StringFactory.stringBuilder(comments[i], stringComponents);
                    stringComponents.clear();
                } else {
                    comments[i].setVisibility(View.GONE);
                }
            }
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
