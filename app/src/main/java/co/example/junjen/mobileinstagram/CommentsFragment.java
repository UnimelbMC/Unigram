package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;

import co.example.junjen.mobileinstagram.elements.Comment;
import co.example.junjen.mobileinstagram.elements.Image;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.StringFactory;
import co.example.junjen.mobileinstagram.elements.TimeSince;
import co.example.junjen.mobileinstagram.elements.Username;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String post_key = "post";
    private static final String comments_key = "comments";
    private static final String username_key = "username";
    private static final String userImage_key = "userImage";
    private static final String caption_key = "caption";
    private static final String timeSince_key = "timeSince";


    // TODO: Rename and change types of parameters
    private Post post;
    private ArrayList<Comment> comments;
    private Username username;
    private Image userImage;
    private String caption;
    private TimeSince timeSince;

    private View commentsFragment;
    private EditText commentToSend;
    private ScrollView commentsScrollView;
    private View loadMoreCommentsBar;
    private ViewGroup commentsContent;
    private int commentCount = 0;
    private int commentsSize = 0;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post Parameter 1.
     * @return A new instance of fragment CommentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentsFragment newInstance(Post post) {

        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(post_key, post);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            Log.w("test", "CommentsFragment onCreate");

            post = (Post) getArguments().getSerializable(post_key);

            comments = post.getComments();
            username = post.getUsername();
            userImage = post.getUserImage();
            caption = post.getCaption();
            timeSince = post.getTimeSince();

            // display back button
            Parameters.NavigationBarActivity.showBackButton();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // remove loading animation
        Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        if(commentsFragment == null) {

            Log.w("test", "CommentsFragment onCreateView");

            // change action bar title
            setTitle();

            commentsFragment = inflater.inflate(R.layout.fragment_comments, container, false);

            // bind method to send comment button
            Button sendButton = (Button) commentsFragment.findViewById(R.id.comment_send_button);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendComment();
                }
            });

            // load field for adding a comment
            commentToSend = (EditText) commentsFragment.findViewById(R.id.comment_send_text);

            commentsScrollView = (ScrollView) commentsFragment.findViewById(R.id.comments_scroll_view);
            ArrayList<CharSequence> stringComponents = new ArrayList<>();

            if (comments != null) {
                commentsContent = (ViewGroup) commentsFragment.findViewById(R.id.comments_content);
                View commentsCaption = commentsFragment.findViewById(R.id.comments_caption);
                loadMoreCommentsBar = commentsFragment.findViewById(R.id.load_more_comments_bar);

                // Comments caption
                if (caption != null) {
                    ImageView userImage = (ImageView) commentsCaption.findViewById(R.id.comment_user_image);
                    TextView username = (TextView) commentsCaption.findViewById(R.id.comment_username);
                    View commentElement = commentsCaption.findViewById(R.id.comments_caption);
                    TextView caption = (TextView) commentElement.findViewById(R.id.comment_text);
                    TextView timeSince = (TextView) commentElement.findViewById(R.id.comment_time_since);

                    Image.setImage(userImage, this.userImage);
                    username.setText("");   // remove default text
                    stringComponents.add(this.username.getUsernameLink());
                    StringFactory.stringBuilder(username, stringComponents);
                    stringComponents.clear();
                    caption.setText(this.caption);
                    timeSince.setText(this.timeSince.getTimeSinceDisplay());


                } else {
                    commentsCaption.setVisibility(View.GONE);
                }

                commentsSize = comments.size();

                // "Load more comments" link
                TextView loadMoreComments = (TextView) loadMoreCommentsBar.findViewById(R.id.load_more_comments);

                if (commentsSize <= Parameters.loadCommentThreshold) {
                    loadMoreCommentsBar.setVisibility(View.GONE);
                } else {
                    // TODO: set onclick listener to load more comments

                    String text = this.getActivity().getResources().getString(R.string.default_load_more_comments);

                    SpannableString commentLink = StringFactory.createLink(text, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: load more comments
                            loadMoreComments();
                        }
                    });
                    loadMoreComments.setText("");    // remove default text
                    stringComponents.add(commentLink);
                    StringFactory.stringBuilder(loadMoreComments, stringComponents);
                    stringComponents.clear();
                }

                // Comments content
                loadMoreComments();
            }

            // focus to most recent comment at the bottom
            commentsScrollView.post(new Runnable() {
                @Override
                public void run() {
                    commentsScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
        // Inflate the layout for this fragment
        return commentsFragment;
    }

    // loads a number of comments based on a threshold
    public void loadMoreComments(){

        LayoutInflater inflater = LayoutInflater.from(getContext());

        int i;
        int index;
        int loadCommentThreshold = Parameters.loadCommentThreshold;
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        // load chunk of comments based on a threshold
        for (i = 0; i < loadCommentThreshold; i++){
            index = commentsSize - 1 - commentCount;

            if (index > commentsSize - 1 || index < 0) {
                loadMoreCommentsBar.setVisibility(View.GONE);
                break;
            }
            // load a comment
            Comment comment = comments.get(index);
            View commentElement = fillComment(comment, index);
            commentsContent.addView(commentElement, 0);
            commentCount++;
        }
    }

    // fill a comment element view with comment details
    private View fillComment(Comment comment, int index){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        // load comment element view components
        View commentElement = inflater.inflate(R.layout.comments_element, commentsContent, false);
        ImageView userImage = (ImageView) commentElement.findViewById(R.id.comment_user_image);
        TextView username = (TextView) commentElement.findViewById(R.id.comment_username);
        TextView timeSince = (TextView) commentElement.findViewById(R.id.comment_time_since);
        TextView commentText = (TextView) commentElement.findViewById(R.id.comment_text);

        username.setText("");   // remove default text
        stringComponents.add(comment.getUsername().getUsernameLink());
        StringFactory.stringBuilder(username, stringComponents);
        stringComponents.clear();
        commentText.setText(comment.getComment());

        if (comment.getUsername().getUsername().startsWith(Parameters.default_username)){
            timeSince.setText(Integer.toString(Math.abs(index - commentsSize + 1)) + "s");
        } else {
            Image.setImage(userImage, comment.getUserImage());
            timeSince.setText(comment.getTimeSince().getTimeSinceDisplay());
        }
        return commentElement;
    }

    // send the comment in the input field when send button is clicked
    public void sendComment(){

        String commentText = commentToSend.getText().toString();

        Comment comment = new Comment(Parameters.loginUserId, Parameters.loginUsername,
                Parameters.loginUser.getUserImage().getImageString(), commentText,
                new TimeSince());

        View commentElement = fillComment(comment, 0);
        commentsContent.addView(commentElement, commentsContent.getChildCount());

        // update post
        comments.add(comments.size(), comment);
        WeakReference<LayoutInflater> weakInflater =
                new WeakReference<>(LayoutInflater.from(getContext()));
        post.buildCommentView(weakInflater.get());

        commentsScrollView.post(new Runnable() {
            @Override
            public void run() {
                commentsScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        // clear input field
        commentToSend.setText("");
    }

    // sets the action bar title when in a comment fragment
    public void setTitle(){
        Parameters.setTitle(Parameters.NavigationBarActivity, Parameters.commentsTitle,
                Parameters.subTitleSize);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setTitle();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
