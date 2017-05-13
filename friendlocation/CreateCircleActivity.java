package com.example.ajay.friendlocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

import static com.example.ajay.friendlocation.WSKey.email;
import static com.example.ajay.friendlocation.WSKey.password;

public class CreateCircleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateCircleActivity";
    ImageView imgBack;
    EditText etCircleName;
    Button btnCreateCircle;
    PreferenceManager pm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle);

        pm=new PreferenceManager(this);
        imgBack= (ImageView) findViewById(R.id.imgBack);
        etCircleName= (EditText) findViewById(R.id.etCircleName);
        btnCreateCircle= (Button) findViewById(R.id.btnCreateCircle);

        imgBack.setOnClickListener(this);
        btnCreateCircle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                break;

            case R.id.btnCreateCircle:
                if(!etCircleName.getText().toString().trim().isEmpty())
                {
                    if(InternetHelper.checkInternet(this))
                    {
                        String circleName=etCircleName.getText().toString();
                        String userId=pm.getUserId();
                        String name=pm.getName();
                        String inviteCode=name.substring(0,3)+getSaltString();
                        Log.d(TAG,"Invite Code ::"+inviteCode);

                        generateCreateCirlceUrl(circleName,inviteCode,userId);
                    }
                    else {
                        AlertDialogManager.noInternetDialog(this);
                    }
                }
                else {
                    etCircleName.setError("Circle name not empty.!");
                }
                break;
        }
    }

    private void generateCreateCirlceUrl(String circleName, String inviteCode, String userId) {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.createCircleApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.circle_name,circleName);
        param.put(WSKey.invite_code,inviteCode);
        param.put(WSKey.user_id,userId);

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

        callCreateCircleWS(POST_URL);
    }

    private void callCreateCircleWS(String post_url) {
        AlertDialogManager.showWaitingDialog(this);
        final WebServiceHelper webServiceHelper=new WebServiceHelper(this);
        webServiceHelper.callWS(post_url, new WebServiceHelper.JSONRequestHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                webServiceHelper.parseJSONOResponse(response);
                Log.d(TAG,"Response::"+response);
                AlertDialogManager.releaseDialog();
                try {
                    String status=response.getString(JSONKey.status);
                    switch (status)
                    {
                        case "200":
                            Toast.makeText(CreateCircleActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            // handleResult(response);
                            finish();
                            break;
                        case "701":
                            Toast.makeText(CreateCircleActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            break;
                        case "702":
                            etCircleName.setError(response.getString(JSONKey.message));
                            etCircleName.requestFocus();
                            break;
                        case "703":
                            Toast.makeText(CreateCircleActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
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


    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 3) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        Log.d(TAG,"Code ::"+saltStr);
        return saltStr;
    }
}
