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

        // initialise ProfileFragment if not created yet
        if(profileFragment == null){

            if(userId.equals(Parameters.default_userId)){
                profile = new Profile(userId);
            } else if (!userId.equals(Parameters.loginUserId)){
                profile = NetParams.NETWORK.getUserProfileFeed(userId);
            } else {
                profile = Parameters.loginProfile;
            }

            setTitle();

            profileFragment = profile.getProfileView(inflater);
            profileFragment.setScrollViewListener(this);

            // add layout listener to add content if default screen is not filled
            ViewTreeObserver vto = profileFragment.getViewTreeObserver();
            final int screenHeight = Parameters.NavigationViewHeight;
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    profileFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    int[] location = new int[2];
                    profileFragment.getLocationOnScreen(location);
                    int height = location[1] + profileFragment.getChildAt(0).getHeight();

                    Log.w("test", "profile: " + Integer.toString(height) + " (" + Integer.toString(screenHeight) + ")");

                    if (height <= screenHeight) {
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        profile.getPostIcons(inflater);
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
            LayoutInflater inflater = LayoutInflater.from(getContext());
            profile.getPostIcons(inflater);
            loadPosts = true;
        }
    }

    // sets the action bar title when in a profile fragment
    public void setTitle(){
        Parameters.setTitle(Parameters.NavigationBarActivity,
                profile.getUsername().getUsername().toUpperCase(), Parameters.subTitleSize);
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
