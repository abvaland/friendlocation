package com.example.ajay.friendlocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgBack;
    LinearLayout llProfile;
    TextView txtCircleOption;
    TextView txtLogOut,txtUserName;
    PreferenceManager spm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        spm=new PreferenceManager(this);

        txtUserName= (TextView) findViewById(R.id.txtUserName);
        imgBack= (ImageView) findViewById(R.id.imgBack);
        llProfile= (LinearLayout) findViewById(R.id.llProfile);
        txtCircleOption= (TextView) findViewById(R.id.txtCircleOption);
        txtLogOut= (TextView) findViewById(R.id.txtLogout);

        llProfile.setOnClickListener(this);
        txtCircleOption.setOnClickListener(this);
        txtLogOut.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        txtUserName.setText(spm.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                finish();
                break;
            case R.id.llProfile:
                Intent editProfileIntent=new Intent(this,EditProfileActivity.class);
                startActivity(editProfileIntent);
                break;

            case R.id.txtLogout:
                spm.clearLoginPref();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

            case R.id.txtCircleOption:
                Intent circleListIntent=new Intent(this,CircleListActivity.class);
                startActivity(circleListIntent);
                break;
        }
    }
}
