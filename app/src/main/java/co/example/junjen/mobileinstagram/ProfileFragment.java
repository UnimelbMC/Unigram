package co.example.junjen.mobileinstagram;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ScrollViewListener;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.elements.TimeSince;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements ScrollViewListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String profile_key = "profile";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Profile profile;
    private ExpandableScrollView profileFragment;

    // keep track of timeSince last post generated to generate new set of posts
    private TimeSince timeSinceLastPost = new TimeSince(Parameters.default_timeSince);

    // flag to check if posts are being loaded before loading new ones
    private boolean loadPosts = true;

    // counter for new posts to be placed in the right order when loaded
    private int postCount = 0;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param profile Parameter 1.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(Profile profile) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(profile_key, profile);
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
            profile = (Profile) getArguments().getSerializable(profile_key);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // initialise ProfileFragment if not created yet
        if(profileFragment == null){

            // TODO: get Profile based on Data Object and pass into fragment constructor
            // currently being created in nav bar activity
//            profile = new Profile();

            setTitle();

            profileFragment = profile.getProfileView(inflater);
            profileFragment.setScrollViewListener(this);

            // add layout listener to add content if default screen is not filled
            ViewTreeObserver vto = profileFragment.getViewTreeObserver();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            final int screenHeight = displaymetrics.heightPixels;

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    profileFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = profileFragment.getHeight();

                    Log.w("test2", Integer.toString(height)+","+Integer.toString(screenHeight));

                    if(height < screenHeight){
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
        View actionBar = ((AppCompatActivity)
                this.getActivity()).getSupportActionBar().getCustomView();
        if (actionBar != null) {
            TextView title = (TextView) actionBar.findViewById(R.id.action_bar_title);
            title.setText(profile.getUsername().getUsername().toUpperCase());
            title.setTextSize(Parameters.subTitleSize);
        }
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
