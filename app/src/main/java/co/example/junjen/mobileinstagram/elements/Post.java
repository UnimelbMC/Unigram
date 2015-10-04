package co.example.junjen.mobileinstagram.elements;

/**
 * Created by junjen on 30/09/2015.
 *
 * This class creates Post objects.
 */

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import co.example.junjen.mobileinstagram.R;
import co.example.junjen.mobileinstagram.customLayouts.UserImageView;

public class Post implements Serializable{

    // post header
    private Image userImage;
    private Username username;
    private Location location;
    private TimeSince timeSince;

    // post content
    private Image postImage;
    private String caption;
    private ArrayList<Like> likes;
    private ArrayList<Comment> comments;

    // post view
    private RelativeLayout postView;

    public Post(){
        // test constructor to create 'empty' Post objects

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
            this.likes.add(new Like(username, new TimeSince(Parameters.default_timeSince)));
        }
        // create 10 empty comments
        for (i = 0; i < 10; i++){
            username = Parameters.default_username + (i + 1);
            comment = Parameters.default_comment + (i + 1);
            this.comments.add(new Comment(username, comment,
                    new TimeSince(Parameters.default_timeSince)));
        }
    }

    public Post(String userImage, String username, String location, String timeSince,
                String postImage, String caption, String likes, String comments){

        // TODO: Assumes strings as parameters. Set appropriately later on.

        this.userImage = new Image(userImage);
        this.username = new Username(username);
        this.location = new Location(location);
        this.timeSince = new TimeSince(timeSince);
        this.postImage = new Image(postImage);
        this.caption = caption;

        this.likes = createLikesList(likes);
        this.comments = createCommentsList(comments);
    }

    private ArrayList<Like> createLikesList(String likes_string){
        ArrayList<Like> likes = new ArrayList<>();

        // TODO: method to convert JSON likes_string into ArrayList<Like>

        return likes;
    }

    private ArrayList<Comment> createCommentsList(String comments_string){
        ArrayList<Comment> comments = new ArrayList<>();

        // TODO: method to convert comments_string into ArrayList<Comment>

        return comments;
    }

    public View getPostView(LayoutInflater inflater, ViewGroup parentView){

        // TODO: set 'onClickListener" for any applicable views
        // Example:
        // TextView t2 = (TextView) findViewById(R.id.text2);
        // t2.setMovementMethod(LinkMovementMethod.getInstance());

        postView = (RelativeLayout)
                inflater.inflate(R.layout.post, parentView, false);
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        /** Fixed parameters **/

        // User image
        if(!this.userImage.getImageString().equals(Parameters.default_image)) {
            UserImageView userImage = (UserImageView)
                    postView.findViewById(R.id.post_header_user_image);
            Image.setImage(userImage, this.userImage.getImage());
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
            Image.setImage(postImage, this.postImage.getImage());
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

            // add link to show all comments if more than 3 comments
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
            ViewGroup commentsView = (ViewGroup) postView.findViewById(R.id.post_comments);
            ArrayList<TextView> commentViews = new ArrayList<>();

            int i;
            for (i = 0; i < commentThreshold; i++){
                if (commentCount > i) {

                    // get post_comment layout
                    View commentLayout =
                            inflater.inflate(R.layout.post_comment, commentsView, true);
                    commentViews.add((TextView) commentLayout.findViewById(R.id.post_comment));
                    TextView commentView = commentViews.get(i);

                    // add layout params for comments to appear below the previous one
                    RelativeLayout.LayoutParams layoutParams =
                            (RelativeLayout.LayoutParams) commentView.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.BELOW, aboveID);

                    commentView.setText("");    // remove default text
                    Comment comment = this.comments.get(i);
                    stringComponents.add(comment.getUsername().getUsername_link());
                    stringComponents.add(" " + comment.getComment());
                    StringFactory.stringBuilder(commentView, stringComponents);
                    stringComponents.clear();
                    ((ViewGroup) commentView.getParent()).removeView(commentView);

                    // add comment below previous one
                    commentsView.addView(commentView, layoutParams);
                    commentViews.get(i).setId(aboveID + 1);
                    aboveID = commentViews.get(i).getId();
                } else {
                    break;
                }
            }
            // add comments block into postView
            ((ViewGroup) commentsView.getParent()).removeView(commentsView);
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) commentsView.getLayoutParams();
            postView.addView(commentsView, layoutParams);
        }
        return postView;
    }

    public static void getPostIcons(LayoutInflater inflater, ViewGroup postIconList,
                                    ArrayList<Post> posts){

        int postsSize = posts.size();
        int postIconsPerRow = Parameters.postIconsPerRow;
        int postIconRowsToLoad = Parameters.postIconRowsToLoad;

        int i;
        int index = 0;
        for (i = 0; i < postIconRowsToLoad; i++){

            LinearLayout postIconRow = (LinearLayout) inflater.inflate(R.layout.post_icon_row, null, false);

            int j;
            for (j = 0; j < postIconsPerRow; j++){
                index = i * postIconsPerRow + j;

                // get post_icon_row layout
                View postIconLayout = inflater.inflate(R.layout.post_icon, null, false);
                ImageView imageView = (ImageView) postIconLayout.findViewById(R.id.post_icon);
                ((ViewGroup) imageView.getParent()).removeView(imageView);

                if ((postsSize - i * postIconsPerRow) - j > 0) {
                    Post post = posts.get(index);
                    if (!post.getPostImage().getImageString().equals(Parameters.default_image)) {
                        Image.setImage(imageView, post.getPostImage().getImage());
                    }

                } else {
                    imageView.setImageResource(R.drawable.empty_user_image);
                }
                // add post icon into row
                postIconRow.addView(imageView, postIconRow.getChildCount());
            }
            // add icon row to list
            postIconList.addView(postIconRow, postIconList.getChildCount());

            if (index >= postsSize - 1){
                break;
            }
        }
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
}
