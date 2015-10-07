package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Post objects.
 */

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import co.example.junjen.mobileinstagram.CommentsFragment;
import co.example.junjen.mobileinstagram.PostFragment;
import co.example.junjen.mobileinstagram.UsersFragment;
import co.example.junjen.mobileinstagram.NavigationBar;
import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.SquareImageView;
import co.example.junjen.mobileinstagram.customLayouts.UserImageView;

public class Post implements Serializable{

    private String postId;

    // post header
    private Image userImage;
    private Username username;
    private Location location;
    private TimeSince timeSince;

    // post content
    private Image postImage;
    private String caption;
    private ArrayList<User> likes;
    private ArrayList<Comment> comments;

    // post view
    private RelativeLayout postView;

    public Post(){
        // test constructor to create 'empty' Post objects

        this.postId = Parameters.default_postId;
        this.userImage = new Image(Parameters.default_image);
        this.username = new Username(Parameters.default_username);
        this.location = new Location(Parameters.default_location);
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
            this.likes.add(new User(username, Parameters.default_image,
                    Parameters.default_profName));
        }
        // create 10 empty comments
        for (i = 0; i < 10; i++){
            username = Parameters.default_username + (i + 1);
            comment = Parameters.default_comment + (i + 1);
            this.comments.add(new Comment(username, Parameters.default_image, comment,
                    new TimeSince(Parameters.default_timeSince)));
        }
    }

    public Post(String postId, String userImage, String username, Location location, String timeSince,
                String postImage, String caption, ArrayList<User> likes, ArrayList<Comment> comments){

        // TODO: Assumes strings as parameters. Set appropriately later on.

        this.postId = postId;
        this.userImage = new Image(userImage);
        this.username = new Username(username);
        this.location = location;
        this.timeSince = new TimeSince(timeSince);
        this.postImage = new Image(postImage);
        this.caption = caption;

        this.likes = likes;
        this.comments = comments;
    }

    public View getPostView(LayoutInflater inflater){

        postView = (RelativeLayout)
                inflater.inflate(R.layout.post, null, false);
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        /** Fixed parameters **/

        // User image
        if(!this.userImage.getImageString().equals(Parameters.default_image)) {
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
        timeSince.setText(this.timeSince.getTimeSince());

        // Post image
        if(!this.postImage.getImageString().equals(Parameters.default_image)) {
            ImageView postImage = (ImageView) postView.findViewById(R.id.post_image);
            Image.setImage(postImage, this.postImage);
        }

        // TODO: Handle clicks for like button

        /** Optional parameters **/

        // TODO: Confirm for 'null' return type if optional params do not exist

        // Location
        TextView location = (TextView) postView.findViewById(R.id.post_header_location);
        if (this.location != null){
            location.setText("");    // remove default text
            stringComponents.add(this.location.getLocation());
            StringFactory.stringBuilder(location, stringComponents);
            stringComponents.clear();
        } else {
            location.setVisibility(View.GONE);
        }

        // Likes
        RelativeLayout likeLine = (RelativeLayout) postView.findViewById(R.id.like_count_line);
        if (this.likes != null){
            TextView likeCountText = (TextView) postView.findViewById(R.id.like_count);
            int likeCount = this.likes.size();
            int likeThreshold = Parameters.likeThreshold;

            // add link to all likes if more than threshold
            if(likeCount > likeThreshold){
                likeCountText.setText(likeCount + " likes");
                String text = likeCount + " likes";

                SpannableString likeLink = StringFactory.createLink(text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // display post's likes
                        Parameters.NavigationBarActivity.showFragment
                                (UsersFragment.newInstance(likes, Parameters.likesTitle));
                    }
                });
                likeCountText.setText("");    // remove default text
                stringComponents.add(likeLink);
                StringFactory.stringBuilder(likeCountText, stringComponents);
                stringComponents.clear();
            }
            else {
                likeCountText.setText("");  // remove default text
                int i;
                for (i = 0; i < likeCount; i++){
                    stringComponents.add(this.likes.get(i).getUsername().getUsernameLink());
                    stringComponents.add(", ");
                }
                stringComponents.remove(stringComponents.size() - 1);   // remove trailing comma
                StringFactory.stringBuilder(likeCountText, stringComponents);
                stringComponents.clear();
            }
        } else {
            likeLine.setVisibility(View.GONE);
        }

        // Caption
        TextView caption = (TextView) postView.findViewById(R.id.post_caption);
        if (this.caption != null){
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

    // builds the view for the comments portion in a post
    private void buildCommentView(LayoutInflater inflater){

        ArrayList<CharSequence> stringComponents = new ArrayList<>();
        TextView commentCountText = (TextView) postView.findViewById(R.id.post_comment_count);
        if (this.comments != null){

            // add link to show all comments if more than 3 comments
            int commentsSize = this.comments.size();
            int commentThreshold = Parameters.commentThreshold;
            if (commentsSize > commentThreshold){
                String text = "View all " + commentsSize + " comments";

                SpannableString commentLink = StringFactory.createLink(text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // display post's comments
                        Parameters.NavigationBarActivity.showFragment(CommentsFragment.
                                newInstance(comments, username, userImage, caption, timeSince));
                    }
                });
                commentCountText.setText("");    // remove default text
                stringComponents.add(commentLink);
                StringFactory.stringBuilder(commentCountText, stringComponents);
                stringComponents.clear();
            } else {
                commentCountText.setVisibility(View.GONE);
            }

            // dynamically add preview of maximum 3 comments below commentCountText
            ViewGroup commentsView = (ViewGroup) postView.findViewById(R.id.post_comments);

            int i;
            int index;
            int commentCount = 0;

            for (i = 0; i < commentThreshold; i++){
                index = commentsSize - 1 - commentCount;

                if (index > commentsSize - 1 || index < 0) break;

                View commentPreview = inflater.inflate(R.layout.post_comment, commentsView, false);
                TextView commentText = (TextView) commentPreview.findViewById(R.id.post_comment);
                Comment comment = comments.get(index);

                if (comment.getUsername().getUsername().startsWith(Parameters.default_username)){

                    commentText.setText("");    // remove default text
                    stringComponents.add(comment.getUsername().getUsernameLink());
                    stringComponents.add(" " + comment.getComment());
                    StringFactory.stringBuilder(commentText, stringComponents);
                    stringComponents.clear();

                } else {
                    // TODO: get Data Object
                }
                commentsView.addView(commentPreview, 0);
                commentCount++;
            }
        }
    }

    // get layout of post icons to be added to the bottom of a profile fragment
    public static void getPostIcons(LayoutInflater inflater, ViewGroup postIconList,
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

                    imageView.setContentDescription((CharSequence) post.getPostId());
                    imageView.setOnClickListener(Post.postIconOnClickListener());

                } else {
                    imageView.setImageResource(0);
                }
                // add post icon into row
                postIconRow.addView(imageView, postIconRow.getChildCount());
            }
            // add row to list
            postIconList.addView(postIconRow, postIconList.getChildCount());
        }
    }

    // OnClickListener for post icon clicks
    public static View.OnClickListener postIconOnClickListener(){

        View.OnClickListener postIconOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postId = (String) v.getContentDescription();

                if(postId.equals(Parameters.default_postId)){

                } else {

                    // TODO: get post based on postId through Network

                    // display post's comments
                    Parameters.NavigationBarActivity.showFragment(PostFragment.newInstance(postId));

                }
            }
        };

        return postIconOnClickListener;
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
}
