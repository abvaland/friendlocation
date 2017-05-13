package com.example.ajay.friendlocation;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Ajay on 08-04-2017.
 */

public class SideMenuFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Menu Fragment";
    LinearLayout llCircle;
    RecyclerView rvCircles;
    Button btnCreateCircle,btnJoinCircle;
    TextView txtSetting,txtLogout;
    PreferenceManager pm;
   Context context;

    public SideMenuFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.side_menu_layout,container,false);
        llCircle= (LinearLayout) view.findViewById(R.id.llCircle);
        rvCircles= (RecyclerView) view.findViewById(R.id.rvCircles);
        btnCreateCircle= (Button) view.findViewById(R.id.btnCreateCircle);
        btnJoinCircle= (Button) view.findViewById(R.id.btnJoinCircle);
        txtSetting= (TextView) view.findViewById(R.id.txtSetting);
        txtLogout= (TextView) view.findViewById(R.id.txtLogout);


        txtLogout.setOnClickListener(this);
        btnCreateCircle.setOnClickListener(this);
        btnJoinCircle.setOnClickListener(this);
        txtSetting.setOnClickListener(this);
        pm=new PreferenceManager(context);
        //call webservice for getting circles

        if(InternetHelper.checkInternet(getActivity()))
        {
            generateGetCircleUrl();
        }
        else {
            Log.d(TAG,"No Internet Connection.!");
        }



        return view;
    }

    private void generateGetCircleUrl() {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.getCirclesApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.user_id,pm.getUserId());

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

        callGetCirclesWS(POST_URL);
    }

    private void callGetCirclesWS(String post_url) {
        //AlertDialogManager.showWaitingDialog(getActivity());
        final WebServiceHelper webServiceHelper=new WebServiceHelper(getActivity());
        webServiceHelper.callWS(post_url, new WebServiceHelper.JSONRequestHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                webServiceHelper.parseJSONOResponse(response);
                Log.d(TAG,"Response::"+response);
                //AlertDialogManager.releaseDialog();
                try {
                    String status=response.getString(JSONKey.status);
                    switch (status)
                    {
                        case "200":
                            handleResult(response);
                            break;
                        case "701":
                            Toast.makeText(getActivity(), response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            break;
                        case "702":
                            Log.d(TAG,response.optString(JSONKey.message));
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
            //    AlertDialogManager.releaseDialog();
            }
        });
    }

    private void setViewData() {
        String[] circleIds=pm.getCircleIds().split(",");
        String[] circleNames=pm.getCircleNames().split(",");
        String[] circleInviteCode=pm.getInvitesCodes().split(",");
        rvCircles.setLayoutManager(new LinearLayoutManager(getActivity()));
        String selectedCircleId=pm.getSelectedCircleId();
        boolean isSelectedAvailable=false;
        for (String cId :
                circleIds) {
            if (selectedCircleId.equals(cId))
            {
                isSelectedAvailable=true;
            }
        }
        if(!isSelectedAvailable)
        {
            pm.clearSelectedCircle();
        }
        rvCircles.setAdapter(new MenuCircleAdapter(context,circleIds,circleNames,circleInviteCode));
        llCircle.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnCreateCircle:
                Intent createCircleIntent=new Intent(getActivity(),CreateCircleActivity.class);
                startActivity(createCircleIntent);
                break;

            case R.id.btnJoinCircle:
                Intent joinCircleIntent=new Intent(getActivity(),JoinCircleActivity.class);
                startActivity(joinCircleIntent);
                break;


            case R.id.txtLogout:
                pm.clearLoginPref();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
                break;

            case R.id.txtSetting:
                Intent settingIntent=new Intent(getActivity(),SettingActivity.class);
                startActivity(settingIntent);
                break;
        }
    }

    private void handleResult(JSONObject response) {
        pm.setCircleIds(response.optString(JSONKey.circle_ids));
        pm.setCircleNames(response.optString(JSONKey.circle_names));
        pm.setInviteCodes(response.optString(JSONKey.invite_codes));


        Log.d(TAG,"Circle ids ::"+pm.getCircleIds());
        setViewData();
    }
}
