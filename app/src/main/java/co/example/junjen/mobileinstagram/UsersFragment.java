package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.customLayouts.ExpandableScrollView;
import co.example.junjen.mobileinstagram.customLayouts.ScrollViewListener;
import co.example.junjen.mobileinstagram.elements.User;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.StringFactory;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment implements ScrollViewListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String usernames_key = "usernames";
    private static final String title_key = "title";

    // TODO: Rename and change types of parameters
    private ArrayList<User> usernames;
    private String title;

    private ExpandableScrollView userFragment;
    private ViewGroup usersView;
    private int userCount = 0;
    private int usersSize = 0;

    // flag to check if usernames are being loaded before loading new ones
    private boolean loadPosts = true;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @param title Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(ArrayList<User> user, String title) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putSerializable(usernames_key, user);
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
            usernames = (ArrayList<User>) getArguments().getSerializable(usernames_key);
            title = getArguments().getString(title_key);

            // display back button
            Parameters.NavigationBarActivity.showBackButton();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // remove loading animation
        Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        if (usernames != null){
            userFragment = (ExpandableScrollView) inflater.inflate(R.layout.fragment_expandable_scroll_view, container, false);
            userFragment.setScrollViewListener(this);
            usersView = (ViewGroup) userFragment.findViewById(R.id.expandable_scroll_view);
            usersSize = usernames.size();

            loadLikes();

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
                        loadLikes();
                    }
                }
            });
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
            loadLikes();
            loadPosts = true;
        }
    }

    // loads a number of comments based on a threshold
    private void loadLikes(){

        LayoutInflater inflater = LayoutInflater.from(getContext());

        int i;
        int loadLikeThreshold = Parameters.loadLikeThreshold;
        ArrayList<CharSequence> stringComponents = new ArrayList<>();

        // load chunk of comments based on a threshold
        for (i = 0; i < loadLikeThreshold; i++){

            if (userCount >= usersSize || userCount >= Parameters.maxLikes) {
                break;
            }

            // load view components
            View likeElement = inflater.inflate(R.layout.user_element, usersView, false);
            ImageView userImage = (ImageView) likeElement.findViewById(R.id.user_user_image);
            TextView username = (TextView) likeElement.findViewById(R.id.user_username);
            TextView profName = (TextView) likeElement.findViewById(R.id.user_prof_name);

            User like = usernames.get(userCount);

            if (like.getUsername().getUsername().startsWith(Parameters.default_username)){

                username.setText("");   // remove default text
                stringComponents.add(like.getUsername().getUsernameLink());
                StringFactory.stringBuilder(username, stringComponents);
                stringComponents.clear();

                profName.setText(like.getProfName());
            } else {


                // TODO: get Data Object


            }
            usersView.addView(likeElement, userCount);
            userCount++;
        }
    }

    // sets the action bar title when in a comment fragment
    public void setTitle(){
        Parameters.setTitle(Parameters.NavigationBarActivity, title, Parameters.subTitleSize);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setTitle();
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
