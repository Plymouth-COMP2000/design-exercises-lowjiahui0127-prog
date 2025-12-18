package com.example.mal2017restaurantmanagementapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_REMEMBER_ME = "remember_me";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveUserRole(Context context, String role) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    public static String getUserRole(Context context) {
        return getSharedPreferences(context).getString(KEY_USER_ROLE, "GUEST");
    }

    public static void setLoggedIn(Context context, boolean isLoggedIn) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static void saveUserEmail(Context context, String email) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public static String getUserEmail(Context context) {
        return getSharedPreferences(context).getString(KEY_USER_EMAIL, "");
    }

    public static void setRememberMe(Context context, boolean remember) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_REMEMBER_ME, remember);
        editor.apply();
    }

    public static boolean getRememberMe(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_REMEMBER_ME, false);
    }

    public static void clearAllData(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}