package com.badr.hourimeche.hiddenfounders.helper;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesHelper {

    private SharedPreferences mPrefs;

    public PreferencesHelper(Context context) {
        mPrefs = context.getSharedPreferences("user_details", Context.MODE_PRIVATE);
    }

    public String getUserId() {
        return mPrefs.getString("id", "");
    }

    public void setUserId(String id) {
        mPrefs.edit().putString("id", id).apply();
    }

    public String getUserName() {
        return mPrefs.getString("name", "");
    }

    public void setUserName(String name) {
        mPrefs.edit().putString("name", name).apply();
    }

    public String getUserEmail() {
        return mPrefs.getString("email", "");
    }

    public void setUserEmail(String email) {
        mPrefs.edit().putString("email", email).apply();
    }

    public void reset() {
        setUserEmail("");
        setUserName("");
        setUserId("");
    }
}
