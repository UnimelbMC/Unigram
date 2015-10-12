package co.example.junjen.mobileinstagram;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import co.example.junjen.mobileinstagram.customLayouts.ToggleButton;
import co.example.junjen.mobileinstagram.elements.Image;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.elements.StringFactory;
import co.example.junjen.mobileinstagram.elements.User;
import co.example.junjen.mobileinstagram.network.NetParams;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View searchFragment;
    private EditText searchInput;
    private ViewGroup searchScrollView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display back button
        Parameters.NavigationBarActivity.showBackButton();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // remove loading animation
        Parameters.NavigationBarActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        if(searchFragment == null){

            // change action bar title
            setTitle();

            searchFragment = inflater.inflate(R.layout.fragment_search, container, false);
            searchScrollView = (ViewGroup) searchFragment.findViewById(R.id.search_scroll_view);

            // set onClickListener on search button
            Button searchButton = (Button) searchFragment.findViewById(R.id.search_user_button);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchUser();
                }
            });

            // input field to search for a user
            searchInput = (EditText) searchFragment.findViewById(R.id.search_user_text);
        }
        return searchFragment;
    }

    // search for user
    private void searchUser(){

        LayoutInflater inflater = LayoutInflater.from(getContext());

        // get user to search from input field
        String userToSearch = searchInput.getText().toString();

        int i;
        ArrayList<User> users = new ArrayList<>();
        User user;

        if(!Parameters.dummyData){
            users = NetParams.NETWORK.searchUser(userToSearch, Parameters.searchedUsersToReturn);
        } else {
            for(i = 0; i < Parameters.default_usersToSearch; i++){
                users.add(new User(i + userToSearch));
            }
        }

        int size = users.size();
        searchScrollView.removeAllViews();  // clear previous search results

        // load suggested users
        for (i = 0; i < size; i++) {

            // build user view components
            View userElement = inflater.inflate(R.layout.user_element, searchScrollView, false);
            user = users.get(i);
            User.buildUserElement(user, userElement);

            searchScrollView.addView(userElement);
        }
    }

    // set title of search fragment
    private void setTitle() {
        Parameters.setTitle(Parameters.NavigationBarActivity,
                Parameters.discoverTitle, Parameters.subTitleSize);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
