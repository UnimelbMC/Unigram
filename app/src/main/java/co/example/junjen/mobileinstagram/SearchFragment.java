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
 *
 * Created by junjen at 12/10/2015.
 *
 * Creates Search fragment on search bar click from Discover fragment.
 *
 */
public class SearchFragment extends Fragment {

    private View searchFragment;
    private EditText searchInput;
    private ViewGroup searchScrollView;

    private OnFragmentInteractionListener mListener;

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
            user.buildUserElement(userElement);

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
