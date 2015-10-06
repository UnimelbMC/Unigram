package co.example.junjen.mobileinstagram;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import co.example.junjen.mobileinstagram.elements.Image;
import co.example.junjen.mobileinstagram.elements.Parameters;
import co.example.junjen.mobileinstagram.network.Network;
import co.example.junjen.mobileinstagram.network.Params;


public class MainActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Image loginUserImage;
    private Network net;

    // Keys
    String username_key = Parameters.loginUsername_key;
    String password_key = Parameters.loginPassword_key;
    String token_key = Parameters.loginToken_key;
    String loginUserImage_key = Parameters.loginUserImage_key;

    int loginClickInBrowserCount = 0;
    int loginClickInBrowserCountMax = 2;
    int urlCount = 0;
    int urlCountMax = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get access token file path
        Params.ACCESS_TOKEN_FILEPATH = getFilesDir().getPath().toString() + Params.ACCESS_TOKEN_FILENAME;

        // check if token is present
        checkToken();

        //Set permission for library to access the internet
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // set custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        TextView title = (TextView) getSupportActionBar().
                getCustomView().findViewById(R.id.action_bar_title);
        title.setText(Parameters.mainTitle);
        title.setTextSize(Parameters.mainTitleSize);

        usernameField = (EditText) findViewById(R.id.login_username_editText);
        passwordField = (EditText) findViewById(R.id.login_password_editText);

        usernameField.setHint(Parameters.usernameFieldHint);
        passwordField.setHint(Parameters.passwordFieldHint);
    }

    // action to take when login button is clicked
    public void loginButtonAction(View v){

        // check for token
        checkToken();

        // if no access token found, go to browser to authenticate
        if (Params.ACCESS_TOKEN == null) {

            WebView myWebView = new WebView(this);
            myWebView.clearFormData();
            setContentView(myWebView);
            myWebView.setWebViewClient(new WebViewClient() {

                // prevent browser app from starting for webview to be within the app
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }

                // capture code in url
                @Override
                public void onLoadResource(WebView view, String url) {

                    Log.w("test",url);

                    // if redirected, access code is in url
                    if (url.startsWith(Params.REDIRECT_URI)) {
                        loginClickInBrowserCount = 0;
                        urlCount = 0;

                        // extract access code
                        String[] parts = url.split("=");
                        Params.AUHTORIZE_CODE = parts[1];
                        Log.v("TEST_NET", Params.AUHTORIZE_CODE);
                        getAccessCode(Params.AUHTORIZE_CODE);

                        // go to navigation screen
                        startNavBar();

                        // update view of main activity
                        view.destroy();
                        setContentView(R.layout.activity_main);

                    }
                    // if login button in webview clicked, disable clicking
                    // (prevents error page popup from spamming the login button)
                    else if (url.startsWith(Params.URL_HEADER)) {
                        loginClickInBrowserCount++;

                        if(loginClickInBrowserCount == loginClickInBrowserCountMax) {
                            view.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return true;
                                }
                            });
                        }
                    }
                    // if login is incorrect enable webview clicking
                    if (loginClickInBrowserCount == loginClickInBrowserCountMax){
                        urlCount++;

                        if (urlCount > urlCountMax){
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
            });
            myWebView.loadUrl(Params.AUTHORIZE_URL);
        } else {
            startNavBar();
        }
    }

    // retrieve access code
    private void getAccessCode(String code){
        InstagramService service = new InstagramAuthService()
                .apiKey(Params.CLIENT_ID)
                .apiSecret(Params.CLIENT_SECRET)
                .callback(Params.REDIRECT_URI)
                .build();
        // Note : An empty token can be define as follows -

        final Token EMPTY_TOKEN = null;
        // Validate your user against Instagram
        //String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        // Getting the Access Token
        Verifier verifier = new Verifier(code);
        Params.ACCESS_TOKEN = service.getAccessToken(EMPTY_TOKEN, verifier);
        Log.v("TEST_ACCESS", Params.ACCESS_TOKEN.toString());

        // writing ACCESS_TOKEN to access token file
        try {
            File accessTokenFile = new File(Params.ACCESS_TOKEN_FILEPATH);
            if(!accessTokenFile.exists()) {
                accessTokenFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(accessTokenFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(Params.ACCESS_TOKEN);
            oos.close();
        } catch (FileNotFoundException e) {
            Log.w("test","file not found");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // go to the navigation screen
    public void startNavBar(){
        Intent intent = new Intent(MainActivity.this, NavigationBar.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        MainActivity.this.startActivity(intent);
    }

    // checks if there is a access token present
    private void checkToken(){
        // read from saved access token if available

        Params.ACCESS_TOKEN = null;
        try {
            File accessTokenFile = new File(Params.ACCESS_TOKEN_FILEPATH);
            if(accessTokenFile.exists()) {
                FileInputStream fis = new FileInputStream(accessTokenFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Params.ACCESS_TOKEN = (Token) ois.readObject();
                Log.w("test", "token exists");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if (Params.ACCESS_TOKEN != null){
            startNavBar();
        }
    }

    public void checkLogin(){
        // get username and password input
        String loginUsername = usernameField.getText().toString();
        String loginPassword = passwordField.getText().toString();

        boolean authenticated = false;

        // TODO: authenticate username with password and get required token
        // TESTING
        String token = null;
        ArrayList<Map<String, String>> accounts = new ArrayList<>();
        Map<String, String> account1 = new HashMap<>();
        Map<String, String> account2 = new HashMap<>();
        account1.put(username_key, "");
        account1.put(password_key, "");
        account2.put(username_key, "qqq");
        account2.put(password_key, "www");
        accounts.add(account1);
        accounts.add(account2);
        for (Map account : accounts){
            if(account.get(username_key).equals(loginUsername) &&
                    account.get(password_key).equals(loginPassword)){
                authenticated = true;
                //Authenticate with Instagram
//                startActivity(Network.LaunchAuthBrowser());
                // TODO: get token type
                token = null;

            }
        }

        loginPassword = null;

        if(!authenticated) {

            // TODO: pop-up saying invalid login and password

        } else {

            //TESTING
            if (token == null){
                ImageView loginUserImage = (ImageView) this.findViewById(R.id.login_user_image);
                loginUserImage.setImageResource(R.drawable.login_user_image);
            } else {
                // TODO: get user image based on token and display
                loginUserImage = new Image(Parameters.default_image);
            }

            Intent intent = new Intent(MainActivity.this, NavigationBar.class);

            // TODO: pass token into Navigation screen creation
            Bundle b = new Bundle();
            b.putString(token_key, token);
            intent.putExtras(b);
            MainActivity.this.startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("resume", "1");
        Log.w("test","main activity resumed");

        checkToken();
     //   Intent intent = new Intent(MainActivity.this, NavigationBar.class);
//
//            // TODO: pass token into Navigation screen creation
//            Bundle b = new Bundle();
//            b.putString(token_key, token);
//            intent.putExtras(b);
          // MainActivity.this.startActivity(intent);

    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v("start","2");

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.w("test", "main activity saved");

        // Save the user's current game state
        savedInstanceState.putSerializable(loginUserImage_key, loginUserImage);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
