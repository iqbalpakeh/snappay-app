package com.payment.snappaytable;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPref {

    /**
     * Constant to access app_context shared preference
     */
    private static final String APP_CONTEXT = "app_context";

    /**
     * Constant to access user_id data
     */
    private static final String USER_ID = "user_id";

    /**
     * Constant to access user_email data
     */
    private static final String USER_EMAIL = "user_email";

    /**
     * Store all user id information data
     *
     * @param context of application
     * @param email   of user input
     * @param uid     from firebase authentication service
     */
    public static void storeUserData(Context context, String email, String uid) {
        storeUid(context, uid);
        storeEmail(context, email);
    }

    /**
     * Store user ID to shared preference
     *
     * @param context of application
     * @param uid     from firebase authentication service
     */
    public static void storeUid(Context context, String uid) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        SharedPreferences.Editor editor = appContext.edit();
        editor.putString(USER_ID, uid);
        editor.apply();
    }

    /**
     * Store user email to shared preference
     *
     * @param context of application
     * @param email   from user input
     */
    public static void storeEmail(Context context, String email) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        SharedPreferences.Editor editor = appContext.edit();
        editor.putString(USER_EMAIL, email);
        editor.apply();
    }

    /**
     * Get UID stored in shared preference
     *
     * @param context of application
     * @return UID for firebase
     */
    public static String getUid(Context context) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        return appContext.getString(USER_ID, "");
    }

    /**
     * Get email of merchant
     *
     * @param context
     * @return
     */
    public static String getEmail(Context context) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        return appContext.getString(USER_EMAIL, "");
    }

}
