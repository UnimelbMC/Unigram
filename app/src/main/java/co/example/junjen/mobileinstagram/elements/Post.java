package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Post objects.
 */

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.R;

public class Post {

    // post header
    private Image userImage;
    private String username;
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

        this.userImage = new Image("<userImage>");
        this.username = "<username>";
        this.timeSince = new TimeSince();
        this.postImage = new Image("<postImage>");
        this.caption = "<caption>";

        this.likes = new ArrayList<>();
        this.likes.add(new Like("<username1>", new TimeSince()));
        this.likes.add(new Like("<username2>", new TimeSince()));
        this.likes.add(new Like("<username3>", new TimeSince()));

        this.comments = new ArrayList<>();
        this.comments.add(new Comment("<username4>", "<comment1>", new TimeSince()));
        this.comments.add(new Comment("<username5>", "<comment2>", new TimeSince()));
        this.comments.add(new Comment("<username6>", "<comment3>", new TimeSince()));

    }

    public Post(String userImage, String username, String timeSince, String postImage,
                String caption, String likes, String comments, LayoutInflater inflater,
                ViewGroup parentView){

        this.userImage = new Image(userImage);
        this.username = "<username>";
        this.timeSince = new TimeSince(timeSince);
        this.postImage = new Image(postImage);
        this.caption = "<caption>";

        this.likes = createLikesList(likes);
        this.comments = createCommentsList(comments);
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
        View postView = inflater.inflate(R.layout.post, parentView, false);

        // fixed parameters
        ImageView userImage = (ImageView) postView.findViewById(R.id.post_header_user_image);
        TextView username = (TextView) postView.findViewById(R.id.post_header_username);
        TextView timeSince = (TextView) postView.findViewById(R.id.post_header_time_since);
        ImageView postImage = (ImageView) postView.findViewById(R.id.post_image);

        // optional parameters
        TextView caption = (TextView) postView.findViewById(R.id.post_caption);
        TextView likes = (TextView) postView.findViewById(R.id.like_count);
        TextView comments = (TextView) postView.findViewById(R.id.post_comment_count);

        ((ViewManager)comments.getParent()).removeView(comments);
        username.setText("jun jen");


        return postView;
    }

    public Image getUserImage() {
        return userImage;
    }

    public String getUsername() {
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
