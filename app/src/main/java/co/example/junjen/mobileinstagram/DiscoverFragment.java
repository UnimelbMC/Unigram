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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiscoverFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment implements TopScrollViewListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Suggestion suggestion;
    private RelativeLayout discoverFragment;
    private TopBottomExpandableScrollView discoverScrollFullView;
    private ViewGroup discoverScrollView;
    private int discoverFragmentTop = 0;
    private View refresh;
    private int refreshPoint;
    private boolean refreshPost = false;
    private boolean initialised = false;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DiscoverFragment() {
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

        if (discoverFragment == null) {
            // remove loading animation
            Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            setTitle();

            discoverFragment = (RelativeLayout)
                    inflater.inflate(R.layout.fragment_discover, container, false);
            discoverScrollFullView = (TopBottomExpandableScrollView)
                    discoverFragment.findViewById(R.id.discover_full_view);
            discoverScrollView = (ViewGroup)
                    discoverFragment.findViewById(R.id.discover_view);

            // set scroll listeners
            discoverScrollFullView.setTopScrollViewListener(this);

            // add refresh bar at the top of discover
            View layout = inflater.inflate(R.layout.pull_down_refresh, null, false);
            refresh = layout.findViewById(R.id.refreshPanel);

            // initialise scroll view position using a global layout listener
            initialisePosition();

            ((ViewGroup) refresh.getParent()).removeView(refresh);
            discoverScrollView.addView(refresh, 0);

            // move back to discover view if user scrolls into refresh bar
            // (after user's finger lifts off the screen)
            setReturnToTopListener();

            // set listener to search bar imageView
            ImageView searchBar = (ImageView)
                    discoverFragment.findViewById(R.id.discover_search_bar);
            searchBar.setOnClickListener(this.searchBarOnClickListener());

            // loads suggested users
            loadSuggestedUsers();
        }
        return discoverFragment;
    }

    // loads a number of users based on a threshold
    private void loadSuggestedUsers() {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        int i;
        ArrayList<CharSequence> stringComponents = new ArrayList<>();
        ArrayList<Profile> suggestedUsers = new ArrayList<>();
        Profile profile;

        if(!Parameters.dummyData){
            // create object to retrieve suggestions
            suggestion = new Suggestion("self");
            Map<String, String> userSuggestions = suggestion.getClassifiedPossibleUserSuggestion();
            for (Map.Entry<String, String> user : userSuggestions.entrySet()) {
                if (user.getValue().equals(SuggestionParams.suggested_key)) {
                    profile = NetParams.NETWORK.searchUserById(user.getKey());
                    if (profile != null) {
                        suggestedUsers.add(profile);
                    }
                }
            }
        } else {
            for (i = 0; i < Parameters.default_suggestions; i++){
                profile = new Profile(Integer.toString(i));
                suggestedUsers.add(profile);
            }
        }

        int size = suggestedUsers.size();

        // load suggested users
        for (i = 0; i < size; i++) {

            // load view components
            View userElement = inflater.inflate(R.layout.user_element, discoverScrollView, false);
            ImageView userImage = (ImageView) userElement.findViewById(R.id.user_user_image);
            TextView username = (TextView) userElement.findViewById(R.id.user_username);
            TextView profName = (TextView) userElement.findViewById(R.id.user_prof_name);
            final ToggleButton followButton = (ToggleButton)
                    userElement.findViewById(R.id.user_follow_button);

            Profile user = suggestedUsers.get(i);

            username.setText("");   // remove default text
            stringComponents.add(user.getUsername().getUsernameLink());
            StringFactory.stringBuilder(username, stringComponents);
            stringComponents.clear();

            Profile.checkIfFollowing(user.getUsername().getUserId(), followButton);

            // set listener to followButton
            followButton.setOnClickListener(new View.OnClickListener() {

                // Handle clicks for like button
                @Override
                public void onClick(View v) {
                    if (followButton.isChecked()) {
                        Profile.updateFollowingCount(true);
                    } else {
                        Profile.updateFollowingCount(false);
                    }
                }
            });

            // set user image
            Image.setImage(userImage, user.getUserImage());

            // set user profile name
            String text = user.getProfName();
            if (text == null || text.equals("")) {
                profName.setVisibility(View.GONE);
            } else {
                profName.setText(text);
            }

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

    // initialise scroll view position using a global layout listener
    private void initialisePosition() {
        ViewTreeObserver vto = refresh.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refresh.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // get starting position of user feed scroll view
                discoverFragmentTop = refresh.getBottom();
                int start = refresh.getTop();

                refreshPoint = Math.round((discoverFragmentTop - start)
                        / Parameters.refreshThreshold + start);

                // set scroll to initial position if user feed is being initialised
                if (!initialised) {
                    returnToTop(discoverFragmentTop, Parameters.refreshReturnDelay);
                    initialised = true;
                    Parameters.discoverFragmentTop = discoverFragmentTop;
                }
            }
        });
    }

    // move back to user feed view if user scrolls into refresh bar
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