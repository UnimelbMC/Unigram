package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RadioGroup;
import android.util.Log;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ScrollViewListener;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.elements.User;
import co.example.junjen.mobileinstagram.elements.Parameters;


/**
 *
 * Created by junjen at 7/10/2015.
 *
 * Creates fragments that lists Users (Likers, Following, Followers, Suggested Users in Discover)
 *
 */
public class UsersFragment extends Fragment implements ScrollViewListener{

    private static final String users_key = "users";
    private static final String title_key = "title";

    private ArrayList<User> users;
    private String title;

    private ExpandableScrollView userFragment;
    private ViewGroup usersView;
    private int userCount = 0;
    private int usersSize = 0;

    // flag to check if users are being loaded before loading new ones
    private boolean loadPosts = true;

    private OnFragmentInteractionListener mListener;

    /**
     * @param user Parameter 1.
     * @param title Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    public static UsersFragment newInstance(ArrayList<User> user, String title) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putSerializable(users_key, user);
        args.putString(title_key, title);
        fragment.setArguments(args);
        return fragment;
    }

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            users = (ArrayList<User>) getArguments().getSerializable(users_key);
            title = getArguments().getString(title_key);

            // display back button
            Parameters.NavigationBarActivity.showBackButton();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.w("test", "user create");

        // remove loading animation
        Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        if(userFragment == null) {

            if (users != null) {

                setTitle();

                userFragment = (ExpandableScrollView) inflater.inflate(
                        R.layout.fragment_expandable_scroll_view, container, false);
                userFragment.setScrollViewListener(this);
                usersView = (ViewGroup) userFragment.findViewById(R.id.expandable_scroll_view);
                usersSize = users.size();

                loadUser();

                // add layout listener to add content if default screen is not filled
                ViewTreeObserver vto = userFragment.getViewTreeObserver();
                DisplayMetrics displaymetrics = new DisplayMetrics();
                this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                final int screenHeight = displaymetrics.heightPixels;

                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        userFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        int height = userFragment.getHeight();

                        if (height < screenHeight) {
                            loadUser();
                        }
                    }
                });
            }
        }
        // Inflate the layout for this fragment
        return userFragment;
    }

    // loads another chunk of posts when at the bottom of the user feed ScrollView
    @Override
    public void onScrollEnded(ExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {

        // load new posts if no posts are currently being loaded
        if(loadPosts){
            loadPosts = false;
            loadUser();
            loadPosts = true;
        }
    }

    // loads a number of users based on a threshold
    private void loadUser(){

        LayoutInflater inflater = LayoutInflater.from(getContext());

        int i;
        int loadUserThreshold = Parameters.loadUserThreshold;
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        // load chunk of users based on a threshold
        for (i = 0; i < loadUserThreshold; i++){

            if (userCount >= usersSize || userCount >= Parameters.maxUsers) {
                break;
            }

            // build user view components
            View userElement = inflater.inflate(R.layout.user_element, usersView, false);
            User user = users.get(userCount);
            user.buildUserElement(userElement);

            // check follow button if in following list
            if(title.equals(Parameters.followingTitle)) {
                Profile.checkFollowButton(user.getFollowButton(), true);
            }

            usersView.addView(userElement, userCount);
            userCount++;
        }
    }

    // sets the action bar title when in a comment fragment
    public void setTitle(){
        Parameters.setTitle(Parameters.NavigationBarActivity, title, Parameters.subTitleSize);
        Parameters.NavigationBarActivity.activityFeedBar(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.w("test", "user attach");

        setTitle();

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w("test", "user resume");

        // update follow buttons on view resume
        if(users != null && !title.equals(Parameters.followingTitle)){
            for(User user : users){
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
