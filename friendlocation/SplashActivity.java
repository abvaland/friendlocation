package com.example.ajay.friendlocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    final int PERMISSION_ALL=1;
    String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION};
    PreferenceManager pm;
    //AVLoadingIndicatorView avLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MultiDex.install(this);
        pm=new PreferenceManager(this);
       // avLoading= (AVLoadingIndicatorView) findViewById(R.id.av_loading);
        //avLoading.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    gotoNextScreen();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void gotoNextScreen() {
        if(!hasPermission(this,permissions))
        {
            ActivityCompat.requestPermissions(this,permissions,PERMISSION_ALL);
        }
        else {

            if(pm.isLogin())
            {
                Intent mainIntent=new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
            else {
                Intent LoginIntent=new Intent(this, LoginActivity.class);
                startActivity(LoginIntent);
                finish();
            }

        }

    }

    private boolean hasPermission(Context context, String[] permissions) {

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && context!=null && permissions!=null)
        {
            for(String permission:permissions)
            {
                if(ActivityCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case PERMISSION_ALL:
                        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                        {
                            Log.d(TAG,"Location Permission Granted...");
                            if(pm.isLogin())
                            {
                                Intent mainIntent=new Intent(this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                            else {
                                Intent LoginIntent=new Intent(this, LoginActivity.class);
                                startActivity(LoginIntent);
                                finish();
                            }

                        }
                        else {
                            finish();
                        }
                break;
        }
    }
}
//https://github.com/81813780/AVLoadingIndicatorView