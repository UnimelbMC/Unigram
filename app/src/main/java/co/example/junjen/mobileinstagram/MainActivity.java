package co.example.junjen.mobileinstagram;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import co.example.junjen.mobileinstagram.elements.Image;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.network.LocationService;
import co.example.junjen.mobileinstagram.network.NetParams;
import co.example.junjen.mobileinstagram.network.Network;



public class MainActivity extends AppCompatActivity {

    int mainActivityView = R.layout.activity_main;
    public static Activity mainActivity;

    int loginClickInBrowserCount = 0;
    int urlCount = 0;
    int splashScreenDuration = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mainActivityView);

        mainActivity = this;

        // set application MainActivityContext to be accessible by other classes
        Parameters.MainActivityContext = this.getApplicationContext();

        Log.w("test", "main activity created");

        // get access token file path
        NetParams.ACCESS_TOKEN_FILEPATH =
                getFilesDir().getPath().toString() + NetParams.ACCESS_TOKEN_FILENAME;

        //Set permission for library to access the internet
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // set custom action bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar);
            TextView title = (TextView) getSupportActionBar().
                    getCustomView().findViewById(R.id.action_bar_title);
            title.setText(Parameters.mainTitle);
            title.setTextSize(Parameters.mainTitleSize);
        }

        // check if token is present
        checkToken();

    }

    // action to take when login button is clicked
    public void loginButtonAction(View v){

        Log.w("test", "login button clicked");

        // check for token
        checkToken();

        // if no access token found, go to browser to authenticate
        if (NetParams.ACCESS_TOKEN == null) {

            WebView myWebView = new WebView(getApplicationContext());
            myWebView.clearFormData();
            setContentView(myWebView);
            myWebView.setWebViewClient(new LoginWebViewClient());
            myWebView.loadUrl(NetParams.AUTHORIZE_URL);
        }
    }


    // WebView client for login sessions
    private class LoginWebViewClient extends WebViewClient{

        // prevent browser app from starting for webview to be within the app
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        // capture code in url
        @Override
        public void onLoadResource(WebView view, String url) {

            Log.w("test", "login webView url: "+url);

            // if redirected, access code is in url
            if (url.startsWith(NetParams.REDIRECT_URI)) {
                loginClickInBrowserCount = 0;
                urlCount = 0;

                // extract access code
                String[] parts = url.split("=");
                NetParams.AUHTORIZE_CODE = parts[1];
                Log.v("TEST_NET", NetParams.AUHTORIZE_CODE);
                getAccessCode(NetParams.AUHTORIZE_CODE);

                // update view of main activity
                view.destroy();

                setContentView(mainActivityView);

                checkToken();

                // update details on login screen
                updateLoginScreen();

                // go to navigation screen
                startNavBar();
            }
            // if login button in webview clicked, disable clicking
            // (prevents error page popup from spamming the login button)
            else if (url.startsWith(NetParams.LOGIN_URL_HEADER)) {
                loginClickInBrowserCount++;

                if (loginClickInBrowserCount == Parameters.loginClickInBrowserCountMax) {
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                }
            }
            // if login is incorrect enable webview clicking
            if (loginClickInBrowserCount == Parameters.loginClickInBrowserCountMax) {
                urlCount++;

                if (urlCount > Parameters.urlCountMax) {
                    urlCount = 0;
                    loginClickInBrowserCount = 1;
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                }
            }
        }
    }

    // retrieve access code
    private void getAccessCode(String code){
        InstagramService service = new InstagramAuthService()
                .apiKey(NetParams.CLIENT_ID)
                .apiSecret(NetParams.CLIENT_SECRET)
                .callback(NetParams.REDIRECT_URI)
                .build();
        // Note : An empty token can be define as follows -

        final Token EMPTY_TOKEN = null;
        // Validate your user against Instagram
        //String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        // Getting the Access Token
        Verifier verifier = new Verifier(code);
//        NetParams.ACCESS_TOKEN = service.getAccessToken(EMPTY_TOKEN, verifier);
//        Log.v("TEST_ACCESS", NetParams.ACCESS_TOKEN.toString());

        // writing ACCESS_TOKEN to access token file
        try {
            File accessTokenFile = new File(NetParams.ACCESS_TOKEN_FILEPATH);
            if(!accessTokenFile.exists()) {
                accessTokenFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(accessTokenFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(service.getAccessToken(EMPTY_TOKEN, verifier));
            oos.close();
            Log.w("test", "written token to file");
        } catch (FileNotFoundException e) {
            Log.w("test","file not found");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // go to the navigation screen
    public void startNavBar(){

        // show splash screen for a duration of time before going to navigation screen
        int DELAY = splashScreenDuration;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, NavigationBar.class);
                MainActivity.this.startActivity(intent);
            }
        }, DELAY);
    }

    // checks if there is a access token present
    private void checkToken(){

        // read from saved access token if available
        NetParams.ACCESS_TOKEN = null;
        try {
            File accessTokenFile = new File(NetParams.ACCESS_TOKEN_FILEPATH);
            if(accessTokenFile.exists()) {
                FileInputStream fis = new FileInputStream(accessTokenFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                NetParams.ACCESS_TOKEN = (Token) ois.readObject();
                Log.v("TEST_ACCESS", NetParams.ACCESS_TOKEN.toString());
                Log.w("test", "token exists");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        // update details on login screen
        updateLoginScreen();

        if (NetParams.ACCESS_TOKEN != null){
            //If we have token but no Network, initialise
            if ( NetParams.NETWORK == null) {
                NetParams.NETWORK = new Network();
            }
            // go to navigation screen
            startNavBar();

            mainActivity.finish();
        }
    }

    // update details on login screen
    public void updateLoginScreen(){

        ImageView userImage = (ImageView) this.findViewById(R.id.login_user_image);
        TextView username = (TextView) this.findViewById(R.id.login_username);
        Button loginButton = (Button) this.findViewById(R.id.login_button);

        //fill login screen with user that is currently logged in
        if (NetParams.ACCESS_TOKEN != null) {
            // initialise Network object
            String userImageLink;
            String usernameText;
            if (NetParams.NETWORK == null) {
                NetParams.NETWORK = new Network();
            }
            userImageLink = NetParams.NETWORK.getProfilePic();
            usernameText = NetParams.NETWORK.getUsername();

            // TODO: overwrite userImageLink and usernameText using Data Object

            Image.setImage(userImage, new Image(userImageLink));
            username.setText(Html.fromHtml("Hello <b>" + usernameText.toUpperCase() + "</b>"));
            username.setTextSize(Parameters.subTitleSize);
            username.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);

        } else if (userImage != null && username != null && loginButton != null){
            // return login to default state when no user is logged in

            Image.setImage(userImage, new Image(Parameters.default_emptyUserImageLink));
            username.setText(Parameters.default_username.toUpperCase());
            username.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("resume", "1");
        Log.w("test", "main activity resumed");

        splashScreenDuration = Parameters.splashScreenDuration;

        checkToken();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    /*    if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }*/
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.w("test", "main activity saved");

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.w("test","main activity restored");

        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
    }

    @Override
    protected void onDestroy() {
        Log.w("test", "main activity destroyed");

        super.onDestroy();
    }

}