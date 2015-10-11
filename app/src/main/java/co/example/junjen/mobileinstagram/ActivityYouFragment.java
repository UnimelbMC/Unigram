package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ScrollViewListener;
import co.example.junjen.mobileinstagram.customLayouts.TopBottomExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.TopScrollViewListener;
import co.example.junjen.mobileinstagram.elements.ActivityFollowing;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.TimeSince;
import co.example.junjen.mobileinstagram.network.NetParams;

/**
 * Created by junjen on 11/10/2015.
 *
 * Creates the ActivityYou fragment.
 */

public class ActivityYouFragment extends Fragment
        implements ScrollViewListener, TopScrollViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExpandableScrollView activityYouFragment;
    private ViewGroup activityYouFragmentView;
    private ViewGroup activityYouView;
    private int activityYouFragmentTop;
    private View refresh;
    private int refreshPoint;
    private boolean refreshActivity = false;
    private boolean initialised = false;
    private ArrayList<Post> allActivityYou = new ArrayList<>();

    // keep track of max post Id to generate new set of activities
    private String maxPostId;
    private String minPostId;

    // flag to check if activities are being loaded before loading new ones
    private boolean loadActivity = true;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivityYouFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityYouFragment newInstance(String param1, String param2) {
        ActivityYouFragment fragment = new ActivityYouFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ActivityYouFragment() {
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

        if(activityYouFragment == null) {

            activityYouFragment = (ExpandableScrollView)
                    inflater.inflate(R.layout.fragment_activity_you, container, false);
            activityYouFragmentView = (ViewGroup)
                    activityYouFragment.findViewById(R.id.activity_you_scroll_view);

            // set scroll listener
            activityYouFragment.setScrollViewListener(this);

            activityYouView = (ViewGroup) activityYouFragment.
                    findViewById(R.id.activity_you_scroll_view);

            setTitle();

            // add layout listener to add user feed posts if default screen is not filled
            fillDefaultScreen();

            // add refresh bar at the top of activity feed
            View layout = inflater.inflate(R.layout.pull_down_refresh, null, false);
            refresh = layout.findViewById(R.id.refreshPanel);

            // initialise scroll view position using a global layout listener
            initialisePosition();

            ((ViewGroup)refresh.getParent()).removeView(refresh);
            activityYouView.addView(refresh, 0);

            // move back to activity feed view if user scrolls into refresh bar
            // (after user's finger lifts off the screen)
            setReturnToTopListener();

            // load initial chunk of user feed posts
            loadActivityYou();

        }
        // Inflate the layout for this fragment
        return activityYouFragment;
    }

    // add layout listener to add content if default screen is not filled
    private void fillDefaultScreen(){
        ViewTreeObserver vto = activityYouFragment.getViewTreeObserver();
        final int screenHeight = Parameters.NavigationViewHeight;
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                activityYouFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] location = new int[2];
                activityYouFragment.getLocationOnScreen(location);
                LinearLayout layout = (LinearLayout) activityYouFragment.getChildAt(0);
                int height = location[1] + layout.getChildAt(1).getHeight();

                if (height <= screenHeight) {
                    loadActivityYou();
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
                activityYouFragmentTop = refresh.getBottom();

                refreshPoint = Math.round(activityYouFragmentTop / Parameters.refreshThreshold);

                // set scroll to initial position if user feed is being initialised
                if (!initialised) {
                    returnToTop(activityYouFragmentTop, Parameters.refreshReturnDelay);
                    initialised = true;
                    Parameters.activityYouFragmentTop = activityYouFragmentTop;
                }
            }
        });
    }

    // move back to activity feed view if user scrolls into refresh bar
    // (after user's finger lifts off the screen)
    private void setReturnToTopListener(){
        activityYouFragment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int scrollY = v.getScrollY();

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (scrollY <= refreshPoint) {
                        // if new activity loading, delay before returning to top of scroll view
                        returnToTop(0, 0);
                        returnToTop(activityYouFragmentTop, Parameters.refreshReturnDelay);
                    } else if (scrollY > refreshPoint && scrollY < activityYouFragmentTop) {
                        returnToTop(activityYouFragmentTop, 0);
                    }
                }
                return false;
            }
        });
    }

    // return scroll level to top of activity feed view
    private void returnToTop(final int scrollY, int delay){
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                activityYouFragment.smoothScrollTo(0, scrollY);
            }
        }, delay);
    }

    // loads another chunk of activityFollowing when at the bottom of the activity feed ScrollView
    @Override
    public void onScrollEnded(ExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {

        // load new posts if no posts are currently being loaded
        if(loadActivity){
            loadActivity = false;
            loadActivityYou();
            loadActivity = true;
        }
    }

    // add scroll listener to update activityFollowing if scroll past top of activity view
    @Override
    public void onScrollTop(TopBottomExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y >= activityYouFragmentTop) {
            refreshActivity = true;
        } else if (y < activityYouFragmentTop) {
            if (y <= refreshPoint && refreshActivity) {
                // if user scroll past a threshold level of the refresh bar, get newer activity
                refreshActivityFollowing();
            }
        }
    }

    // loads a chunk of activityFollowing on the activity view
    private void loadActivityYou() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int i;
        ArrayList<Post> activityFeed;

        if (!Parameters.dummyData) {
            // TODO: update method
//            activityFeed = NetParams.NETWORK.getUserFeed(null, maxPostId);
            activityFeed = NetParams.NETWORK.getMediaUserLikes();
           // activityFeed = new ArrayList<>();

            int aFsize = activityFeed.size();
            if (activityFeed.size() > 0){
                //Posts earlier than last
                maxPostId = activityFeed.get(aFsize - 1).getPostId();
            }
        } else {
            activityFeed = new ArrayList<>();
            int size = Parameters.activityYouIconsPerRow * Parameters.activityYouRowsToLoad;
            for (i = 0; i < size; i++) {
                activityFeed.add(new Post());
            }
        }
        Post.buildPostIcons(inflater, activityYouFragmentView, activityFeed,
                Parameters.activityYouIconsPerRow, Parameters.activityYouRowsToLoad);

        allActivityYou.addAll(activityFeed);
    }

    // get new activity of following
    private void refreshActivityFollowing() {

        // flag set so that posts are only refreshed once until view moves back
        // to the activity view
        refreshActivity = false;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int i;
        ArrayList<Post> activityFeed;

        if (!Parameters.dummyData) {
            // TODO: update method
//            activityFeed = NetParams.NETWORK.getMediaUserLikes(minPostId, null);
            activityFeed = new ArrayList<>();

            if (activityFeed.size() > 0){
                //Posts earlier than last
                minPostId = activityFeed.get(0).getPostId();
            }
        } else {
            activityFeed = new ArrayList<>();
            int size = Parameters.activityYouIconsPerRow * Parameters.activityYouRowsToLoad;
            for (i = 0; i < size; i++) {
                activityFeed.add(new Post());
            }
        }
        allActivityYou.addAll(0, activityFeed);
        activityYouFragmentView.removeAllViews();
        Post.buildPostIcons(inflater, activityYouFragmentView, allActivityYou,
                Parameters.activityYouIconsPerRow, Parameters.activityYouRowsToLoad);
    }

    // set title of activity fragment
    private void setTitle() {
        Parameters.setTitle(Parameters.NavigationBarActivity,
                Parameters.activityTitle, Parameters.subTitleSize);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(activityYouFragment != null){
            setTitle();
            Parameters.NavigationBarActivity.activityFeedBar(true);
        }
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
