package com.example.ajay.friendlocation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetFriendsLocationService extends Service {
    private static final String TAG = "GetFriendsLocationServ";
    PreferenceManager spm;
    @Override
    public void onCreate() {
        super.onCreate();
        spm=new PreferenceManager(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"On Start Command");

        generateGetFriendsUrl();

        return START_STICKY;
    }

    private void generateGetFriendsUrl() {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.getFriendsApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.circle_id,spm.getSelectedCircleId());
        //param.put(WSKey.user_id,spm.getUserId());

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

        callGetFriendsWS(POST_URL);
    }

    private void callGetFriendsWS(final String post_url) {
        final WebServiceHelper webServiceHelper=new WebServiceHelper(this);
        webServiceHelper.callWS(post_url, new WebServiceHelper.JSONRequestHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                webServiceHelper.parseJSONOResponse(response);
                Log.d(TAG,"Response::"+response);
                try {
                    String status=response.getString(JSONKey.status);
                    switch (status)
                    {
                        case "200":
                            Log.d(TAG,response.getString(JSONKey.message));
                            Intent sendFriendResult=new Intent("com.ajay.new_friend_location");
                            JSONArray jsonArrayFriends=response.getJSONArray(JSONKey.data);
                            sendFriendResult.putExtra(WSKey.friendLocationJson,jsonArrayFriends.toString());
                            sendBroadcast(sendFriendResult);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(30000);
                                        Log.d(TAG,"NEw List Request After 60 second");
                                        generateGetFriendsUrl();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                            break;
                        case "701":
                            Log.d(TAG,response.getString(JSONKey.message));
                            break;
                        case "702":
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

    @Override
    public void onDestroy() {
        Log.d(TAG,"On Destroy");
        super.onDestroy();
    }
}
