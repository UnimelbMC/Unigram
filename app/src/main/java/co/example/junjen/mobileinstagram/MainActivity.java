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

        EditText usernameInput = (EditText) this.findViewById(R.id.login_username_editText);
        String loginUsername = usernameInput.getText().toString();
        EditText passwordInput = (EditText) this.findViewById(R.id.login_password_editText);
        String loginPassword = passwordInput.getText().toString();

        // TODO: pass username and password to fragments
//        Bundle b = new Bundle();
//        b.putInt("key", 1); //Your id
//        intent.putExtras(b); //Put your id to your next Intent
//
//        Bundle b = getIntent().getExtras();
//        int value = b.getInt("key");

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
