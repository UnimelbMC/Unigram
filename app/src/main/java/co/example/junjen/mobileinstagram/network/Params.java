package co.example.junjen.mobileinstagram.network;

/**
 * Created by Jaime on 10/4/2015.
 */
public class Params {
    public static final String CLIENT_ID = "c978ebb2c7544f28a0403cf0a0c90fc7";
    public static final String CLIENT_SECRET = "afba876a0eac4d79b6cc8520c2b5e330";
    public static final String REDIRECT_URI = "network://localhost/redirect/";
    //Flow 1, get code from instagram auth server
    public static final String AUTHORIZE_URL= "https://api.instagram.com/oauth/authorize/?client_id="+CLIENT_ID+"&redirect_uri="+REDIRECT_URI+"&response_type=code";
    public static String AUHTORIZE_CODE="";
    public static String ACCESS_TOKEN = "";
}
