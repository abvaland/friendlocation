package com.example.ajay.friendlocation;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class JoinCircleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "JoinCircleActivity";
    ImageView imgBack;
    PinEntryEditText pinEntryCode;
    Button btnJoinCircle;
    PreferenceManager spm;
    RelativeLayout relativeLayoutParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);
        spm=new PreferenceManager(this);
        relativeLayoutParent= (RelativeLayout) findViewById(R.id.relativeLayoutParent);
        imgBack= (ImageView) findViewById(R.id.imgBack);
        pinEntryCode= (PinEntryEditText) findViewById(R.id.pinEntry1);
        btnJoinCircle= (Button) findViewById(R.id.btnJoinCircle);
        btnJoinCircle.setOnClickListener(this);
        pinEntryCode.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                Log.d(TAG,str.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnJoinCircle:
                String inviteCode=pinEntryCode.getText().toString();
                    if(inviteCode.length()==6)
                    {
                        if(InternetHelper.checkInternet(this))
                        {
                            generateJoinCircleUrl(inviteCode);
                        }
                        else {
                            AlertDialogManager.noInternetDialog(this);
                        }

                    }
                    else {
                        Toast.makeText(this, "Enter valid code of 6 character..!", Toast.LENGTH_SHORT).show();
                        pinEntryCode.setText(null);
                        pinEntryCode.requestFocus();
                        InputMethodManager inputMethodManager =
                                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInputFromWindow(
                                relativeLayoutParent.getApplicationWindowToken(),
                                InputMethodManager.SHOW_FORCED, 0);
                    }

                break;

            case R.id.imgBack:
                this.finish();
                break;
        }
    }

    private void generateJoinCircleUrl(String inviteCode) {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.joinCircleApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.invite_code,inviteCode);
        param.put(WSKey.user_id,spm.getUserId());

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

        callJoinCircleWS(POST_URL);
    }

    private void callJoinCircleWS(String post_url) {
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
                            Toast.makeText(JoinCircleActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            // handleResult(response);
                            finish();
                            break;
                        case "701":
                            Toast.makeText(JoinCircleActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            break;
                        case "702":
                            Toast.makeText(JoinCircleActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            break;
                        case "703":
                            Toast.makeText(JoinCircleActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            pinEntryCode.setText(null);
                            pinEntryCode.requestFocus();
                            InputMethodManager inputMethodManager =
                                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInputFromWindow(
                                    relativeLayoutParent.getApplicationWindowToken(),
                                    InputMethodManager.SHOW_FORCED, 0);
                            break;
                        case "704":
                            Toast.makeText(JoinCircleActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            pinEntryCode.setText(null);
                            pinEntryCode.requestFocus();
                            InputMethodManager inputMethodManager1 =
                                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager1.toggleSoftInputFromWindow(
                                    relativeLayoutParent.getApplicationWindowToken(),
                                    InputMethodManager.SHOW_FORCED, 0);
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


//https://github.com/alphamu/PinEntryEditText
