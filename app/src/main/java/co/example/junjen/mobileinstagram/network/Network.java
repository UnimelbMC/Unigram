package co.example.junjen.mobileinstagram.network;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.exceptions.InstagramException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import co.example.junjen.mobileinstagram.MainActivity;

/**
 * Created by Jaime on 10/4/2015.
 */
public class Network  extends MainActivity {

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

        instagram = new Instagram(Params.ACCESS_TOKEN);
        finish();
        //Go back to main activity
        // UPDATE: check if token is null or not
      //  if (Params.ACCESS_TOKEN != null){
          /*  Intent navIntent = new Intent(MainActivity.this, NavigationBar.class);
//
//            // TODO: pass token into Navigation screen creation

            startActivity(navIntent);*/
      //  }
      //  Intent homepage = new Intent(this, MainActivity.class);
    //    startActivity(homepage);
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

    public static Intent LaunchAuthBrowser(){
        String url = Params.AUTHORIZE_URL;
        Intent internetIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
       // internetIntent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
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
