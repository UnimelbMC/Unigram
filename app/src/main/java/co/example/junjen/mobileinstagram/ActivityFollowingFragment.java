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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActivityFollowingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivityFollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityFollowingFragment extends Fragment
        implements ScrollViewListener, TopScrollViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TopBottomExpandableScrollView activityFollowingFragment;
    private ViewGroup activityFollowingView;
    private int activityFollowingIndex = 0;
    private int activityBottomCount = 0;
    private int activityTopCount = 0;
    private int activityFollowingFragmentTop;
    private View refresh;
    private int refreshPoint;
    private boolean refreshActivity = false;
    private boolean initialised = false;
    private ArrayList<ActivityFollowing> allActivityFollowing = new ArrayList<>();

    // keep track of max and min timeSince to generate new set of activities
    private String maxTimeSince;
    private String minTimeSince;

    // flag to check if activities are being loaded before loading new ones
    private boolean loadActivity = true;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivityFollowingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityFollowingFragment newInstance(String param1, String param2) {
        ActivityFollowingFragment fragment = new ActivityFollowingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ActivityFollowingFragment() {
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

        if(activityFollowingFragment == null) {

            activityFollowingFragment = (TopBottomExpandableScrollView)
                    inflater.inflate(R.layout.fragment_activity_following, container, false);

            // set scroll listeners
            activityFollowingFragment.setScrollViewListener(this);
            activityFollowingFragment.setTopScrollViewListener(this);

            activityFollowingView = (ViewGroup) activityFollowingFragment.
                    findViewById(R.id.activity_following_scroll_view);

            setTitle();

            // add layout listener to activity feed if default screen is not filled
            fillDefaultScreen();

            // add refresh bar at the top of activity feed
            View layout = inflater.inflate(R.layout.pull_down_refresh, null, false);
            refresh = layout.findViewById(R.id.refreshPanel);

            // initialise scroll view position using a global layout listener
            initialisePosition();

            ((ViewGroup)refresh.getParent()).removeView(refresh);
            activityFollowingView.addView(refresh, 0);

            // move back to activity feed view if user scrolls into refresh bar
            // (after user's finger lifts off the screen)
            setReturnToTopListener();

            // load initial chunk of activity feed
            loadActivityFollowing();

        }
        // Inflate the layout for this fragment
        return activityFollowingFragment;
    }

    // add layout listener to add content if default screen is not filled
    private void fillDefaultScreen(){
        ViewTreeObserver vto = activityFollowingFragment.getViewTreeObserver();
        final int screenHeight = Parameters.NavigationViewHeight;
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                activityFollowingFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] location = new int[2];
                activityFollowingFragment.getLocationOnScreen(location);
                int height = location[1] + activityFollowingFragment.getChildAt(0).getHeight();

                if (height <= screenHeight) {
                    loadActivityFollowing();
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
                activityFollowingFragmentTop = refresh.getBottom();

                refreshPoint = Math.round(activityFollowingFragmentTop / Parameters.refreshThreshold);

                // set scroll to initial position if user feed is being initialised
                if (!initialised) {
                    returnToTop(activityFollowingFragmentTop, Parameters.refreshReturnDelay);
                    initialised = true;
                    Parameters.activityFeedFragmentTop = activityFollowingFragmentTop;
                }
            }
        });
    }

    // move back to activity feed view if user scrolls into refresh bar
    // (after user's finger lifts off the screen)
    private void setReturnToTopListener(){
        activityFollowingFragment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int scrollY = v.getScrollY();

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (scrollY <= refreshPoint) {
                        // if new activity loading, delay before returning to top of scroll view
                        returnToTop(0, 0);
                        returnToTop(activityFollowingFragmentTop, Parameters.refreshReturnDelay);
                    } else if (scrollY > refreshPoint && scrollY < activityFollowingFragmentTop) {
                        returnToTop(activityFollowingFragmentTop, 0);
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
                activityFollowingFragment.smoothScrollTo(0, scrollY);
            }
        }, delay);
    }

    // loads another chunk of activityFollowing when at the bottom of the activity feed ScrollView
    @Override
    public void onScrollEnded(ExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {

        // load new posts if no posts are currently being loaded
        if(loadActivity){
            loadActivity = false;
            loadActivityFollowing();
            loadActivity = true;
        }
    }

    // add scroll listener to update activityFollowing if scroll past top of activity view
    @Override
    public void onScrollTop(TopBottomExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y >= activityFollowingFragmentTop) {
            refreshActivity = true;
        } else if (y < activityFollowingFragmentTop) {
            if (y <= refreshPoint && refreshActivity) {
                // if user scroll past a threshold level of the refresh bar, get newer activity
                refreshActivityFollowing();
            }
        }
    }

    // loads a chunk of activityFollowing on the activity view
    private void loadActivityFollowing() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int i;
        View activityView;
        ArrayList<ActivityFollowing> activityFeed;

        if (!Parameters.dummyData) {
            // TODO: update method
            //Pass the date as strings min/max
            activityFeed = NetParams.NETWORK. getActivityFeedFollowing(null,null);
           // activityFeed = new ArrayList<>();


            int aFsize = activityFeed.size();
            if (activityFeed.size() > 0){
                //Posts earlier than last
                int size = activityFeed.get(aFsize - 1).getPostIcons().size();
                maxTimeSince = activityFeed.get(aFsize - 1).getPostIcons().get(size - 1).
                        getTimeSince().getTimeSince();
            }
        } else {
            activityFeed = new ArrayList<>();
            for (i = 0; i < Parameters.activityFollowingToLoad; i++) {
                activityFeed.add(new ActivityFollowing());
            }
        }
        for (ActivityFollowing activity : activityFeed) {
            activity.buildActivityView(inflater);
            activityView = activity.getActivityView();

            // if activity is from dummyData
            if (Parameters.dummyData) {
                TextView timeSinceText = (TextView)
                        activityView.findViewById(R.id.activity_following_time_since);
                timeSinceText.setText(Integer.toString(activityBottomCount) + "s");
            }
            if(activityView != null){
                activityFollowingView.addView(activityView, activityFollowingIndex + 1);
            }
            activityBottomCount++;
            activityFollowingIndex++;
        }
        allActivityFollowing.addAll(activityFeed);
        updateTimeSince();
    }

    // get new activity of following
    private void refreshActivityFollowing() {

        // flag set so that posts are only refreshed once until view moves back
        // to the activity view
        refreshActivity = false;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int i;
        View activityView;
        ArrayList<ActivityFollowing> activityFeed;

        if (!Parameters.dummyData) {
            // TODO: update method
//            activityFeed = NetParams.NETWORK.getUserFeed(minTimeSince, null);
            activityFeed = new ArrayList<>();

            int aFSize = activityFeed.size();
            if (activityFeed.size() > 0){
                //Activity after first
                minTimeSince = activityFeed.get(0).getPostIcons().get(0).getTimeSince().
                        getTimeSince();
            }
        } else {
            activityFeed = new ArrayList<>();
            for (i = 0; i < Parameters.activityFollowingToLoad; i++) {
                activityFeed.add(new ActivityFollowing());
            }
        }
        i = 0;
        int size = activityFeed.size();
        activityTopCount += size;
        for (ActivityFollowing activity : activityFeed) {
            activity.buildActivityView(inflater);
            activityView = activity.getActivityView();

            // if activity is from dummyData
            if (Parameters.dummyData) {
                TextView timeSinceText = (TextView)
                        activityView.findViewById(R.id.activity_following_time_since);
                timeSinceText.setText(Integer.toString(-activityTopCount + i) + "s");
            }
            if(activityView != null){
                activityFollowingView.addView(activityView, i + 1);
            }
            i++;
        }
        activityFollowingIndex += size;
        allActivityFollowing.addAll(0, activityFeed);
        updateTimeSince();
    }

    // update time since posted of all activity
    private void updateTimeSince(){
        for (ActivityFollowing activity : allActivityFollowing){
            TimeSince timeSince = activity.getPostIcons().get(0).getTimeSince();

            // update post time since
            timeSince.formatTime(timeSince.getTimeSince());

            // display updated time since
            if(activity.getActivityView() != null) {
                ((TextView) activity.getActivityView().
                        findViewById(R.id.activity_following_time_since)).
                        setText(timeSince.getTimeSinceDisplay());
            }
        }
    }

    // set title of activity fragment
    private void setTitle() {
        Parameters.setTitle(Parameters.NavigationBarActivity,
                Parameters.activityTitle, Parameters.subTitleSize);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(activityFollowingFragment != null){
            setTitle();
            Parameters.NavigationBarActivity.activityFeedBar(true);
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
