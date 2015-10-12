package co.example.junjen.mobileinstagram.network;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import co.example.junjen.mobileinstagram.elements.Parameters;

public class LocationService extends Service implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    //Service ninder
    private final IBinder mBinder = new LocatioServicenBinder();
    //Location needed
    protected static GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest locationRequest;
    static boolean mRequestingLocationUpdates= true;
    LocationRequest mLocationRequest;
    private int count = 0;
    @Override
    public void onCreate() {
        Log.v("gps","onCreate");
        initGoogleApiClient();
        getLoc();
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.v("gps","onBind");
        requesLocUpdates(true);
        return mBinder;
    }


    public class LocatioServicenBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
    //API Client to get location
    private void initGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
    public static synchronized void getLoc() {
        Log.v("GPS-Conn", "1");
        if (mGoogleApiClient != null) {
            Log.v("GPS-Conn", "2");
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onConnected(Bundle connectionHint) {

        if (mRequestingLocationUpdates) {
            createLocationRequest();
            startLocationUpdates();

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            Log.v("GPS-onConn", "1");
            if (mLastLocation != null) {
                Parameters.DEV_LATITUDE = mLastLocation.getLatitude();
                Parameters.DEV_LONGITUDE = mLastLocation.getLongitude();
                Log.v("GPS-SUCCESS", Double.toString(Parameters.DEV_LATITUDE) + " " + Double.toString(Parameters.DEV_LONGITUDE));
            } else {
                Parameters.DEV_LATITUDE = -37.8138434;
                Parameters.DEV_LONGITUDE = 144.9595481;
                Log.v("GPS-FAILED", Double.toString(Parameters.DEV_LATITUDE) + " " + Double.toString(Parameters.DEV_LONGITUDE));
            }
        }

    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public static void requesLocUpdates(boolean v){
        mRequestingLocationUpdates = v;
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {
        //NOTHING
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Latitude south, longitude east
        Parameters.DEV_LATITUDE = -37.8138434;
        Parameters.DEV_LONGITUDE = 144.9595481;
        Log.v("GPS-FAILED-CONN",Double.toString(Parameters.DEV_LATITUDE)+" "+Double.toString(Parameters.DEV_LONGITUDE));
    }

    @Override
    public void onLocationChanged(Location location) {
        //Update device location
        if (location != null) {
            Parameters.DEV_LATITUDE = location.getLatitude();
            Parameters.DEV_LONGITUDE = location.getLongitude();
           if(count >= 3) {
               requesLocUpdates(false);
               stopLocationService();
               Parameters.LOC_DONE = true;
               count = 0;
               Log.v("GPS-UPDATED", Double.toString(Parameters.DEV_LATITUDE) + " " + Double.toString(Parameters.DEV_LONGITUDE));
               return;
           }
            count++;
        }

    }

    public void stopLocationService(){

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
}
