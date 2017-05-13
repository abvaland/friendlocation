package com.example.ajay.friendlocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ajay on 14-04-2017.
 */

public class GeoAddressHelper {


    public static String getAddressFromLocation(Context context,double lat, double lng) {
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
}
