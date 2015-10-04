package co.example.junjen.mobileinstagram;

import android.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import co.example.junjen.mobileinstagram.elements.Profile;

public class NavigationBar extends AppCompatActivity {

    FragmentTransaction ft;
    UserFeedFragment userFeedFragment;
    DiscoverFragment discoverFragment;
    CameraFragment cameraFragment;
    ActivityFeedFragment activityFeedFragment;
    ProfileFragment profileFragment;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        setContentView(R.layout.activity_navigation_bar);
        // get username and password
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                finish();
            } else {
                // TODO: get token type
                token = extras.getString("token");

                // TODO: pass token for creating fragments
                // create fragments
                createFragments();

                // set default fragment to User Feed
                RadioButton userFeedButton = (RadioButton) findViewById(R.id.userfeed_button);
                userFeedButton.setChecked(true);
                getSupportFragmentManager().beginTransaction().add(R.id.view1, userFeedFragment).commit();
            }
        } else {
            // recreate fragments from previous token
            token = savedInstanceState.getString("token");
            createFragments();
        }

        // set listener for navigation bar
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.nav_bar);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ft = getSupportFragmentManager().beginTransaction();
                switch (checkedId) {
                    case R.id.userfeed_button:
                        ft.replace(R.id.view1, userFeedFragment);
                        break;
                    case R.id.discover_button:
                        ft.replace(R.id.view1, discoverFragment);
                        break;
                    case R.id.camera_button:
                        ft.replace(R.id.view1, cameraFragment);
                        break;
                    case R.id.activityfeed_button:
                        ft.replace(R.id.view1, activityFeedFragment);
                        break;
                    case R.id.profile_button:
                        ft.replace(R.id.view1, profileFragment);
                        break;
                }
                ft.commit();
            }
        });
    }

    // TODO: update argument to receive token
    private void createFragments(){
        userFeedFragment = new UserFeedFragment();
        discoverFragment = new DiscoverFragment();
        cameraFragment = new CameraFragment();
        activityFeedFragment = new ActivityFeedFragment();
        profileFragment = ProfileFragment.newInstance(new Profile());
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
