package com.example.ajay.friendlocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EditProfileActivity";
    ImageView imgBack;
    EditText etName;
    EditText etEmail;
    Button btnSave;
    PreferenceManager spm;
    RelativeLayout relativeLayoutBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        spm=new PreferenceManager(this);
        relativeLayoutBase= (RelativeLayout) findViewById(R.id.relativeLayoutBase);
        imgBack= (ImageView) findViewById(R.id.imgBack);
        etName= (EditText) findViewById(R.id.etName);
        etEmail= (EditText) findViewById(R.id.etEmail);
        btnSave= (Button) findViewById(R.id.btnSave);

        imgBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        setViewData();
    }

    private void setViewData() {
        etName.setText(spm.getName());
        etEmail.setText(spm.getEmail());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                this.finish();
                break;

            case R.id.btnSave:
                if(InternetHelper.checkInternet(this))
                {
                    String name=etName.getText().toString();
                    String email=etEmail.getText().toString();
                    if(checkValidation(name,email))
                    {
                        generateUpdateUrl(name,email);
                    }
                }
                else {
                    AlertDialogManager.noInternetDialog(this);
                }
                break;
        }
    }

    private void generateUpdateUrl(String name, String email) {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.editProfileApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.name,encodeField(name));
        param.put(WSKey.email,email);
        param.put(WSKey.user_id,spm.getUserId());

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

        callEditProfileWS(POST_URL);
    }

    private void callEditProfileWS(String post_url) {
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
                            Toast.makeText(EditProfileActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                             handleResult(response);
                            finish();
                            break;
                        case "701":
                            Toast.makeText(EditProfileActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            break;
                        case "702":
                            etEmail.selectAll();
                            etEmail.setError(response.getString(JSONKey.message));
                            etEmail.requestFocus();
                            InputMethodManager inputMethodManager =
                                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInputFromWindow(
                                    relativeLayoutBase.getApplicationWindowToken(),
                                    InputMethodManager.SHOW_FORCED, 0);
                            break;
                        case "703":
                            Toast.makeText(EditProfileActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
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
        try {
            spm.setEmail(response.getString(JSONKey.email));
            spm.setName(response.getString(JSONKey.name));
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidation(String name, String email) {

        if(name.trim().isEmpty())
        {
            etName.setError("Name not Empty.!");
            etName.requestFocus();
            return false;
        }
        else if(email.trim().isEmpty())
        {
            etEmail.setError("Email not Empty.!");
            etEmail.requestFocus();
            return false;
        }
        else if(!MyValidation.isValidEmail(email))
        {
            etEmail.setError("Invalid Email Id");
            etEmail.requestFocus();
            return false;
        }
        return true;
    }
}
