package com.app.mybaseapplication.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import static com.app.mybaseapplication.preference.PreferenceAttribute.SHARED_PREFERENCE;
import static com.app.mybaseapplication.preference.PreferenceAttribute.TOKEN;

/**
 * Created on 11-Oct-2020
 * author: Syed Fuzail
 * email: fuzail@imobisoft.co.uk
 */
public class PreferenceHelper {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private PreferenceHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static PreferenceHelper getInstance(Context context) {
        return new PreferenceHelper(context);
    }

    public String getToken() {
        return mSharedPreferences.getString(TOKEN, "");
    }

    public void setToken(String token) {
        mEditor.putString(TOKEN, token).apply();
    }

}
