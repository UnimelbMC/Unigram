package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ScrollViewListener;
import co.example.junjen.mobileinstagram.customLayouts.ToggleButton;
import co.example.junjen.mobileinstagram.customLayouts.TopBottomExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.TopScrollViewListener;
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
public class UserFeedFragment extends Fragment
        implements ScrollViewListener, TopScrollViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TopBottomExpandableScrollView userFeedFragment;
    private ViewGroup userFeedView;
    private ToggleButton sortButton;
    private int postIndex = 0;
    private int postBottomCount = 0;
    private int postTopCount = 0;
    private int userFeedFragmentTop = 0;
    private View refresh;
    private int refreshPoint;
    private int currentHeight;
    private boolean refreshPost = false;
    private boolean initialised = false;
    private boolean loadInitialFeed = true;
    private ArrayList<Post> allPosts = new ArrayList<>();

    // keep track of max and min id of last post generated to generate new set of posts
    private String maxPostId = null;
    private String minPostId = null;

    // flag to check if posts are being loaded before loading new ones
    private boolean loadPosts = true;

    // sort flag
    private boolean showPostByTime = true;
    private boolean prevSortWasTime = true;

    private OnFragmentInteractionListener mListener;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

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

            ActionBar actionBar = Parameters.NavigationBarActivity.getSupportActionBar();
            if(actionBar != null) {
                // bind sortButton click to sort flag
                sortButton = (ToggleButton)
                        actionBar.getCustomView().findViewById(R.id.sort_button);
                // default checked to sort by time
                sortButton.setChecked(Parameters.default_sortByTime);
                sortButton.setOnClickListener(new View.OnClickListener() {
                    // Handle clicks for sort button
                    @Override
                    public void onClick(View v) {
                        if(!Parameters.dummyData){
                            if (sortButton.isChecked()) {
                                showPostByTime = true;
                            } else {
                                showPostByTime = false;
                            }
                            updateUserFeedView();
                        }
                    }
                });
            }

            userFeedFragment = (TopBottomExpandableScrollView)
                    inflater.inflate(R.layout.fragment_top_bottom_expandable_scroll_view, container, false);

            // set scroll listeners
            userFeedFragment.setScrollViewListener(this);
            userFeedFragment.setTopScrollViewListener(this);

            userFeedView = (ViewGroup) userFeedFragment.findViewById(R.id.top_bottom_expandable_scroll_view);

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

            // move back to user feed view if user scrolls into refresh bar
            // (after user's finger lifts off the screen)
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
                currentHeight = location[1] + userFeedFragment.getChildAt(0).getHeight();

                if (currentHeight <= screenHeight) {
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
                    returnToTop(userFeedFragmentTop, Parameters.refreshReturnDelay);
                    initialised = true;
                    userFeedFragment.setTopLevel(userFeedFragmentTop);
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

    // add scroll listener to update posts if scroll past top of user feed
    @Override
    public void onScrollTop(TopBottomExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y >= userFeedFragmentTop) {
            refreshPost = true;
        } else if (y < userFeedFragmentTop) {
            if (y <= refreshPoint && refreshPost) {
                // if user scroll past a threshold level of the refresh bar, get newer posts
                refreshUserFeedPosts();
            }
        }
    }

    // loads a chunk of posts on the user feed view
    private void loadUserFeedPosts() {

        WeakReference<LayoutInflater> weakInflater =
                new WeakReference<>(LayoutInflater.from(getContext()));
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
                if(loadInitialFeed) {
                    //Posts after first
                    minPostId = userFeed.get(0).getPostId();
                    loadInitialFeed = false;
                }
            }
        } else {
            userFeed = new ArrayList<>();
            for (i = 0; i < Parameters.postsToLoad; i++) {
                userFeed.add(new Post());
            }
        }
        for (Post post : userFeed) {
            postView = post.getPostView(weakInflater.get());

            // if post is from dummyData
            if (Parameters.dummyData) {
                TextView timeSinceText = (TextView)
                        postView.findViewById(R.id.post_header_time_since);
                timeSinceText.setText(Integer.toString(postBottomCount) + "s");
            }
            if(postView != null && showPostByTime){
                userFeedView.addView(postView, postIndex + 1);
            }
            postBottomCount++;
            postIndex++;
        }
        allPosts.addAll(userFeed);

        if(!Parameters.dummyData) {
            updateUserFeedView();
        }
    }

    // get new userfeed posts
    private void refreshUserFeedPosts() {

        // flag set so that posts are only refreshed once until view moves back
        // to the user feed view
        refreshPost = false;

        WeakReference<LayoutInflater> weakInflater =
                new WeakReference<>(LayoutInflater.from(getContext()));
        int i;
        View postView;
        ArrayList<Post> userFeed;

        if (!Parameters.dummyData) {
            userFeed = NetParams.NETWORK.getUserFeed(minPostId, null);
            int uFSize = userFeed.size();
            Log.v("NETWORK", "sizeof ufeed " + Integer.toString(uFSize));
            if (userFeed.size() > 0){
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
            postView = post.getPostView(weakInflater.get());

            // if post is from dummyData
            if (Parameters.dummyData) {
                TextView timeSinceText = (TextView)
                        postView.findViewById(R.id.post_header_time_since);
                timeSinceText.setText(Integer.toString(-postTopCount + i) + "s");
            }
            if(postView != null && showPostByTime){
                userFeedView.addView(postView, i + 1);
            }
            i++;
        }
        postIndex += size;
        allPosts.addAll(0, userFeed);

        if(!Parameters.dummyData) {
            updateUserFeedView();
        }
    }

    // update time since posted of all posts
    private void updateUserFeedView(){

        // re-sort by location
        if(!showPostByTime){
            Log.v("sort", "showByLoc");
            Post.sortPostByLocation(allPosts);
            prevSortWasTime = false;
            userFeedView.removeAllViews();
            for (Post post : allPosts) {
                if (post.getLocation() != null){
                    Log.v("sort",Double.toString(post.getLocDiff()));
                }else{
                    Log.v("sort",post.getUsername().getUsername());
                }
                userFeedView.addView(post.getPostView());
            }
            userFeedView.addView(refresh, 0);
        }
        // re-sort by time if needed
        else if(showPostByTime && !prevSortWasTime){
            prevSortWasTime = true;
            Post.sortPostByTime(allPosts);
            userFeedView.removeAllViews();
            for (Post post : allPosts){
                if (post.getLocation() != null){
                    Log.v("sort",Double.toString(post.getLocDiff()));
                }else{
                    Log.v("sort",post.getUsername().getUsername());
                }
                userFeedView.addView(post.getPostView());
            }
            userFeedView.addView(refresh, 0);
        }

        for (Post post : allPosts){
            if(post.getLocation()!= null){
                Log.v("DIFF",Double.toString(post.getLocDiff()));
            }
            TimeSince timeSince = post.getTimeSince();

            // update post time since
            timeSince.formatTime(timeSince.getTimeSince());

            // display updated time since
            if(post.getPostView() != null) {
                ((TextView) post.getPostView().findViewById(R.id.post_header_time_since)).
                        setText(timeSince.getTimeSinceDisplay());
            }
        }
    }

    // set title of user feed fragment
    private void setTitle() {
        Parameters.setTitle(Parameters.NavigationBarActivity,
                Parameters.mainTitle, Parameters.mainTitleSize);
        Parameters.NavigationBarActivity.activityFeedBar(false);
    }

    @Override
    // sets the action bar title when in a user feed fragment
    public void onAttach(Context context) {
        super.onAttach(context);

        if (userFeedFragment != null) {
            setTitle();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(allPosts != null){
            for(Post post : allPosts){
                post.checkLikeButton();
            }
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


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if(item.getItemId()==R.id.action_swipe){
//            Toast.makeText(getActivity(),"userfeedfragment",Toast.LENGTH_LONG).show();
//
////            Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
////            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
//            return true;
//
//        }
//        return super.onOptionsItemSelected(item);
//    }


    // Bluetooth swipe action




}
