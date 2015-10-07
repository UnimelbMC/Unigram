package co.example.junjen.mobileinstagram.network;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.basicinfo.UserInfoData;
import org.jinstagram.exceptions.InstagramException;

import co.example.junjen.mobileinstagram.elements.Profile;

/**
 * Created by Jaime on 10/4/2015.
 */
public class Network {
    // Object used to retrieve data from Instagram API
    private Instagram instagram;
    public Network() {
        instagram = new Instagram(Params.ACCESS_TOKEN);
    }

    public Profile getUserInfo(String username){
        UserInfoData uData;
        try {
            uData = instagram.getUserInfo(username).getData();
        } catch (InstagramException e) {
            e.printStackTrace();
        }
        return null;
    }
}
