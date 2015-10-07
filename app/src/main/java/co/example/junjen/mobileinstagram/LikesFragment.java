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
import co.example.junjen.mobileinstagram.elements.Like;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.elements.StringFactory;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LikesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikesFragment extends Fragment implements ScrollViewListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String likes_key = "likes";

    // TODO: Rename and change types of parameters
    private ArrayList<Like> likes;

    private ExpandableScrollView likesFragment;
    private ViewGroup likesView;
    private int likeCount = 0;
    private int likesSize = 0;

    // flag to check if likes are being loaded before loading new ones
    private boolean loadPosts = true;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param likes Parameter 1.
     * @return A new instance of fragment LikesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LikesFragment newInstance(ArrayList<Like> likes) {
        LikesFragment fragment = new LikesFragment();
        Bundle args = new Bundle();
        args.putSerializable(likes_key, likes);
        fragment.setArguments(args);
        return fragment;
    }

    public LikesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            likes = (ArrayList<Like>) getArguments().getSerializable(likes_key);
            // display back button
            ((NavigationBar) this.getActivity()).showBackButton();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // change action bar title
        setTitle();

        if (likes != null){
            likesFragment = (ExpandableScrollView) inflater.inflate(R.layout.fragment_expandable_scroll_view, container, false);
            likesFragment.setScrollViewListener(this);
            likesView = (ViewGroup) likesFragment.findViewById(R.id.expandable_scroll_view);
            likesSize = likes.size();

            loadLikes();

            // add layout listener to add content if default screen is not filled
            ViewTreeObserver vto = likesFragment.getViewTreeObserver();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            final int screenHeight = displaymetrics.heightPixels;

            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    likesFragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = likesFragment.getHeight();

                    if (height < screenHeight) {
                        loadLikes();
                    }
                }
            });
        }
        // Inflate the layout for this fragment
        return likesFragment;
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

            if (likeCount >= likesSize || likeCount >= Parameters.maxLikes) {
                break;
            }

            // load view components
            View likeElement = inflater.inflate(R.layout.likes_element, likesView, false);
            ImageView userImage = (ImageView) likeElement.findViewById(R.id.like_user_image);
            TextView username = (TextView) likeElement.findViewById(R.id.like_username);
            TextView profName = (TextView) likeElement.findViewById(R.id.like_prof_name);

            Like like = likes.get(likeCount);

            if (like.getUsername().getUsername().startsWith(Parameters.default_username)){

                username.setText("");   // remove default text
                stringComponents.add(like.getUsername().getUsernameLink());
                StringFactory.stringBuilder(username, stringComponents);
                stringComponents.clear();

                profName.setText(like.getProfName());
            } else {


                // TODO: get Data Object


            }
            likesView.addView(likeElement, likeCount);
            likeCount++;
        }
    }

    // sets the action bar title when in a comment fragment
    public void setTitle(){
        View actionBar = ((AppCompatActivity)
                this.getActivity()).getSupportActionBar().getCustomView();
        if (actionBar != null) {
            TextView title = (TextView) actionBar.findViewById(R.id.action_bar_title);
            title.setText(Parameters.likesTitle);
            title.setTextSize(Parameters.subTitleSize);
        }
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
