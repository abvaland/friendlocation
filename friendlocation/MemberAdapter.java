package com.example.ajay.friendlocation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ajay on 18-04-2017.
 */

class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
    private static final String TAG = "MemberAdapter";
    Context context;
    ArrayList<UserModel> alMembers;
    PreferenceManager spm;
    boolean isAdmin;
    String ownerID;
    String circleID;
    public MemberAdapter(Context context, ArrayList<UserModel> alMembers, boolean isOwner, String ownerID, String circleID) {
        this.context=context;
        this.alMembers=alMembers;
        spm=new PreferenceManager(context);
        isAdmin=isOwner;
        this.ownerID=ownerID;
        this.circleID=circleID;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final UserModel userModel=alMembers.get(position);
        if(ownerID.equals(userModel.getUserId()))
        {
            holder.txtName.setText(userModel.getName()+"(Admin)");
            holder.imgDelete.setEnabled(false);
            holder.imgDelete.setAlpha(0.5f);
        }
        else {
            holder.txtName.setText(userModel.getName());
        }

        if(!isAdmin)
        {
            holder.imgDelete.setEnabled(false);
            holder.imgDelete.setAlpha(0.5f);
        }


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();

                removeMember(circleID,userModel.getUserId(),position);
            }
        });

    }

    private void removeMember(String circleID, String userId, int position) {
        String POST_URL=context.getResources().getString(R.string.post_url);
        String api=context.getResources().getString(R.string.removeMemberApi);
        POST_URL=POST_URL+api;

        HashMap<String,String> param=new HashMap<>();
        param.put(WSKey.circle_id,circleID);
        param.put(WSKey.user_id,userId);
        POST_URL=WebServiceHelper.getApiUrl(param,POST_URL);

        Log.d(TAG,"URL :: "+POST_URL);
        callRemoveMemberWS(POST_URL,position);
    }

    private void callRemoveMemberWS(String post_url, final int position) {
        final WebServiceHelper webServiceHelper=new WebServiceHelper(context);
        AlertDialogManager.showWaitingDialog(context);
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
                            alMembers.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Remove Member Successfully.!", Toast.LENGTH_SHORT).show();
                            break;
                        case "701":
                            Toast.makeText(context, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
                            Log.d(TAG,response.getString(JSONKey.message));
                            break;
                        case "702":
                            Toast.makeText(context, response.getString(JSONKey.message), Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return alMembers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView imgDelete;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtName= (TextView) itemView.findViewById(R.id.txtName);
            imgDelete= (ImageView) itemView.findViewById(R.id.imgDelete);
        }
    }
}
