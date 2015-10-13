package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Post;
import co.example.junjen.mobileinstagram.network.NetParams;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String postId_key = "postId";
    private static final String like_key = "like";

    // TODO: Rename and change types of parameters
    private String postId;
    private boolean like;

    private ViewGroup postFragment;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param postId Parameter 1.
     * @param like Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String postId, boolean like) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(postId_key, postId);
        args.putBoolean(like_key, like);
        fragment.setArguments(args);
        return fragment;
    }

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(postId_key);
            like = getArguments().getBoolean(like_key);

            // display back button
            Parameters.NavigationBarActivity.showBackButton();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // remove loading animation
        Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        if(postFragment == null) {

            Log.w("test", "PostFragment onCreateView: " + postId);

            Post post;
            if (!postId.equals(Parameters.default_postId)) {
                post = NetParams.NETWORK.getPostById(postId);
                if(like){
                    post.setLiked(Parameters.like);
                }
            } else {
                post = new Post();
            }

            postFragment = (ViewGroup) inflater.inflate(R.layout.fragment_post, container, false);
            WeakReference<LayoutInflater> weakInflater = new WeakReference<>(inflater);
            View postView = post.getPostView(weakInflater.get());
            if (postView != null) {

                postFragment.addView(postView);
            }
        }
        // Inflate the layout for this fragment
        return postFragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    // sets the action bar title when in a post fragment
    private void setTitle(){
        Parameters.setTitle(Parameters.NavigationBarActivity, Parameters.postTitle,
                Parameters.subTitleSize);
        Parameters.NavigationBarActivity.activityFeedBar(false);
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
