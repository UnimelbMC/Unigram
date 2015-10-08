package co.example.junjen.mobileinstagram;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.util.ArrayList;

import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.network.NetParams;

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
    private int navigationViewId = R.id.view1;
    private ActionBar actionBar;

    // Button IDs
    private final int userFeedButtonId = R.id.userfeed_button;
    private final int discoverButtonId = R.id.discover_button;
    private final int cameraButtonId = R.id.camera_button;
    private final int activityFeedButtonId = R.id.activityfeed_button;
    private final int profileButtonId = R.id.profile_button;

    private int logoutBrowserCount = 0;
    private boolean cameraOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.mainActivity.finish();
        Parameters.NavigationBarActivity = this;

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
                        ft.replace(navigationViewId, userFeedHistory.get(userFeedHistory.size() - 1));
                        prevNavButtonId = checkedId;
                        break;
                    case discoverButtonId:
                        ft.replace(navigationViewId, discoverHistory.get(discoverHistory.size() - 1));
                        prevNavButtonId = checkedId;
                        break;
                    case cameraButtonId:
                        ft.replace(navigationViewId, cameraFragment);
                        cameraOn = true;
                        break;
                    case activityFeedButtonId:
                        ft.replace(navigationViewId,
                                activityFeedHistory.get(activityFeedHistory.size() - 1));
                        prevNavButtonId = checkedId;
                        break;
                    case profileButtonId:
                        ft.replace(navigationViewId, profileHistory.get(profileHistory.size() - 1));
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
        ft.replace(navigationViewId, fragment);
        ft.commit();
    }

    // go back to the previous fragment using the back button
    public void goBack(){

        switch (prevNavButtonId) {
            case userFeedButtonId:
                updateHistory(userFeedHistory);
                break;
            case discoverButtonId:
                updateHistory(discoverHistory);
                break;
            case activityFeedButtonId:
                updateHistory(activityFeedHistory);
                break;
            case profileButtonId:
                updateHistory(profileHistory);
                break;
        }

    }

    // update history when back button is clicked
    public void updateHistory(ArrayList<Fragment> history){
        int size = 0;

        if(!cameraOn) {
            replaceView(history.get(history.size() - 2));
            history.remove(history.size() - 1);
        } else {
            cameraOn = false;
            RadioButton rb = (RadioButton) findViewById(prevNavButtonId);
            rb.setChecked(true);
            replaceView(history.get(history.size() - 1));
        }
        size = history.size();

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
        profileHistory.add(ProfileFragment.newInstance(Parameters.selfLogin_key, false));
    }

    // returns the navigation bar object to the camera fragment's back button
    public RadioGroup getNavBar(){
        return navBar;
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

    // destroy the access token
    public void clearToken(){
        File file = new File(NetParams.ACCESS_TOKEN_FILEPATH);
        if (file.exists()) {
            file.delete();
            Log.w("test", "token deleted");
        }
        NetParams.ACCESS_TOKEN = null;
    }

    // go back to login screen
    private void goToMain(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);

        finish();
    }

    // android back button goes back to previous fragment when in camera fragment
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && cameraOn) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
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

            // logout of instagram account
            WebView myWebView = new WebView(getApplicationContext());
            myWebView.clearFormData();
            setContentView(myWebView);
            myWebView.setWebViewClient(new LogoutWebViewClient());
            myWebView.loadUrl("https://instagram.com/accounts/logout");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // WebView client for logging out
    private class LogoutWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.w("test", url);

            if (url.startsWith(NetParams.LOGOUT_URL_HEADER)) {
                logoutBrowserCount++;

                if (logoutBrowserCount >= Parameters.logoutBrowserCountMax){
                    goToMain();
                    view.destroy();
                }
            }
        }
    }
}
