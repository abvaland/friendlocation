package com.example.ajay.friendlocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareCodeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgBack;
    TextView txtInviteCode;
    Button btnShareCode;
    PreferenceManager spm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_code);
        spm=new PreferenceManager(this);
        imgBack= (ImageView) findViewById(R.id.imgBack);
        txtInviteCode= (TextView) findViewById(R.id.txtInviteCode);
        btnShareCode= (Button) findViewById(R.id.btnShareCode);

        //set Invite Code to TextView
        String selectedCode=spm.getSelectedCircleCode();
        txtInviteCode.setText(selectedCode);

        imgBack.setOnClickListener(this);
        btnShareCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBack:
                finish();
                break;
            case R.id.btnShareCode:
                String inviteCode=txtInviteCode.getText().toString();
                String shareText="Use this code to join my circle : "+inviteCode;
                Intent shareCodeIntent=new Intent(Intent.ACTION_SEND);
                shareCodeIntent.putExtra(Intent.EXTRA_TEXT,shareText);
                shareCodeIntent.setType("text/plain");

                Intent chooser=Intent.createChooser(shareCodeIntent,"Share this code via");

                if(shareCodeIntent.resolveActivity(getPackageManager())!=null)
                {
                    startActivity(chooser);
                }
                else {
                    Toast.makeText(this, "You don't have any app to share Code.!", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }
}
