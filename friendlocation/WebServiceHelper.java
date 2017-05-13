package com.example.ajay.friendlocation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/***************************************************************************************************
 * class name :: WebServiceHelper
 * this class contain methods for calling web api and provide response to user
 ***************************************************************************************************/
public class WebServiceHelper {

    public static String TAG = "WebServiceHelper";
    public static String STATUS;
    public static String MESSAGE;
    public static JSONObject HEADER, DATA_OBJECT;
    public static JSONArray DATA_ARRAY;

    static Context context;
    RequestQueue mRequestQueue;

    /**********************************************************************************************
     * WebServiceHelper
     *
     * @param context
     **********************************************************************************************/
    public WebServiceHelper(Context context) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }//end of WebServiceHelper()

    /**********************************************************************************************
     * call ws and get the json data from ws
     *
     * @param postUrl
     * @param handler
     ***********************************************************************************************/
    public void callWS(String postUrl, final JSONRequestHandler handler) {
        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.GET, postUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handler.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onFailure(error);
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jr);

    }//end of class parseObject()

    /**********************************************************************************************
     * @param response
     * @return
     * @throws JSONException
     ***********************************************************************************************/
    public JSONObject getHeaderObject(JSONObject response) throws JSONException {
        return response.optJSONObject("header");
    }//end of getHeaderObject

    /**********************************************************************************************
     * @param jsonHeader
     * @return
     * @throws JSONException
     ***********************************************************************************************/
    public String getMessage(JSONObject jsonHeader) throws JSONException {
        return jsonHeader.optString("message");
    }//end of getMessage

    /**********************************************************************************************
     * @param jsonHeader
     * @return
     * @throws JSONException
     ***********************************************************************************************/
    public String getStatus(JSONObject jsonHeader) throws JSONException {
        return jsonHeader.optString("status");
    }//end of getStatus

    /**********************************************************************************************
     * @param response
     * @return
     * @throws JSONException
     ***********************************************************************************************/
    public JSONObject getDataObject(JSONObject response) throws JSONException {
        return response.optJSONObject("data");
    }//end of getDataObject

    /**********************************************************************************************
     * @param response
     * @return
     * @throws JSONException
     ***********************************************************************************************/
    public JSONArray getDataArray(JSONObject response) throws JSONException {
        return response.optJSONArray("data");
    }//end of getDataArray

    /***********************************************************************************************
     * parse the jsonObject and get all data inside json header
     *
     * @param response
     ***********************************************************************************************/
    public void parseJSONOResponse(JSONObject response) {
        if (response != null) {
            try {
                HEADER = response.optJSONObject("header");
                DATA_OBJECT = response.optJSONObject("data");
                DATA_ARRAY = response.optJSONArray("data");

                if (HEADER != null) {
                    STATUS = HEADER.optString("status");
                    MESSAGE = HEADER.optString("message");
                }
            } catch (Exception e) {

            }
        }
    }//end of parseJSONOResponse


    /***********************************************************************************************
     * Function to display simple Alert Dialog
     *
     * @param message - alert message
     **********************************************************************************************/
    public static void showAlertDialog(String message) {
     /*   AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert Message");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();*/
    }//end of showAlertDialog

    /***********************************************************************************************
     * thus method will give the url from nameVale pair
     *
     * @param urlNameValue
     * @param postUrl
     * @return
     **********************************************************************************************/
    public static String getApiUrl(HashMap<String, String> urlNameValue, String postUrl) {
        StringBuilder apiUrl = new StringBuilder(postUrl);

        //add ? end of url
        if (!postUrl.contains("?")) {
            apiUrl.append("?");
        }

        //append key values to url
        for (Map.Entry<String, String> entry : urlNameValue.entrySet()) {
            apiUrl.append("&" + entry.getKey() + "=" + entry.getValue());
        }

        return apiUrl.toString();
    }//end of getApiUrl()

    /***********************************************************************************************
     * thus method will give the url from nameVale pair with encoded data
     *
     * @param urlNameValue
     * @param postUrl
     * @return
     **********************************************************************************************/
    public static String getApiUrlEncoded(HashMap<String, String> urlNameValue, String postUrl) {
        StringBuilder apiUrl = new StringBuilder(postUrl);
        String base64 = "";
        //add ? end of url
        if (!postUrl.contains("?")) {
            apiUrl.append("?");
        }
        int i = 0;
        StringBuilder data = new StringBuilder();
        //append key values to url
        for (Map.Entry<String, String> entry : urlNameValue.entrySet()) {
            if (i == 0) {
                data.append(entry.getKey() + "=" + entry.getValue());
            } else {
                data.append("&" + entry.getKey() + "=" + entry.getValue());
            }
            i++;
        }
        try {
            base64 = Base64.encodeToString(data.toString().getBytes("UTF-8"), Base64.NO_WRAP);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        apiUrl.append("data=" + base64);

        return apiUrl.toString();
    }//end of getApiUrl()

    /***********************************************************************************************
     * class name :: JSONRequestHandler
     * this will call ws and get response
     ***********************************************************************************************/
    public static abstract class JSONRequestHandler {
        public abstract void onSuccess(JSONObject response);

        public void onFailure(VolleyError error) {
            showAlertDialog(context.getResources().getString(R.string.admin_error));
error.printStackTrace();
            if (error instanceof TimeoutError) {
                Log.e(TAG, "TimeoutError");
            } else if (error instanceof NoConnectionError) {
                Log.e(TAG, "tNoConnectionError");
            } else if (error instanceof AuthFailureError) {
                Log.e(TAG, "AuthFailureError");
            } else if (error instanceof ServerError) {
                Log.e(TAG, "ServerError");
            } else if (error instanceof NetworkError) {
                Log.e(TAG, "NetworkError");
            } else if (error instanceof ParseError) {
                Log.e(TAG, "ParseError");
            }
        }
    }//end of class JSONRequestHandler
}//end 0f class WebServiceHelper
