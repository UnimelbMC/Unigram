package co.example.junjen.mobileinstagram.network;

/**
 * Created by Jaime on 10/4/2015.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Jaime on 10/4/2015.
 */
public class NetworkService extends Service {
    private final IBinder mBinder = new NetworkBinder();
    private Network network;

    @Override
    public void onCreate() {
        network = new Network();

    }

    public class NetworkBinder extends Binder {
        NetworkService getService() {
            return NetworkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
}
