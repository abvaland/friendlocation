package com.example.ajay.friendlocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";

    public MyReceiver()
    {
        Log.d(TAG,"On Constructor");
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(TAG,"On Receive");
        if(intent.getAction().equals("android.location.PROVIDERS_CHANGED") || intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE"))
        {
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

           /* ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=cm.getActiveNetworkInfo();
            boolean isConnected=networkInfo!=null && networkInfo.isConnectedOrConnecting();*/
            if(statusOfGPS)
            {
                if(!MyService.isRunning)
                {
                    Intent updateService=new Intent(context,MyService.class);
                    context.startService(updateService);
                    MyService.isRunning=true;
                }
               // Log.d(TAG,"GPS ::"+"Enabled");
            }
            else {
                if(MyService.isRunning)
                {
                    Intent updateService=new Intent(context,MyService.class);
                    context.stopService(updateService);
                    MyService.isRunning=false;
                }
               // Log.d(TAG,"GPS ::"+"Disable");
            }
        }

    }
}
