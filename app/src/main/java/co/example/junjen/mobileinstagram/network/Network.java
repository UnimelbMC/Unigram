package co.example.junjen.mobileinstagram.network;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.exceptions.InstagramException;

import co.example.junjen.mobileinstagram.MainActivity;

/**
 * Created by Jaime on 10/4/2015.
 */
public class Network  extends AppCompatActivity {
    private Token accessToken;
    private Instagram instagram;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("TEST_NET","1");
        //network://localhost/redirect/?code=baf92352dbb144eeb2e3bfb6403ba360
        final Intent intent = getIntent();
        String data = getIntent().getData().toString();

        String[] parts = data.split("=");
        Params.AUHTORIZE_CODE = parts[1];
        Log.v("TEST_NET", Params.AUHTORIZE_CODE);
        getAccessCode(Params.AUHTORIZE_CODE);
        //  getUserFeed();

        instagram = new Instagram(accessToken);
        Intent homepage = new Intent(this, MainActivity.class);
        startActivity(homepage);
    }
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
        accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        Log.v("TEST_ACCESS", accessToken.toString());
    }

    public static Intent LaunchAuthBrowser(){
        String url = Params.AUTHORIZE_URL;
        Intent internetIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        internetIntent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
        internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       return internetIntent;
    }
    public UserInfo getUserInfo(String username){
        try {
            return instagram.getUserInfo(username);
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }
}
