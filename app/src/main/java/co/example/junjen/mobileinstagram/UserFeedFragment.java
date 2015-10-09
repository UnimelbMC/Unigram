package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ScrollViewListener;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.network.NetParams;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFeedFragment extends Fragment implements ScrollViewListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExpandableScrollView userFeedFragment;
    private ViewGroup userFeedView;
    private int postCount = 0;

    // keep track of timeSince last post generated to generate new set of posts
    private String maxPostId;
    private String minPostId;

    // flag to check if posts are being loaded before loading new ones
    private boolean loadPosts = true;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFeedFragment newInstance(String param1, String param2) {
        UserFeedFragment fragment = new UserFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // remove loading animation
        Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // initialise userFeedFragment if not created yet
        if(userFeedFragment == null){
            userFeedFragment = (ExpandableScrollView)
                    inflater.inflate(R.layout.fragment_expandable_scroll_view, container, false);
            userFeedFragment.setScrollViewListener(this);
            userFeedView = (ViewGroup) userFeedFragment.findViewById(R.id.expandable_scroll_view);

            setTitle();

            loadUserFeedPosts();

            // add layout listener to add content if default screen is not filled
            ViewTreeObserver vto = userFeedFragment.getViewTreeObserver();
            final int screenHeight = Parameters.NavigationViewHeight;
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    userFeedFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    int[] location = new int[2];
                    userFeedFragment.getLocationOnScreen(location);
                    int height = location[1] + userFeedFragment.getChildAt(0).getHeight();

                    Log.w("test", "userfeed: " + Integer.toString(height) + " (" + Integer.toString(screenHeight) + ")");

                    if (height <= screenHeight) {
                        loadUserFeedPosts();
                    }
                }
            });
        }
        return userFeedFragment;
    }

    // loads another chunk of posts when at the bottom of the user feed ScrollView
    @Override
    public void onScrollEnded(ExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {

        // load new posts if no posts are currently being loaded
        if(loadPosts){
            loadPosts = false;
            loadUserFeedPosts();
            loadPosts = true;
        }
    }

    // loads a chunk of posts on the user feed view
    private void loadUserFeedPosts(){

        LayoutInflater inflater = LayoutInflater.from(getContext());

        int maxPosts = Parameters.postsToLoad;
        int i;
     //  Post post;
        View postView;

        ArrayList<Post> userFeed;
        if(!Parameters.dummyData) {
            userFeed = NetParams.NETWORK.getUserFeed(null, maxPostId);
            int uFSize = userFeed.size();
            Log.v("NETWORK", "sizeof ufeed " + Integer.toString(uFSize));
            //Posts earlier than last
            maxPostId = userFeed.get(uFSize - 1).getPostId();
            //Posts after first
            minPostId = userFeed.get(0).getPostId();
        } else {
            userFeed = new ArrayList<>();
            int temp = postCount;
            for (i = 0; i < Parameters.postsToLoad; i++){
                userFeed.add(new Post());
                temp++;
            }
        }

        for (Post post : userFeed) {

            postView = post.getPostView(inflater);

            // if post is from dummyData
            TextView timeSinceText = (TextView) postView.findViewById(R.id.post_header_time_since);
            String timeSince = post.getTimeSince().getTimeSince();
            if(post.getUsername().getUserId().equals(Parameters.default_userId)){
                int time = Integer.parseInt(timeSince) - postCount;
                post.getTimeSince().setTimeSince(Integer.toString(time));
                timeSinceText.setText(post.getTimeSince().getTimeSinceDisplay());
            }

            userFeedView.addView(postView, postCount);
            postCount++;

        }
    }

    private void setTitle(){
        Parameters.setTitle(Parameters.NavigationBarActivity,
                Parameters.mainTitle, Parameters.mainTitleSize);
    }

    @Override
    // sets the action bar title when in a user feed fragment
    public void onAttach(Context context) {
        super.onAttach(context);

        if(userFeedFragment != null){
            setTitle();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
