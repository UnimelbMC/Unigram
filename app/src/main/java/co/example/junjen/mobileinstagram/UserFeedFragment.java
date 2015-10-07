package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ScrollViewListener;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.TimeSince;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFeedFragment extends Fragment implements ScrollViewListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExpandableScrollView userFeedFragment;

    // keep track of timeSince last post generated to generate new set of posts
    private TimeSince timeSinceLastPost = new TimeSince(Parameters.default_timeSince);

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

        // initialise userFeedFragment if not created yet
        if(userFeedFragment == null){
            userFeedFragment = (ExpandableScrollView)
                    inflater.inflate(R.layout.fragment_user_feed, container, false);

            userFeedFragment.setScrollViewListener(this);

            getUserFeedPosts(inflater, userFeedFragment);

            // add layout listener to add content if default screen is not filled
            ViewTreeObserver vto = userFeedFragment.getViewTreeObserver();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            final int screenHeight = displaymetrics.heightPixels;

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    userFeedFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = userFeedFragment.getHeight();


                    if (height < screenHeight) {
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        getUserFeedPosts(inflater, userFeedFragment);
                    }
                }
            });

        }
        return userFeedFragment;
    }

    // loads another chunk of posts when at the bottom of the user feed ScrollView
    @Override
    public void onScrollEnded(ExpandableScrollView scrollView, int x, int y, int oldx, int oldy) {

        // load new posts if no posts are currently being loaded
        if(loadPosts){
            loadPosts = false;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            getUserFeedPosts(inflater, scrollView);
            loadPosts = true;
        }
    }

    // loads a chunk of posts on the user feed view
    private View getUserFeedPosts(LayoutInflater inflater, View userFeedFragment){

        ViewGroup userFeedView = (ViewGroup) userFeedFragment.findViewById(R.id.userfeed_view);

        int maxPosts = Parameters.postsToLoad;
        int i = 0;
        int index;
        Post post;
        View postView;
        for (i = 0; i < maxPosts; i++){

            // TODO: use getPost(..) method from Data Object class based on timeSinceLastPost
            // if timeSinceLastPost is default, get latest posts
            // else get posts later than timeSinceLastPost
            // add an if (getPost != null) condition
            post = new Post();

            postView = post.getPostView(inflater, userFeedView);

            index = userFeedView.getChildCount();

            //TESTING
            TextView timeSince = (TextView) postView.findViewById(R.id.post_header_time_since);
            if(post.getTimeSince().getTimeSince().equals(Parameters.default_timeSince)){
                timeSince.setText(Integer.toString(index) + "s");
            }

            userFeedView.addView(postView, index);

            this.timeSinceLastPost = post.getTimeSince();
        }
        return userFeedView;
    }

    @Override
    // sets the action bar title when in a user feed fragment
    public void onAttach(Context context) {
        super.onAttach(context);

        View actionBar = ((AppCompatActivity)
                this.getActivity()).getSupportActionBar().getCustomView();
        if (actionBar != null) {
            TextView title = (TextView) actionBar.findViewById(R.id.action_bar_title);
            title.setText(Parameters.mainTitle);
            title.setTextSize(Parameters.mainTitleSize);
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
