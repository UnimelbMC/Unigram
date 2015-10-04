package co.example.junjen.mobileinstagram;

import android.app.ActionBar;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setContentView(R.layout.activity_main);
        usernameField = (EditText) findViewById(R.id.login_username_editText);
        passwordField = (EditText) findViewById(R.id.login_password_editText);

        usernameField.setHint(Parameters.usernameFieldHint);
        passwordField.setHint(Parameters.passwordFieldHint);
    }

    public void actionBar(View v){
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
                startActivity(Network.LaunchAuthBrowser());
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
        if (Params.ACCESS_TOKEN.compareTo("") !=0) {
            Log.v("MAIN_RESUME", Params.ACCESS_TOKEN);

        }
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
