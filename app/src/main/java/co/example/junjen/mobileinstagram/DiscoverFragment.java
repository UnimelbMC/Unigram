package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import co.example.junjen.mobileinstagram.customLayouts.ToggleButton;
import co.example.junjen.mobileinstagram.customLayouts.TopBottomExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.TopScrollViewListener;
import co.example.junjen.mobileinstagram.elements.Image;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.elements.StringFactory;
import co.example.junjen.mobileinstagram.elements.User;
import co.example.junjen.mobileinstagram.network.NetParams;
import co.example.junjen.mobileinstagram.suggestion.Suggestion;
import co.example.junjen.mobileinstagram.suggestion.SuggestionParams;

/**
 *
 * Created by junjen at 9/10/2015.
 *
 * Creates the Discover Fragment in the application.
 *
 */

public class DiscoverFragment extends Fragment implements TopScrollViewListener {

    private Suggestion suggestion;
    private RelativeLayout discoverFragment;
    private TopBottomExpandableScrollView discoverScrollFullView;
    private ViewGroup discoverScrollView;
    private ViewGroup discoverView;
    private int discoverFragmentTop = 0;
    private View refresh;
    private int refreshPoint;
    private boolean refreshPost = false;
    private boolean initialised = false;
    private ArrayList<User> suggestedUsers = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // remove loading animation
        Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        if (discoverFragment == null) {

            setTitle();

            discoverFragment = (RelativeLayout)
                    inflater.inflate(R.layout.fragment_discover, container, false);
            discoverScrollFullView = (TopBottomExpandableScrollView)
                    discoverFragment.findViewById(R.id.discover_full_view);
            discoverScrollView = (ViewGroup)
                    discoverFragment.findViewById(R.id.discover_scroll_view);
            discoverView = (ViewGroup) discoverFragment.findViewById(R.id.discover_view);

            // set scroll listeners
            discoverScrollFullView.setTopScrollViewListener(this);

            // add refresh bar at the top of discover
            View layout = inflater.inflate(R.layout.pull_down_refresh, null, false);
            refresh = layout.findViewById(R.id.refreshPanel);

            // initialise scroll view position using a global layout listener
            initialisePosition();

            ((ViewGroup) refresh.getParent()).removeView(refresh);
            discoverView.addView(refresh, 0);

            // move back to discover view if user scrolls into refresh bar
            // (after user's finger lifts off the screen)
            setReturnToTopListener();

            // set listener to search bar imageView
            ImageView searchBar = (ImageView)
                    discoverFragment.findViewById(R.id.discover_search_bar);
            searchBar.setOnClickListener(this.searchBarOnClickListener());

            // set listener to return to top of scroll view if in refresh panel for too long
            setScrollHeightListener();

            // loads suggested users
            loadSuggestedUsers();
        }
        return discoverFragment;
    }

    // loads a number of users based on a threshold
    private void loadSuggestedUsers() {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        int i;
        suggestedUsers = new ArrayList<>();
        User user;

        if(!Parameters.dummyData){
            // create object to retrieve suggestions
            suggestion = new Suggestion("self");
            Map<String, String> userSuggestions = suggestion.getClassifiedPossibleUserSuggestion();
            for (Map.Entry<String, String> userId : userSuggestions.entrySet()) {
                if (userId.getValue().equals(SuggestionParams.suggested_key)) {
                    user = NetParams.NETWORK.searchUserInfoById(userId.getKey());
                    if (user != null) {
                        suggestedUsers.add(user);
                    }
                }
            }
        } else {
            for (i = 0; i < Parameters.default_suggestions; i++){
                user = new User(Integer.toString(i));
                suggestedUsers.add(user);
            }
        }

        int size = suggestedUsers.size();

        // load suggested users
        for (i = 0; i < size; i++) {

            // build user view components
            View userElement = inflater.inflate(R.layout.user_element, discoverScrollView, false);
            user = suggestedUsers.get(i);
            user.buildUserElement(userElement);

            discoverScrollView.addView(userElement);
        }
    }

    // refresh and update suggested users
    public void refreshSuggestedUsers(){

        // flag set so that posts are only refreshed once until view moves back
        // to the user feed view
        refreshPost = false;

        discoverScrollView.removeAllViews();
        loadSuggestedUsers();
    }

    // listener to keep track of post height
    private void setScrollHeightListener(){
        ViewTreeObserver vto = discoverScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // if view in refresh panel, return after a delay
                int scrollY = discoverFragment.getScrollY();
                if (scrollY < discoverFragmentTop) {
                    // if new user feed loading, delay before returning to top of scroll view
                    returnToTop(discoverFragmentTop, Parameters.refreshReturnDelay);
                }
            }
        });
    }

    // initialise scroll view position using a global layout listener
    private void initialisePosition() {
        ViewTreeObserver vto = refresh.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refresh.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // get starting position of discover scroll view
                discoverFragmentTop = refresh.getBottom();
                int start = refresh.getTop();

                refreshPoint = Math.round((discoverFragmentTop - start)
                        / Parameters.refreshThreshold + start);

                // set scroll to initial position if discover fragment is being initialised
                if (!initialised) {
                    returnToTop(discoverFragmentTop, Parameters.refreshReturnDelay);
                    initialised = true;
                    discoverScrollFullView.setTopLevel(discoverFragmentTop);
                }
            }
        });
    }

    // move back to discover view if user scrolls into refresh bar
    // (after user's finger lifts off the screen)
    private void setReturnToTopListener() {
        discoverScrollFullView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int scrollY = v.getScrollY();

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (scrollY <= refreshPoint) {
                        // if new user feed loading, delay before returning to top of scroll view
                        returnToTop(0, 0);
                        returnToTop(discoverFragmentTop, Parameters.refreshReturnDelay);
                    } else if (scrollY > refreshPoint && scrollY < discoverFragmentTop) {
                        returnToTop(discoverFragmentTop, 0);
                    }
                }
                return false;
            }
        });
    }

    // return scroll level to top of user feed view
    private void returnToTop(final int scrollY, int delay) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                discoverScrollFullView.smoothScrollTo(0, scrollY);
            }
        }, delay);
    }

    // OnClickListener for post icon clicks
    public View.OnClickListener searchBarOnClickListener(){

        View.OnClickListener searchBarOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to search fragment
                Parameters.NavigationBarActivity.
                        showFragment(new SearchFragment());
            }
        };
        return searchBarOnClickListener;
    }

    // add scroll listener to update posts if scroll past top of user feed
    @Override
    public void onScrollTop(TopBottomExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y >= discoverFragmentTop) {
            refreshPost = true;
        } else if (y < discoverFragmentTop) {
            if (y <= refreshPoint && refreshPost) {
                // if user scroll past a threshold level of the refresh bar, get newer posts
                refreshSuggestedUsers();
            }
        }
    }

    // set title of discover fragment
    private void setTitle() {
        Parameters.setTitle(Parameters.NavigationBarActivity,
                Parameters.discoverTitle, Parameters.subTitleSize);
        Parameters.NavigationBarActivity.activityFeedBar(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (discoverFragment != null) {
            setTitle();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w("test", "discover resume");

        // update follow buttons on view resume
        if(suggestedUsers != null){
            for(User user : suggestedUsers){
                ((RadioGroup) user.getFollowButton().getParent()).clearCheck();
                user.updateFollowButton();
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
}