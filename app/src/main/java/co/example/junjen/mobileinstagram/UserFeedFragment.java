package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ScrollViewListener;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.TimeSince;
import co.example.junjen.mobileinstagram.network.NetParams;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFeedFragment extends Fragment implements ScrollViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExpandableScrollView userFeedFragment;
    private ViewGroup userFeedView;
    private int postIndex = 0;
    private int postBottomCount = 0;
    private int postTopCount = 0;
    private int userFeedFragmentTop;
    private View refresh;
    private int refreshPoint;
    private boolean refreshPost = false;
    private boolean initialised = false;
    private ArrayList<Post> allPosts = new ArrayList<>();

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

            // add layout listener to add user feed posts if default screen is not filled
            fillDefaultScreen();

            // add refresh bar at the top of user feed
            View layout = inflater.inflate(R.layout.pull_down_refresh, null, false);
            refresh = layout.findViewById(R.id.refreshPanel);

            // initialise scroll view position using a global layout listener
            initialisePosition();

            ((ViewGroup)refresh.getParent()).removeView(refresh);
            userFeedView.addView(refresh, 0);

            // add scroll listener to update posts if scroll past top of user feed
            //setTopScrollListener();

            // move back to user feed view if user scrolls into refresh bar
            // (after user's finger lifts off the screen
            setReturnToTopListener();

            // load initial chunk of user feed posts
            loadUserFeedPosts();
        }
        return userFeedFragment;
    }


    // add layout listener to add content if default screen is not filled
    private void fillDefaultScreen(){
        ViewTreeObserver vto = userFeedFragment.getViewTreeObserver();
        final int screenHeight = Parameters.NavigationViewHeight;
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                userFeedFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] location = new int[2];
                userFeedFragment.getLocationOnScreen(location);
                int height = location[1] + userFeedFragment.getChildAt(0).getHeight();

                if (height <= screenHeight) {
                    loadUserFeedPosts();
                }
            }
        });
    }

    // initialise scroll view position using a global layout listener
    private void initialisePosition(){
        ViewTreeObserver vto = refresh.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refresh.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // get starting position of user feed scroll view
                userFeedFragmentTop = refresh.getBottom();

                refreshPoint = Math.round(userFeedFragmentTop / Parameters.refreshThreshold);

                // set scroll to initial position if user feed is being initialised
                if (!initialised) {
                    returnToTop(userFeedFragmentTop, 0);
                    initialised = true;
                    Parameters.userFeedFragmentTop = userFeedFragmentTop;
                }
            }
        });
    }

    // add scroll listener to update posts if scroll past top of user feed
    private void setTopScrollListener(){
        userFeedFragment.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= userFeedFragmentTop) {
                    refreshPost = true;
                } else if (scrollY < userFeedFragmentTop) {
                    if (scrollY <= refreshPoint && refreshPost) {
                        // if user scroll past a threshold level of the refresh bar, get newer posts
                        refreshUserFeedPosts();
                    }
                }
            }
        });
    }

    // move back to user feed view if user scrolls into refresh bar
    // (after user's finger lifts off the screen)
    private void setReturnToTopListener(){
        userFeedFragment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int scrollY = v.getScrollY();

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (scrollY <= refreshPoint) {
                        // if new user feed loading, delay before returning to top of scroll view
                        returnToTop(0, 0);
                        returnToTop(userFeedFragmentTop, Parameters.refreshReturnDelay);
                    } else if (scrollY > refreshPoint && scrollY < userFeedFragmentTop) {
                        returnToTop(userFeedFragmentTop, 0);
                    }
                }
                return false;
            }
        });
    }

    // return scroll level to top of user feed view
    private void returnToTop(final int scrollY, int delay){
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                userFeedFragment.smoothScrollTo(0, scrollY);
            }
        }, delay);
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
    private void loadUserFeedPosts() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int i;
        View postView;
        ArrayList<Post> userFeed;

        if (!Parameters.dummyData) {
            userFeed = NetParams.NETWORK.getUserFeed(null, maxPostId);
            int uFSize = userFeed.size();
            Log.v("NETWORK", "sizeof ufeed " + Integer.toString(uFSize));
            if (userFeed.size() > 0){
                //Posts earlier than last
                maxPostId = userFeed.get(uFSize - 1).getPostId();
                //Posts after first
                minPostId = userFeed.get(0).getPostId();
            }
        } else {
            userFeed = new ArrayList<>();
            for (i = 0; i < Parameters.postsToLoad; i++) {
                userFeed.add(new Post());
            }
        }
        for (Post post : userFeed) {
            postView = post.getPostView(inflater);

            // if post is from dummyData
            if (Parameters.dummyData) {
                TextView timeSinceText = (TextView)
                        postView.findViewById(R.id.post_header_time_since);
                timeSinceText.setText(Integer.toString(postBottomCount) + "s");
            }
            if(postView != null){
                userFeedView.addView(postView, postIndex + 1);
            }
            postBottomCount++;
            postIndex++;
        }
        allPosts.addAll(userFeed);
        updateTimeSince();
    }

    // get new userfeed posts
    private void refreshUserFeedPosts() {

        // flag set so that posts are only refreshed once until view moves back
        // to the user feed view
        refreshPost = false;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int i;
        View postView;
        ArrayList<Post> userFeed;

        if (!Parameters.dummyData) {
            userFeed = NetParams.NETWORK.getUserFeed(minPostId, null);
            int uFSize = userFeed.size();
            Log.v("NETWORK", "sizeof ufeed " + Integer.toString(uFSize));
            if (userFeed.size() > 0){
                //Posts earlier than last
                maxPostId = userFeed.get(uFSize - 1).getPostId();
                //Posts after first
                minPostId = userFeed.get(0).getPostId();
            }
        } else {
            userFeed = new ArrayList<>();
            for (i = 0; i < Parameters.postsToLoad; i++) {
                userFeed.add(new Post());
            }
        }
        i = 0;
        int size = userFeed.size();
        postTopCount += size;
        for (Post post : userFeed) {
            postView = post.getPostView(inflater);

            // if post is from dummyData
            if (Parameters.dummyData) {
                TextView timeSinceText = (TextView)
                        postView.findViewById(R.id.post_header_time_since);
                timeSinceText.setText(Integer.toString(-postTopCount + i) + "s");
            }
            if(postView != null){
                userFeedView.addView(postView, i + 1);
            }
            i++;
        }
        postIndex += size;
        allPosts.addAll(0, userFeed);
        updateTimeSince();
    }

    // update time since posted of all posts
    private void updateTimeSince(){
        for (Post post : allPosts){
            TimeSince timeSince = post.getTimeSince();

            // update post time since
            timeSince.formatTime(timeSince.getTimeSince());

            // display updated time since
            ((TextView) post.getPostView().findViewById(R.id.post_header_time_since)).
                    setText(timeSince.getTimeSinceDisplay());
        }
    }

    // set title of user feed fragment
    private void setTitle() {
        Parameters.setTitle(Parameters.NavigationBarActivity,
                Parameters.mainTitle, Parameters.mainTitleSize);
    }

    @Override
    // sets the action bar title when in a user feed fragment
    public void onAttach(Context context) {
        super.onAttach(context);

        if (userFeedFragment != null) {
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
