package com.example.ajay.friendlocation;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ajay on 07-04-2017.
 */

public class PreferenceManager {
    private static final String SELECTED_CIRCLE_ID = "SELECTED_CIRCLE_ID";
    private static final String SELECTED_CIRCLE_NAME = "SELECTED_CIRCLE_NAME";
    private static final String SELECTED_CIRCLE_CODE = "SELECTED_CIRCLE_CODE";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public PreferenceManager(Context context)
    {
        preferences=context.getSharedPreferences(WSKey.locationPref,Context.MODE_PRIVATE);
        editor=preferences.edit();
    }

    public void setUserId(String uid) {
        editor.putString(JSONKey.user_id,uid);
        editor.commit();
    }

    public void setName(String name) {
        editor.putString(JSONKey.name,name);
        editor.commit();
    }
    public String getName()
    {
        return preferences.getString(JSONKey.name,"");
    }

    public boolean isLogin() {
        return preferences.contains(JSONKey.user_id);
    }

    public String getUserId() {
        return preferences.getString(JSONKey.user_id,"0");
    }

    public void setCircleIds(String ids) {
        editor.putString(JSONKey.circle_ids,ids);
        editor.commit();
    }
    public String getCircleIds()
    {
        return preferences.getString(JSONKey.circle_ids,"");
    }

    public void setCircleNames(String names) {
        editor.putString(JSONKey.circle_names,names);
        editor.commit();
    }
    public String getCircleNames()
    {
        return preferences.getString(JSONKey.circle_names,"");
    }

    public void setInviteCodes(String codes) {
        editor.putString(JSONKey.invite_codes,codes);
        editor.commit();
    }
    public String getInvitesCodes()
    {
        return preferences.getString(JSONKey.invite_codes,"");
    }

    public boolean isCircleSelected() {
        return preferences.contains(SELECTED_CIRCLE_ID);
    }

    public String getSelectedCircleId() {
        return preferences.getString(SELECTED_CIRCLE_ID,"0");
    }
    public void setSelectedCircleId(String circleId)
    {
        editor.putString(SELECTED_CIRCLE_ID,circleId);
        editor.commit();
    }

    public void setSelectedCircleName(String circleName) {
        editor.putString(SELECTED_CIRCLE_NAME,circleName);
        editor.commit();
    }
    public String getSelectedCircleName()
    {
        return preferences.getString(SELECTED_CIRCLE_NAME,"");
    }

    public void setSelectedCircleCode(String code) {
        editor.putString(SELECTED_CIRCLE_CODE,code);
        editor.commit();
    }
    public String getSelectedCircleCode()
    {
        return preferences.getString(SELECTED_CIRCLE_CODE,"");
    }

    public void clearLoginPref() {
        editor.remove(JSONKey.user_id);
        editor.commit();
    }

    public void clearSelectedCircle() {
        editor.remove(SELECTED_CIRCLE_ID);
        editor.commit();
    }

    public void setEmail(String string) {
        editor.putString(JSONKey.email,string);
        editor.commit();
    }
    public String getEmail()
    {
       return preferences.getString(JSONKey.email,"");
    }
}
