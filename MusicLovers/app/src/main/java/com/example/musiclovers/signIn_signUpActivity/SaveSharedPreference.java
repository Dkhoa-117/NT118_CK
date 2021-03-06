package com.example.musiclovers.signIn_signUpActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * DONE
 */
public class SaveSharedPreference {
    static final String PREF_USER_NAME= "username";
    static final String PREF_EMAIL= "email";
    static final String PREF_AVATAR= "avatar";
    static final String PREF_ID= "_id";

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUser(Context ctx, String userName, String email, String avatar, String _id) {

        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_EMAIL, email);
        editor.putString(PREF_AVATAR, avatar);
        editor.putString(PREF_ID, _id);
        editor.commit();
    }

    public static String getUserName(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
    public static String getEmailAddress(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
    }
    public static String getAvatar(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_AVATAR, "");
    }
    public static String getId(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_ID, "");
    }
    public static void clearUser(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}
