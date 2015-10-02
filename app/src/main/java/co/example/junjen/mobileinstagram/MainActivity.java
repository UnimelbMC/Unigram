package co.example.junjen.mobileinstagram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    EditText editText;
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.login_username_editText);
        editText2 = (EditText) findViewById(R.id.login_password_editText);

        editText.setHint("Username");
        editText2.setHint("Password");
    }

    public void actionBar(View v){
        Intent intent = new Intent(MainActivity.this, NavigationBar.class);

        // get username and password input
        EditText usernameInput = (EditText) this.findViewById(R.id.login_username_editText);
        String loginUsername = usernameInput.getText().toString();
        EditText passwordInput = (EditText) this.findViewById(R.id.login_password_editText);
        char[] loginPassword = passwordInput.getText().toString().toCharArray();

        // pass username and password into Navigation screen creation
        Bundle b = new Bundle();
        b.putString("username", loginUsername);
        b.putCharArray("password", loginPassword);  // used char[] for added security
        intent.putExtras(b);
        b.clear();  // clear username and password
        MainActivity.this.startActivity(intent);
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
