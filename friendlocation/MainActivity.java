package com.example.ajay.friendlocation;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener{
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mReceiver;
    GoogleMap googleMap = null;
    DrawerLayout drawerLayout;
    View fragmentDrawer;
    ImageView imgMenu;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    LocationManager locationManager;
    ConnectivityManager connectivityManager;
    private BroadcastReceiver mLocationReceiver;
    RecyclerView rvCircleFriend;
    ImageView imgShareCode;
    PreferenceManager spm;
    MyReceiver myReceiver;
    LinearLayoutManager layoutManager;
    ArrayList<UserModel> alFriends=new ArrayList<>();
    MyCircleFriendAdapter circleFriendAdapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spm=new PreferenceManager(this);

        //layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        layoutManager=new LinearLayoutManager(this);

        imgMenu= (ImageView) findViewById(R.id.imgMenu);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawerLayout);
        fragmentDrawer=findViewById(R.id.fragmentDrawer);
        imgMenu.setOnClickListener(this);
        rvCircleFriend= (RecyclerView) findViewById(R.id.rvCirclesFriend);

        imgShareCode= (ImageView) findViewById(R.id.btnShareCode);
        imgShareCode.setOnClickListener(this);
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        SideMenuFragment fragment = new SideMenuFragment(MainActivity.this);
        fragmentTransaction.add(R.id.fragmentDrawer,fragment);
        fragmentTransaction.commit();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myReceiver= new MyReceiver();
        //registerReceiver(myReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(myReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
        registerReceiver(myReceiver,new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (statusOfGPS) {
            if(isConnected)
            {
                if (!MyService.isRunning) {
                    Intent updateService = new Intent(this, MyService.class);
                    startService(updateService);
                }
            }
            else
            {
                Snackbar snackbar=Snackbar.make(findViewById(R.id.coordinateLayout1),"No internet connection!",Snackbar.LENGTH_LONG);
                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                snackbar.setActionTextColor(Color.RED);
                View sbView=snackbar.getView();
                TextView textView= (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
            }

            // Log.d(TAG,"GPS ::"+"Enabled");
        } else {
            Toast.makeText(this, "Turn On Location", Toast.LENGTH_SHORT).show();

        }

        initBroadCastReceiver();
        setBroadCastReceiver();
    }

    private void initBroadCastReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String lat=intent.getDoubleExtra("latitude", 0)+"";
                String lng=intent.getDoubleExtra("longitude", 0)+"";
                String date_time=intent.getStringExtra("date_time");
                Log.d(TAG, "Latitude ::" + lat);
                Log.d(TAG, "Longitude ::" + lng);
                UserModel userModel=new UserModel();
                userModel.setUserId(spm.getUserId());
                userModel.setName(spm.getName());
                userModel.setLatitude(lat);
                userModel.setLongitude(lng);
                userModel.setDateTime(date_time);

                // updateLocationOnMap(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));
            }
        };
        mLocationReceiver=new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                String responseData=intent.getStringExtra(WSKey.friendLocationJson);
                Log.d(TAG,"Receive Friends list :"+responseData);
                alFriends.clear();

                try {
                    JSONArray jsonArrayFriends=new JSONArray(responseData);

                    for (int i=0;i<jsonArrayFriends.length();i++)
                    {
                        UserModel userModel1=new UserModel();
                        JSONObject jsonObj=jsonArrayFriends.getJSONObject(i);

                        if(jsonObj.optString(JSONKey.user_id).equals(spm.getUserId()))
                        {
                            continue;
                        }

                        userModel1.setUserId(jsonObj.optString(JSONKey.user_id));
                        userModel1.setName(jsonObj.optString(JSONKey.name));
                        userModel1.setLatitude(jsonObj.optString(JSONKey.latitude));
                        userModel1.setLongitude(jsonObj.optString(JSONKey.longitude));
                        userModel1.setDateTime(jsonObj.optString(JSONKey.date_time));

                        alFriends.add(userModel1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                rvCircleFriend.setLayoutManager(layoutManager);
                circleFriendAdapter=new MyCircleFriendAdapter(MainActivity.this,alFriends);
                rvCircleFriend.setAdapter(circleFriendAdapter);
                circleFriendAdapter.notifyDataSetChanged();

                if(alFriends.size()>0)
                {
                    updateMarkerOnMap();
                }


            }
        };
    }

    private void setBroadCastReceiver() {

        this.registerReceiver(mReceiver, new IntentFilter("com.ajay.location_change"));
        this.registerReceiver(mLocationReceiver,new IntentFilter("com.ajay.new_friend_location"));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        SideMenuFragment fragment = new SideMenuFragment(MainActivity.this);
        fragmentTransaction.replace(R.id.fragmentDrawer,fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if(isConnected)
        {
            if(spm.isCircleSelected())
            {
                Intent getFriendLocation=new Intent(this,GetFriendsLocationService.class);
                startService(getFriendLocation);
                Log.d(TAG,"GetFriend Location Service Started..");
            }
            else {
                Toast.makeText(this, "Select Circle to get Your Friend Location.!", Toast.LENGTH_LONG).show();
            }

        }
        else {
            Log.d(TAG,"GetFriend Location Service Not Started..");
        }

    }

    private void updateMarkerOnMap() {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_red_800_36dp);
        if(googleMap!=null)
        {
            LatLng mylatlong=null;
            googleMap.clear();
            LatLngBounds.Builder builder = LatLngBounds.builder();
            for (UserModel userModel :alFriends) {
                String address=null;
                LatLng latLng=null;
                try {
                    double lat = Double.parseDouble(userModel.getLatitude());
                    double lng = Double.parseDouble(userModel.getLongitude());
                    address= GeoAddressHelper.getAddressFromLocation(this,lat, lng);
                    latLng=new LatLng(lat,lng);

                } catch (Exception e) {
                    address=null;
                    latLng=null;
                }
                if(address!=null && latLng!=null)
                {
                    Bitmap imageBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.map_3);
                    Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(mutableBitmap);
                    Paint paint=new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(50);
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    int x=mutableBitmap.getHeight()/2;
                    int y=mutableBitmap.getWidth()/2;
                    canvas.drawText(userModel.getName().substring(0,1).toUpperCase(),x,y,paint);

                    BitmapDescriptor icon2 = BitmapDescriptorFactory.fromBitmap(mutableBitmap);

                    MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                            .title(userModel.getName())
                            .snippet(address)
                            .icon(icon2);

                    googleMap.addMarker(markerOptions);

                    builder.include(latLng);
                    mylatlong=latLng;
                }

            }

            LatLngBounds latLngBounds=builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100),1000,null);
          //  googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,300));
            //googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter() ,15) );
           //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlong,15));
        }
    }

    private void updateLocationOnMap(double latitude, double longitude) {
        if (googleMap != null) {
            googleMap.clear();
            LatLng latLng = new LatLng(latitude, longitude);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_red_800_36dp);

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

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
                snippedDesc = "Hi i am here";
            }

            //create map marker
            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                    .title("My Location")
                    .snippet(snippedDesc)
                    .icon(icon);

            googleMap.addMarker(markerOptions);

            //LatLngBounds.Builder builder = LatLngBounds.builder();
            //builder.include(latLng);
            //LatLngBounds latLngBounds=builder.build();
            try {
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,1000));
                //googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,500));
                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)      // Sets the center of the map to Mountain View
                        .zoom(13)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "Google map not ready");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent getFriendLocation=new Intent(this,GetFriendsLocationService.class);
        stopService(getFriendLocation);
        Log.d(TAG,"GetFriend Location Service Stopped..");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
        this.unregisterReceiver(mLocationReceiver);
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng( 22.2587,71.1924) , 5) );
        googleMap.setMinZoomPreference(1.0f);
        googleMap.setMaxZoomPreference(18.0f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgMenu:
                drawerLayout.openDrawer(fragmentDrawer);
                break;
            case R.id.btnShareCode:
                if(spm.isCircleSelected())
                {
                    Intent shareIntent=new Intent(this,ShareCodeActivity.class);
                    startActivity(shareIntent);
                }
                else {
                    Toast.makeText(this, "Select Circle From Left Drawer.!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


}
