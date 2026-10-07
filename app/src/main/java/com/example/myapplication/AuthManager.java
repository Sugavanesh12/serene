package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREF_NAME = "SereneAuthPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PHONE = "userPhone";
    private static final String USERS_PREF = "SereneUsersDB";

    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static void saveLogin(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_PHONE, email)
                .apply();
    }

    public static String getPhoneNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PHONE, "member@serene.app");
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_IS_LOGGED_IN).remove(KEY_PHONE).apply();
    }

    // Database methods for email & password validation
    public static boolean isEmailRegistered(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(USERS_PREF, Context.MODE_PRIVATE);
        return prefs.contains(email.toLowerCase().trim());
    }

    public static void registerUser(Context context, String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences(USERS_PREF, Context.MODE_PRIVATE);
        prefs.edit().putString(email.toLowerCase().trim(), password).apply();
    }

    public static boolean validatePassword(Context context, String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences(USERS_PREF, Context.MODE_PRIVATE);
        String savedPassword = prefs.getString(email.toLowerCase().trim(), "");
        return savedPassword.equals(password);
    }

    public static void updatePassword(Context context, String email, String newPassword) {
        registerUser(context, email, newPassword);
    }
}
