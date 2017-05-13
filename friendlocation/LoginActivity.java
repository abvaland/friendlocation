package com.example.ajay.friendlocation;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    TextView txtNewMember;
    EditText etEmail, etPassword;
    Button btnLogin;
    TextInputLayout  inputLayoutEmail, inputLayoutPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtNewMember= (TextView) findViewById(R.id.txtNewMember);
        txtNewMember.setOnClickListener(this);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutPass = (TextInputLayout) findViewById(R.id.inputLayoutPass);
        btnLogin= (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtNewMember:
                Intent registerIntent=new Intent(this,RegisterActivity.class);
                startActivity(registerIntent);
                finish();
                break;

            case R.id.btnLogin:
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (checkValidation(email, password)) {
                    if (InternetHelper.checkInternet(this)) {
                        generateUrlForLogin(email, password);
                    }
                    else {
                        AlertDialogManager.noInternetDialog(this);
                    }
                }
                break;
        }
    }

    private void generateUrlForLogin(String email, String password) {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.login_api);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.email,email);
        param.put(WSKey.password,password);

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

        callLoginWS(POST_URL);
    }

    private void callLoginWS(String post_url) {
        AlertDialogManager.showWaitingDialog(this);
        final WebServiceHelper webServiceHelper=new WebServiceHelper(this);
        webServiceHelper.callWS(post_url, new WebServiceHelper.JSONRequestHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                webServiceHelper.parseJSONOResponse(response);
                Log.d(TAG,"status::"+response);
                AlertDialogManager.releaseDialog();
                try {
                    String status=response.getString(JSONKey.status);
                    switch (status)
                    {
                        case "200":
                            handleResult(response);
                            break;
                        case "701":
                            Toast.makeText(LoginActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            break;
                        case "702":
                            //Toast.makeText(RegisterActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            inputLayoutEmail.setError(response.getString(JSONKey.message));
                            etEmail.requestFocus();
                            break;
                        case "703":
                            inputLayoutPass.setError(response.getString(JSONKey.message));
                            etPassword.requestFocus();
                            break;

                        case "704":
                            Toast.makeText(LoginActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
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

    private void handleResult(JSONObject response) throws JSONException {
        PreferenceManager pm=new PreferenceManager(this);
        Log.d(TAG,"User_id :: "+response.getString(JSONKey.user_id));
        Log.d(TAG,"Name :: "+response.getString(JSONKey.name));
        Log.d(TAG,"Email ::"+response.get(JSONKey.email));
        pm.setUserId(response.getString(JSONKey.user_id));
        pm.setName(response.getString(JSONKey.name));
        pm.setEmail(response.getString(JSONKey.email));
        Intent mainActivity=new Intent(this,MainActivity.class);
        startActivity(mainActivity);
        pm.clearSelectedCircle();
        finish();
    }

    private boolean checkValidation(String email, String password) {
        inputLayoutEmail.setErrorEnabled(false);
        inputLayoutPass.setErrorEnabled(false);
        if(email.trim().isEmpty())
        {
            inputLayoutEmail.setError("Email not empty");
            etEmail.requestFocus();
            return false;
        }
        else if(!MyValidation.isValidEmail(email))
        {
            inputLayoutEmail.setError("Invalid Email Id");
            etEmail.requestFocus();
            return false;
        }
        else if(password.trim().isEmpty())
        {
            inputLayoutPass.setError("Password not empty");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }
}
