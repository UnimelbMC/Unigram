package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;

import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ScrollViewListener;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.network.NetParams;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements ScrollViewListener{
    // the fragment initialization parameters
    private static final String userId_key = "userId";
    private static final String backButton_key = "backButton";

    private String userId;
    private boolean backButton;

    private ExpandableScrollView profileFragment;
    private Profile profile;

    // flag to check if posts are being loaded before loading new ones
    private boolean loadPosts = true;

    // counter for new posts to be placed in the right order when loaded
    private int postCount = 0;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId Parameter 1.
     * @param backButton Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String userId, boolean backButton) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(userId_key, userId);
        args.putBoolean(backButton_key, backButton);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(userId_key);
            backButton = getArguments().getBoolean(backButton_key);

            // display back button if profile fragment created from userId link
            if (backButton){
                ((NavigationBar) this.getActivity()).showBackButton();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // remove loading animation
        Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // initialise ProfileFragment if not created yet
        if(profileFragment == null){

            Log.w("like", "profileFragment: " + userId);

            if(userId.startsWith(Parameters.default_userId)){
                String[] parts = userId.split(Parameters.default_userId);
                if(parts.length > 0){
                    profile = new Profile(parts[1]);
                } else {
                    profile = new Profile("");
                }
            } else if (!userId.equals(Parameters.loginUserId)){
                Log.w("like", "profile creation through NETWORK");
                profile = NetParams.NETWORK.getUserProfileInfo(userId);

                if (profile == null){
                    // this might mean the profile is private, hence search for user info only
                    profile = NetParams.NETWORK.searchUserProfileById(userId);

                    if (profile == null) {
                        // at this point it means current user has restricted access to this profile
                        profileFragment = (ExpandableScrollView)
                                inflater.inflate(R.layout.restricted_profile, container, false);
                        return profileFragment;
                    }
                }
            } else if (userId.equals(Parameters.loginUserId)){
                profile = Parameters.loginProfile;
            }

            setTitle();

            Log.w("like", "profile: " + profile.getUsername().getUserId());

            profileFragment = profile.getProfileView(inflater);
            profileFragment.setScrollViewListener(this);

            // add layout listener to add post icons if default screen is not filled
            ViewTreeObserver vto = profileFragment.getViewTreeObserver();
            final int screenHeight = Parameters.NavigationViewHeight;
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    profileFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    int[] location = new int[2];
                    profileFragment.getLocationOnScreen(location);
                    int height = location[1] + profileFragment.getChildAt(0).getHeight();

                    if (height <= screenHeight) {
                        WeakReference<LayoutInflater> weakInflater =
                                new WeakReference<>(LayoutInflater.from(getContext()));
                        profile.getPostIcons(weakInflater.get());
                    }
                }
            });
        }
        return profileFragment;
    }

    // loads another chunk of posts when at the bottom of the profile ScrollView
    @Override
    public void onScrollEnded(ExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {

        // load new posts if no posts are currently being loaded
        if(loadPosts){
            loadPosts = false;
            WeakReference<LayoutInflater> weakInflater =
                    new WeakReference<>(LayoutInflater.from(getContext()));
            profile.getPostIcons(weakInflater.get());
            loadPosts = true;
        }
    }

    // sets the action bar title when in a profile fragment
    public void setTitle(){
        Parameters.setTitle(Parameters.NavigationBarActivity,
                Parameters.titleBuffer+profile.getUsername().getUsername().toUpperCase(),
                Parameters.subTitleSize);
        Parameters.NavigationBarActivity.activityFeedBar(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(profile != null){
            setTitle();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        //fragment specific menu creation
        setTitle();
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