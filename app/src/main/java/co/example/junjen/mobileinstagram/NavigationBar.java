package co.example.junjen.mobileinstagram;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import co.example.junjen.mobileinstagram.bluetoothSwipeInRange.DeviceListActivity;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.elements.Profile;
import co.example.junjen.mobileinstagram.elements.User;
import co.example.junjen.mobileinstagram.network.LocationService;
import co.example.junjen.mobileinstagram.network.NetParams;
import co.example.junjen.mobileinstagram.bluetoothSwipeInRange.BluetoothSwipeFragment;

/**
 *
 * Created by junjen at 7/10/2015.
 *
 * Navigation activity for UniGram application. This is where the user navigates the application.
 *
 */

public class NavigationBar extends AppCompatActivity {

    private FragmentTransaction ft;
    private ArrayList<Fragment> userFeedHistory = new ArrayList<>();
    private ArrayList<Fragment> discoverHistory = new ArrayList<>();
    private CameraFragment cameraFragment;
    private ArrayList<Fragment> activityFollowingHistory = new ArrayList<>();
    private ArrayList<Fragment> activityYouHistory = new ArrayList<>();
    private ArrayList<Fragment> profileHistory = new ArrayList<>();
    private RadioGroup navBar;
    private RadioGroup activityBar;
    private int prevNavButtonId;
    private int navigationViewId = R.id.view1;
    private ActionBar actionBar;
    private View navigationBar;
    private BluetoothSwipeFragment bluetoothSwipeFragment;

    // Button IDs
    private final int userFeedButtonId = R.id.userfeed_button;
    private final int discoverButtonId = R.id.discover_button;
    private final int cameraButtonId = R.id.camera_button;
    private final int activityFeedButtonId = R.id.activityfeed_button;
    private final int activityFollowingButtonId = R.id.activity_following_button;
    private final int activityYouButtonId = R.id.activity_you_button;
    private final int profileButtonId = R.id.profile_button;

    private int logoutBrowserCount = 0;
    private boolean cameraOn = false;
    private String currentActivityFeed;

    // Intent request codes for bluetooth
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    //Location sercvice
    private LocationService mService;
    private boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            // destroy main activity
            if(MainActivity.mainActivity != null){
                MainActivity.mainActivity.finish();
            }

            Parameters.NavigationBarActivity = this;
            Parameters.NavigationBarContext = this.getApplicationContext();
            Parameters.NavigationBarView = findViewById(navigationViewId);

            //Init location service
            initLocService();

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

            // save current user profile
            if(Parameters.dummyData){
                Parameters.loginProfile = new Profile("");
            } else {
                Parameters.loginProfile = NetParams.NETWORK.getUserProfileInfo(
                        Parameters.login_key);
            }

            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            Parameters.loginProfileView = Parameters.loginProfile.getProfileView(inflater);
            Parameters.loginUserId = Parameters.loginProfile.getUsername().getUserId();
            Parameters.loginUsername = Parameters.loginProfile.getUsername().getUsername();
            Parameters.loginUser = new User(Parameters.loginUserId, Parameters.loginUsername,
                    Parameters.loginProfile.getUserImage().getImageString(),
                    Parameters.loginProfile.getProfName());

            createFragments();

            // get navigation view height when navBar layout changes
            navigationBar = findViewById(R.id.nav_bar);
            ViewTreeObserver vto = navigationBar.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    navigationBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    int[] location = new int[2];
                    navigationBar.getLocationOnScreen(location);
                    Parameters.NavigationViewHeight = location[1];
                }
            });

            activityBar = (RadioGroup) findViewById(R.id.activity_bar);
            activityFeedBar(false);

            // set default fragment to User Feed
            RadioButton userFeedButton = (RadioButton) findViewById(userFeedButtonId);
            userFeedButton.setChecked(true);
            showSortButton();   // show sort button
            getSupportFragmentManager().beginTransaction().
                    add(R.id.view1, userFeedHistory.get(0)).commit();
            prevNavButtonId = userFeedButtonId;

            // set default activity
            currentActivityFeed = Parameters.initialActivityFeed;
            int buttonId;
            if(currentActivityFeed.equals(Parameters.activityYou_key)){
                buttonId = activityYouButtonId;
                RadioButton activityButton = (RadioButton) findViewById(buttonId);
                activityButton.setChecked(true);
            } else if (currentActivityFeed.equals(Parameters.activityFollowing_key)){
                buttonId = activityFollowingButtonId;
                RadioButton activityButton = (RadioButton) findViewById(buttonId);
                activityButton.setChecked(true);
            }

        } else {
            createFragments();
        }

        navBar = (RadioGroup) findViewById(R.id.nav_bar);

        // set listener for navigation bar
        navBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                loadingAnimation();
                ft = getSupportFragmentManager().beginTransaction();
                switch (checkedId) {
                    case userFeedButtonId:
                        ft.replace(navigationViewId,
                                userFeedHistory.get(userFeedHistory.size() - 1));
                        prevNavButtonId = checkedId;

                        // update action bar buttons
                        backButton(userFeedHistory);
                        sortButton();
                        break;
                    case discoverButtonId:
                        ft.replace(navigationViewId,
                                discoverHistory.get(discoverHistory.size() - 1));
                        prevNavButtonId = checkedId;

                        // update action bar buttons
                        backButton(discoverHistory);
                        sortButton();
                        break;
                    case cameraButtonId:
                        ft.replace(navigationViewId, cameraFragment);
                        cameraOn = true;
                        sortButton();
                        break;
                    case activityFeedButtonId:
                        ArrayList<Fragment> activityHistory = getCurrentActivityFeed();
                        ft.replace(navigationViewId,
                                activityHistory.get(activityHistory.size() - 1));
                        prevNavButtonId = checkedId;

                        // update action bar buttons
                        backButton(activityHistory);
                        sortButton();
                        break;
                    case profileButtonId:
                        ft.replace(navigationViewId,
                                profileHistory.get(profileHistory.size() - 1));
                        prevNavButtonId = checkedId;

                        // update action bar buttons
                        backButton(profileHistory);
                        sortButton();
                        break;
                }
                ft.commit();
            }
        });

        // set listener for activity bar
        activityBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                loadingAnimation();
                ft = getSupportFragmentManager().beginTransaction();
                switch (checkedId) {
                    case activityFollowingButtonId:
                        ft.replace(navigationViewId,
                                activityFollowingHistory.get(activityFollowingHistory.size() - 1));
                        backButton(activityFollowingHistory);
                        currentActivityFeed = Parameters.activityFollowing_key;
                        break;
                    case activityYouButtonId:
                        ft.replace(navigationViewId,
                                activityYouHistory.get(activityYouHistory.size() - 1));
                        backButton(activityYouHistory);
                        currentActivityFeed = Parameters.activityYou_key;
                        break;
                }
                ft.commit();
            }
        });
    }

    // adds a fragment to be displayed in a history
    public void showFragment(Fragment fragment){
        loadingAnimation();
        replaceView(fragment);

        switch (prevNavButtonId) {
            case userFeedButtonId:
                userFeedHistory.add(fragment);
                break;
            case discoverButtonId:
                discoverHistory.add(fragment);
                break;
            case activityFeedButtonId:
                ArrayList<Fragment> activityHistory = getCurrentActivityFeed();
                activityHistory.add(fragment);
                break;
            case profileButtonId:
                profileHistory.add(fragment);
                break;
        }
        // update UI bars and buttons
        activityFeedBar(false);
        sortButton();
    }

    // starts the loading animation on screen
    private void loadingAnimation() {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            }
        }, Parameters.loadingAnimationDelay);
    }

    // replaces the main view with a fragment
    public void replaceView(Fragment fragment){
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(navigationViewId, fragment);
        ft.commit();
    }

    // shows or hides activity feed bar
    public void activityFeedBar(boolean show) {
        if(show){
            activityBar.setVisibility(View.VISIBLE);
        } else {
            activityBar.setVisibility(View.GONE);
        }
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
                ArrayList<Fragment> activityHistory = getCurrentActivityFeed();
                updateHistory(activityHistory);
                break;
            case profileButtonId:
                updateHistory(profileHistory);
                break;
        }
    }

    // update history when back button is clicked
    public void updateHistory(ArrayList<Fragment> history){
        int size;

        if(!cameraOn) {
            // if not in Camera Fragment, replace and delete previous fragment
            replaceView(history.get(history.size() - 2));
            history.remove(history.size() - 1);
        } else {
            // if in Camera Fragment, replace with previous fragment and check previous navigation
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

        // show sort button if in main User Feed view
        sortButton();
    }

    // create navigation fragments
    private void createFragments(){
        userFeedHistory.add(new UserFeedFragment());
        discoverHistory.add(new DiscoverFragment());
        cameraFragment = new CameraFragment();
        activityFollowingHistory.add(new ActivityFollowingFragment());
        activityYouHistory.add(new ActivityYouFragment());
        profileHistory.add(ProfileFragment.newInstance(Parameters.loginUserId, false));


        ft = getSupportFragmentManager().beginTransaction();
        Parameters.bluetoothSwipeFragment = new BluetoothSwipeFragment();
        ft.add(Parameters.bluetoothSwipeFragment, "bluetoothSwipeFragment");
        ft.commit();

    }

    // gets the current activity feed
    private ArrayList<Fragment> getCurrentActivityFeed(){
        ArrayList<Fragment> activityHistory = new ArrayList<>();
        if(currentActivityFeed.equals(Parameters.activityFollowing_key)){
            activityHistory = activityFollowingHistory;
        } else if (currentActivityFeed.equals(Parameters.activityYou_key)){
            activityHistory = activityYouHistory;
        }
        return activityHistory;
    }

    // shows or hides back button on history count
    public void backButton(ArrayList<Fragment> history){

        // if history size is more than one then show back button, else hide it
        if(history.size() > 1){
            showBackButton();
        } else {
            hideBackButton();
        }
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

    // shows or hides the sort button accordingly
    public void sortButton(){
        if(prevNavButtonId == userFeedButtonId && userFeedHistory.size() == 1){
            showSortButton();
        } else {
            hideSortButton();
        }
    }

    // show the sort button on the action bar
    public void showSortButton(){
        if (actionBar != null){
            actionBar.getCustomView().
                    findViewById(R.id.sort_button_group).setVisibility(View.VISIBLE);
        }
    }

    // hide the sort button on the action bar
    public void hideSortButton(){
        if (actionBar != null){
            actionBar.getCustomView().
                    findViewById(R.id.sort_button_group).setVisibility(View.GONE);
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
        NetParams.NETWORK = null;
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
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.w("test", "nav bar saved");

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
        switch (item.getItemId()){
            case R.id.action_logout:{
                clearToken();

                // logout of instagram account
                WebView myWebView = new WebView(getApplicationContext());
                myWebView.clearFormData();
                setContentView(myWebView);
                myWebView.setWebViewClient(new LogoutWebViewClient());
                myWebView.loadUrl(NetParams.LOGOUT_URL);
                NetParams.ACCESS_TOKEN = null;
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    //Location Service
    private void initLocService(){
        //Init LocationService Service
        Log.v("gps", "startService");
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocationService.LocatioServicenBinder binder = (LocationService.LocatioServicenBinder) service;
            Log.v("gps","newSercixeconn");
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    // WebView client for logging out
    private class LogoutWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.w("test", "logout webView url: "+url);

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
