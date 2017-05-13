package com.example.ajay.friendlocation;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    TextView txtAlreadyAccount;
    EditText etName, etEmail, etPassword;
    Button btnRegister;
    TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txtAlreadyAccount = (TextView) findViewById(R.id.txtAlreadyAccount);
        txtAlreadyAccount.setOnClickListener(this);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutName);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutPass = (TextInputLayout) findViewById(R.id.inputLayoutPass);
        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAlreadyAccount:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;
            case R.id.btnRegister:
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (checkValidation(name, email, password)) {
                    if (InternetHelper.checkInternet(this)) {
                        generateUrlForReg(name, email, password);
                    }
                    else {
                        AlertDialogManager.noInternetDialog(this);
                    }
                }
                break;
        }
    }

    private boolean checkValidation(String name, String email, String password) {

        inputLayoutName.setErrorEnabled(false);
        inputLayoutEmail.setErrorEnabled(false);
        inputLayoutPass.setErrorEnabled(false);
        if (name.trim().isEmpty()) {
            inputLayoutName.setError("Name not empty");
            etName.requestFocus();
            return false;
        }
        else if(email.trim().isEmpty())
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

    private void generateUrlForReg(String name, String email, String password) {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.register_api);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.name,encodeField(name));
        param.put(WSKey.email,email);
        param.put(WSKey.password,password);

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

        callRegisterWS(POST_URL);
    }

    private void callRegisterWS(String POST_URL) {
        AlertDialogManager.showWaitingDialog(this);
        WebServiceHelper webServiceHelper=new WebServiceHelper(this);
        webServiceHelper.callWS(POST_URL, new WebServiceHelper.JSONRequestHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                AlertDialogManager.releaseDialog();
                try {
                    String status=response.getString(JSONKey.status);

                    switch (status)
                    {
                        case "200":
                            handleResult(response);
                            break;

                        case "701":
                            Toast.makeText(RegisterActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            break;
                        case "702":
                            //Toast.makeText(RegisterActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            inputLayoutEmail.setError(response.getString(JSONKey.message));
                            etEmail.requestFocus();
                            break;
                        case "703":
                            Toast.makeText(RegisterActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            break;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                super.onFailure(error);
                AlertDialogManager.releaseDialog();
            }
        });
    }

    private void handleResult(JSONObject response) throws JSONException {

        PreferenceManager pm=new PreferenceManager(this);
        Log.d(TAG,"User_id :: "+response.getString(JSONKey.user_id));
        Log.d(TAG,"Name :: "+response.getString(JSONKey.name));
        Log.d(TAG,"Email ::"+response.getString(JSONKey.email));
        pm.setUserId(response.getString(JSONKey.user_id));
        pm.setName(response.getString(JSONKey.name));
        pm.setEmail(response.getString(JSONKey.email));
        Intent mainActivity=new Intent(this,MainActivity.class);
        startActivity(mainActivity);
        pm.clearSelectedCircle();
        finish();
    }
}
