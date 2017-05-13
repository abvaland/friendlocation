package com.example.ajay.friendlocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Ajay on 11-04-2017.
 */

class MenuCircleAdapter extends RecyclerView.Adapter<MenuCircleAdapter.MyViewHolder> {

    private static final String TAG = "MenuCircleAdapter";
    Context context;
    String circleIds[];
    String circleNames[];
    String circleInviteCode[];
    PreferenceManager spm;
    public MenuCircleAdapter(Context context, String[] circleIds, String[] circleNames, String[] circleInviteCode) {

        this.context=context;
        this.circleIds=circleIds;
        this.circleNames=circleNames;
        this.circleInviteCode=circleInviteCode;
        spm=new PreferenceManager(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_single_circle,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.txtCircleName.setText(circleNames[position]);

        if(spm.isCircleSelected())
        {
            Log.d(TAG,"Selected Circle Id :: "+spm.getSelectedCircleId());
            Log.d(TAG,"Circle Id :: "+circleIds[position]);

            if(circleIds[position].equals(spm.getSelectedCircleId()))
            {
                holder.rbt1.setChecked(true);
            }
            else {
                holder.rbt1.setChecked(false);
            }
        }
        else {
            holder.rbt1.setChecked(false);
            Log.d(TAG,"Not Selected Any Circle");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rbt1.setChecked(true);
                spm.setSelectedCircleId(circleIds[position]);
                Log.d(TAG,"circle set id :: "+circleIds[position]);
                spm.setSelectedCircleName(circleNames[position]);
                spm.setSelectedCircleCode(circleInviteCode[position]);
                notifyDataSetChanged();


                Intent getFriendLocation=new Intent(context,GetFriendsLocationService.class);
                context.startService(getFriendLocation);
                Log.d(TAG,"GetFriend Location Service Started..");
            }
        });

        holder.rbt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rbt1.setChecked(true);
                spm.setSelectedCircleId(circleIds[position]);
                Log.d(TAG,"circle set id :: "+circleIds[position]);
                spm.setSelectedCircleName(circleNames[position]);
                spm.setSelectedCircleCode(circleInviteCode[position]);
                notifyDataSetChanged();
                Intent getFriendLocation=new Intent(context,GetFriendsLocationService.class);
                context.startService(getFriendLocation);
                Log.d(TAG,"GetFriend Location Service Started..");
            }
        });


    }

    @Override
    public int getItemCount() {
        return circleIds.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton rbt1;
        TextView txtCircleName;
        public MyViewHolder(View itemView) {
            super(itemView);
            rbt1= (RadioButton) itemView.findViewById(R.id.rbt1);
            txtCircleName= (TextView) itemView.findViewById(R.id.txtCircleName);
        }
    }
}
