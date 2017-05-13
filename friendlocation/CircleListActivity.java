package com.example.ajay.friendlocation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CircleListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "CircleListActivity";
    ImageView imgBack;
    ListView lvCircles;
    PreferenceManager spm;
    private String[] circleIds;
    private String[] circleNames;
    private String ownerIds[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_list);

        spm=new PreferenceManager(this);
        imgBack= (ImageView) findViewById(R.id.imgBack);
        lvCircles= (ListView) findViewById(R.id.lvCircle);
        lvCircles.setOnItemClickListener(this);

        getCirclesList();
    }

    private void getCirclesList() {
        String POST_URL=getResources().getString(R.string.post_url);
        String api=getResources().getString(R.string.getCirclesApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.user_id,spm.getUserId());

        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);

        callGetCirclesWS(POST_URL);
    }

    private void callGetCirclesWS(String post_url) {
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
                            handleResult(response);
                            break;
                        case "701":
                            Toast.makeText(CircleListActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            break;
                        case "702":
                            Toast.makeText(CircleListActivity.this, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
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

        circleIds=response.optString(JSONKey.circle_ids).split(",");
        circleNames=response.optString(JSONKey.circle_names).split(",");
        ownerIds=response.optString(JSONKey.owner_ids).split(",");

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,circleNames);
        lvCircles.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent circleDetailIntent=new Intent(this,CircleDetailActivity.class);
        circleDetailIntent.putExtra(WSKey.circle_id,circleIds[position]);
        circleDetailIntent.putExtra(WSKey.circle_name,circleNames[position]);
        circleDetailIntent.putExtra(WSKey.owner_id,ownerIds[position]);
        startActivity(circleDetailIntent);
    }
}
