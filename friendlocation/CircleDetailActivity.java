package com.example.ajay.friendlocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.ajay.friendlocation.WebServiceHelper.context;

public class CircleDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CircleDetailActivity";
    ImageView imgBack;
    TextView txtCircleName;
    RecyclerView rvMembers;
    Button btnLeaveCircle,btnDeleteCircle;
    LinearLayout llEditCircle;
    private String circleID;
    private String circleName;
    private String ownerID;
    PreferenceManager spm;
    boolean isOwner=false;
    ArrayList<UserModel> alMembers=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_detail);

        spm=new PreferenceManager(this);
        imgBack= (ImageView) findViewById(R.id.imgBack);
        txtCircleName= (TextView) findViewById(R.id.txtCircleName);
        rvMembers= (RecyclerView) findViewById(R.id.rvMembers);
        btnLeaveCircle= (Button) findViewById(R.id.btnLeaveCircle);
        btnDeleteCircle= (Button) findViewById(R.id.btnDeleteCircle);
        llEditCircle= (LinearLayout) findViewById(R.id.llEditCircle);
        btnLeaveCircle.setOnClickListener(this);
        btnDeleteCircle.setOnClickListener(this);
        llEditCircle.setOnClickListener(this);

        setUIData();
    }

    private void setUIData() {
        circleID=getIntent().getStringExtra(WSKey.circle_id);
        circleName=getIntent().getStringExtra(WSKey.circle_name);
        ownerID=getIntent().getStringExtra(WSKey.owner_id);

        Log.d(TAG,"Circle ID="+circleID);
        Log.d(TAG,"Circle Name="+circleName);
        Log.d(TAG,"Circle Owner="+ownerID);
        txtCircleName.setText(circleName);

        if(ownerID.equals(spm.getUserId()))
        {
            btnDeleteCircle.setVisibility(View.VISIBLE);
            btnLeaveCircle.setVisibility(View.GONE);
            isOwner=true;
        }
        else {
            btnDeleteCircle.setVisibility(View.GONE);
            btnLeaveCircle.setVisibility(View.VISIBLE);
            isOwner=false;
        }

        getMemberList(circleID);
    }

    private void getMemberList(String circleID) {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.getFriendsApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.circle_id,circleID);
        //param.put(WSKey.user_id,spm.getUserId());

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

        callGetMembersWS(POST_URL);
    }

    private void callGetMembersWS(String post_url) {
        final WebServiceHelper webServiceHelper=new WebServiceHelper(this);
        AlertDialogManager.showWaitingDialog(this);
        webServiceHelper.callWS(post_url, new WebServiceHelper.JSONRequestHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                AlertDialogManager.releaseDialog();
                webServiceHelper.parseJSONOResponse(response);
                Log.d(TAG,"Response::"+response);
                try {
                    String status=response.getString(JSONKey.status);
                    switch (status)
                    {
                        case "200":
                            Log.d(TAG,response.getString(JSONKey.message));
                            handleResult(response);
                            break;
                        case "701":
                            Toast.makeText(CircleDetailActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            Log.d(TAG,response.getString(JSONKey.message));
                            break;
                        case "702":
                            Toast.makeText(CircleDetailActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
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
                AlertDialogManager.releaseDialog();
            }
        });
    }

    private void handleResult(JSONObject response) {

        alMembers.clear();
        try {
            JSONArray jsonArrayFriends=response.getJSONArray(JSONKey.data);

            for (int i=0;i<jsonArrayFriends.length();i++)
            {
                UserModel userModel1=new UserModel();
                JSONObject jsonObj=jsonArrayFriends.getJSONObject(i);

                userModel1.setUserId(jsonObj.optString(JSONKey.user_id));
                userModel1.setName(jsonObj.optString(JSONKey.name));

                alMembers.add(userModel1);

                Log.d(TAG,"User Details ::"+userModel1.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(new MemberAdapter(this,alMembers,isOwner,ownerID,circleID));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnLeaveCircle:
                leaveCircle();
                break;
            case R.id.btnDeleteCircle:
                removeCircle();
                break;
            case R.id.llEditCircle:
                break;
        }
    }

    private void removeCircle() {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.removeCircleApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.circle_id,circleID);
        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);
        callRemoveCircleWS(POST_URL);
    }

    private void callRemoveCircleWS(String post_url) {
        final WebServiceHelper webServiceHelper=new WebServiceHelper(this);
        AlertDialogManager.showWaitingDialog(this);
        webServiceHelper.callWS(post_url, new WebServiceHelper.JSONRequestHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                AlertDialogManager.releaseDialog();
                webServiceHelper.parseJSONOResponse(response);
                Log.d(TAG,"Response::"+response);
                try {
                    String status=response.getString(JSONKey.status);
                    switch (status)
                    {
                        case "200":
                            Log.d(TAG,response.getString(JSONKey.message));
                            Toast.makeText(CircleDetailActivity.this, "Leave Circle SuccessFully.!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CircleDetailActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            spm.clearSelectedCircle();
                            finish();
                            break;
                        case "701":
                            Toast.makeText(CircleDetailActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            Log.d(TAG,response.getString(JSONKey.message));
                            break;
                        case "702":
                            Toast.makeText(CircleDetailActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
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
                AlertDialogManager.releaseDialog();
            }
        });
    }

    private void leaveCircle() {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.removeMemberApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.circle_id,circleID);
        param.put(WSKey.user_id,spm.getUserId());
        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);
        callRemoveMemberWS(POST_URL);
    }

    private void callRemoveMemberWS(String post_url) {
        final WebServiceHelper webServiceHelper=new WebServiceHelper(this);
        AlertDialogManager.showWaitingDialog(this);
        webServiceHelper.callWS(post_url, new WebServiceHelper.JSONRequestHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                AlertDialogManager.releaseDialog();
                webServiceHelper.parseJSONOResponse(response);
                Log.d(TAG,"Response::"+response);
                try {
                    String status=response.getString(JSONKey.status);
                    switch (status)
                    {
                        case "200":
                            Log.d(TAG,response.getString(JSONKey.message));
                            Toast.makeText(CircleDetailActivity.this, "Leave Circle SuccessFully.!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CircleDetailActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            spm.clearSelectedCircle();
                            finish();
                            break;
                        case "701":
                            Toast.makeText(CircleDetailActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            Log.d(TAG,response.getString(JSONKey.message));
                            break;
                        case "702":
                            Toast.makeText(CircleDetailActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
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
                AlertDialogManager.releaseDialog();
            }
        });
    }
}
