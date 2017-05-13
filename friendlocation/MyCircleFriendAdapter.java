package com.example.ajay.friendlocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ajay on 12-04-2017.
 */

class MyCircleFriendAdapter extends RecyclerView.Adapter<MyCircleFriendAdapter.MyViewHolder> {
    //String names[]={"Ajay","Mehul","Tushar","Ketan","Faiz"};
    Context context;
    ArrayList<UserModel> alFriends;

    public MyCircleFriendAdapter(Context context, ArrayList<UserModel> alFriends) {
        this.context = context;
        this.alFriends = alFriends;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_friend_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserModel userModel = alFriends.get(position);

        char firstChar = userModel.getName().toUpperCase().charAt(0);
        holder.txtFirstChar.setText(firstChar + "");
        holder.txtFriendName.setText(userModel.getName());
        holder.txtDateTime.setText(userModel.getDateTime());
        try {
            double lat = Double.parseDouble(userModel.getLatitude());
            double lng = Double.parseDouble(userModel.getLongitude());
            String address = getAddressFromLocation(lat, lng);
            holder.txtAddress.setText(address);

        } catch (Exception e) {
            holder.txtAddress.setText("Location not available.");
        }

    }

    private String getAddressFromLocation(double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        } catch (IOException e) {
            e.printStackTrace();
        }

        String snippedDesc = null;
        if (addresses != null) {
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            // String knownName = addresses.get(0).getFeatureName();

            snippedDesc = address + "," + city + "," + state + "," + country + "," + postalCode;
        } else {
            snippedDesc = "Address not available";
        }

        return snippedDesc;
    }


    @Override
    public int getItemCount() {
        return alFriends.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFriend;
        TextView txtFriendName;
        TextView txtFirstChar;
        TextView txtAddress;
        TextView txtDateTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgFriend = (ImageView) itemView.findViewById(R.id.imgFriend);
            txtFriendName = (TextView) itemView.findViewById(R.id.txtFriendName);
            txtFirstChar = (TextView) itemView.findViewById(R.id.txtFirstChar);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            txtDateTime= (TextView) itemView.findViewById(R.id.txtDateTime);
        }
    }
}
