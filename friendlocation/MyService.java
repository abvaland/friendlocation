package com.example.ajay.friendlocation;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks,LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MyService";
    public static boolean isRunning = false;
    private GoogleApiClient mGoogleApiClient;
    public MyService() {
        Log.d(TAG, "MyService Constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Log.d(TAG,"onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        isRunning = true;
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        mGoogleApiClient.disconnect();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected");
        startLocationUpdate();
    }

    private void startLocationUpdate() {
        Log.d("MainActivity:", "Created Request");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        //mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connected Fail");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("MainActivity ::","Update the Location");

        double lat=location.getLatitude();
        double lng=location.getLongitude();


        Intent updateLocationIntent=new Intent("com.ajay.location_change");
        updateLocationIntent.putExtra("latitude",lat);
        updateLocationIntent.putExtra("longitude",lng);
        updateLocationIntent.putExtra("date_time",getDateTime());
        sendBroadcast(updateLocationIntent);

        PreferenceManager preferenceManager=new PreferenceManager(getApplicationContext());
        if(preferenceManager.isLogin())
        {
            if(InternetHelper.checkInternet(getApplicationContext()))
            {
                generateURLForUpdateLocation(preferenceManager,lat,lng);
            }
            else {
                Log.d(TAG,"Internet Off");
            }

        }
        else {
            Log.d(TAG,"Login Require");
        }

        Log.d(TAG,"Latitude ::"+lat+"");
        Log.d(TAG,"Longitude ::"+lng+"");
    }

    private void generateURLForUpdateLocation(PreferenceManager preferenceManager, double lat, double lng) {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.updateLocationApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.user_id,preferenceManager.getUserId());
        param.put(WSKey.latitude,lat+"");
        param.put(WSKey.longitude,lng+"");
        param.put(WSKey.date_time,getDateTime());

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

       callLocationUpdateWS(POST_URL);
    }

    private void callLocationUpdateWS(String post_url) {
       final WebServiceHelper webServiceHelper=new WebServiceHelper(this);
        webServiceHelper.callWS(post_url, new WebServiceHelper.JSONRequestHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                webServiceHelper.parseJSONOResponse(response);
                Log.d(TAG,"status::"+response);
                try {
                    String status=response.getString(JSONKey.status);
                    switch (status)
                    {
                        case "200":
                            Log.d(TAG,response.getString(JSONKey.message));
                            break;
                        case "701":
                            Log.d(TAG,response.getString(JSONKey.message));
                            break;
                        case "702":
                            Log.d(TAG,response.getString(JSONKey.message));
                            break;
                        case "703":
                            Log.d(TAG,response.getString(JSONKey.message));
                            break;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                super.onFailure(error);
                //error.printStackTrace();
            }
        });
    }

    public String getDateTime()
    {
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy,hh:mm:ss");
        Calendar calendar=Calendar.getInstance();
        String date_time=sdf.format(calendar.getTime());
        Log.d(TAG,"Date Time ::"+date_time);

        return date_time;
    }
}
