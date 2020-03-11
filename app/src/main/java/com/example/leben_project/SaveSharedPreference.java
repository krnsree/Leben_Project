package com.example.leben_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.example.leben_project.PreferencesUtility.LOGGED_IN_PREF;

public class SaveSharedPreference {

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the Login Status
     *
     * @param context
     * @param loggedIn
     */
    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    /**
     * Get the Login Status
     *
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    public static void setAccount(Context context, String Name) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("Account Name", Name);
        editor.apply();
    }
    public static String getAccount(Context context) {
        return getPreferences(context).getString("Account Name", "");
    }
    public static void setAccountImage(Context context, String photo) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("Account Image", photo);
        editor.apply();
    }

    public static String getAccountImage(Context context) {
        return getPreferences(context).getString("Account Image", "");
    }
}
