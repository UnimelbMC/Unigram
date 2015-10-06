package co.example.junjen.mobileinstagram;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.util.ArrayList;

import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.network.Network;
import co.example.junjen.mobileinstagram.network.Params;

public class NavigationBar extends AppCompatActivity {

    private FragmentTransaction ft;
    private ArrayList<Fragment> userFeedHistory = new ArrayList<>();
    private ArrayList<Fragment> discoverHistory = new ArrayList<>();
    private CameraFragment cameraFragment;
    private ArrayList<Fragment> activityFeedHistory = new ArrayList<>();
    private ArrayList<Fragment> profileHistory = new ArrayList<>();
    private String token;
    private RadioGroup navBar;
    private int prevNavButtonId;
    private int mainView = R.id.view1;
    private ActionBar actionBar;

    // Button IDs
    private final int userFeedButtonId = R.id.userfeed_button;
    private final int discoverButtonId = R.id.discover_button;
    private final int activityFeedButtonId = R.id.activityfeed_button;
    private final int profileButtonId = R.id.profile_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set custom action bar
        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar);

            // bind backButton click to goBack() method
            ImageButton backButton = (ImageButton)
                    actionBar.getCustomView().findViewById(R.id.back_button);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }

        setContentView(R.layout.activity_navigation_bar);

        // get username and password
        if (savedInstanceState == null) {
            createFragments();

            // set default fragment to User Feed
            RadioButton userFeedButton = (RadioButton) findViewById(userFeedButtonId);
            userFeedButton.setChecked(true);
            getSupportFragmentManager().beginTransaction().
                    add(R.id.view1, userFeedHistory.get(0)).commit();
            prevNavButtonId = userFeedButtonId;
        } else {
            createFragments();
        }

        // set listener for navigation bar
        navBar = (RadioGroup) findViewById(R.id.nav_bar);
        navBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ft = getSupportFragmentManager().beginTransaction();
                switch (checkedId) {
                    case userFeedButtonId:
                        ft.replace(mainView, userFeedHistory.get(userFeedHistory.size() - 1));
                        prevNavButtonId = checkedId;
                        break;
                    case discoverButtonId:
                        ft.replace(mainView, discoverHistory.get(discoverHistory.size() - 1));
                        prevNavButtonId = checkedId;
                        break;
                    case R.id.camera_button:
                        ft.replace(mainView, cameraFragment);
                        break;
                    case activityFeedButtonId:
                        ft.replace(mainView,
                                activityFeedHistory.get(activityFeedHistory.size() - 1));
                        prevNavButtonId = checkedId;
                        break;
                    case profileButtonId:
                        ft.replace(mainView, profileHistory.get(profileHistory.size() - 1));
                        prevNavButtonId = checkedId;
                        break;
                }
                ft.commit();
            }
        });
    }

    // displays a fragment and adds it to a history
    public void showFragment(Fragment fragment){
        replaceView(fragment);

        switch (prevNavButtonId) {
            case userFeedButtonId:
                userFeedHistory.add(fragment);
                break;
            case discoverButtonId:
                discoverHistory.add(fragment);
                break;
            case activityFeedButtonId:
                activityFeedHistory.add(fragment);
                break;
            case profileButtonId:
                profileHistory.add(fragment);
                break;
        }
    }

    // replaces the main view with a fragment
    public void replaceView(Fragment fragment){
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(mainView, fragment);
        ft.commit();
    }

    // go back to the previous fragment using the back button
    public void goBack(){
        int size = 0;

        switch (prevNavButtonId) {
            case userFeedButtonId:
                replaceView(userFeedHistory.get(userFeedHistory.size()-2));
                userFeedHistory.remove(userFeedHistory.size()-1);
                size = userFeedHistory.size();
                break;
            case discoverButtonId:
                replaceView(discoverHistory.get(discoverHistory.size()-2));
                discoverHistory.remove(discoverHistory.size() - 1);
                size = discoverHistory.size();
                break;
            case activityFeedButtonId:
                replaceView(activityFeedHistory.get(activityFeedHistory.size()-2));
                activityFeedHistory.remove(activityFeedHistory.size() - 1);
                size = activityFeedHistory.size();
                break;
            case profileButtonId:
                replaceView(profileHistory.get(profileHistory.size()-2));
                profileHistory.remove(profileHistory.size() - 1);
                size = profileHistory.size();
                break;
        }
        // hide back button if fragment history is only the current fragment
        if (size == 1){
            hideBackButton();
        }
    }

    // TODO: update argument to receive token
    private void createFragments(){
        userFeedHistory.add(new UserFeedFragment());
        discoverHistory.add(new DiscoverFragment());
        cameraFragment = new CameraFragment();
        activityFeedHistory.add(new ActivityFeedFragment());
        profileHistory.add(ProfileFragment.newInstance(new Profile(), false));
    }

    // return to previous fragment by programmatically checking the radio button
    public void checkPreviousNavButton(){
        navBar.check(prevNavButtonId);
    }

    // returns the navigation bar object to the camera fragment's back button
    public RadioGroup getNavBar(){
        return navBar;
    }

    // returns main view in navigation screen
    public int getMainView(){
        return mainView;
    }

    // show the back button on the action bar
    public void showBackButton(){
        if (actionBar != null){
            actionBar.getCustomView().findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        }

    }

    // hide the back button on the action bar
    public void hideBackButton(){
        if (actionBar != null){
            actionBar.getCustomView().findViewById(R.id.back_button).setVisibility(View.GONE);
        }
    }

    public void goToMain(){
        Intent intent = new Intent(NavigationBar.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NavigationBar.this.startActivity(intent);
    }

    public void clearToken(){
        File file = new File(Params.ACCESS_TOKEN_FILEPATH);
        if(file.exists()) {
            file.delete();
            Log.w("test", "token deleted");
        }
        Params.ACCESS_TOKEN = null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.w("test", "nav bar saved");

        // Save the user's current game state
        savedInstanceState.putString("token", token);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.w("test","nav bar restored");

        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // clear access token on logout
        if (id == R.id.action_logout) {

            clearToken();

//            Intent internetIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://instagram.com/accounts/logout"));
//            //internetIntent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
//            internetIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(internetIntent);

            WebView myWebView = new WebView(this);
            myWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }
            });
            myWebView.loadUrl("https://instagram.com/accounts/logout");

            goToMain();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
