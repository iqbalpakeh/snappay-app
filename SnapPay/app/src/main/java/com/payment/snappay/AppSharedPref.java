package com.payment.snappay;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPref {

    /**
     * Constant to access app_context shared preference
     */
    private static final String APP_CONTEXT = "app_context";

    /**
     * Flag to indicate if debugging code is active or not
     */
    private static final boolean DEBUG_MODE = true;

    /**
     * Constant to access user_id data
     */
    private static final String USER_ID = "user_id";

    /**
     * Constant to access user_email data
     */
    private static final String USER_EMAIL = "user_email";

    /**
     * Constant to access user_token data
     */
    private static final String USER_TOKEN = "user_token";

    /**
     * Store all user id information data
     *
     * @param context of application
     * @param email   of user input
     * @param token   from firebase token generator service
     * @param uid     from firebase authentication service
     */
    public static void storeUserData(Context context, String email, String token, String uid) {
        storeUserID(context, uid);
        storeUserEmail(context, email);
        storeUserToken(context, token);
    }

    /**
     * Store user ID to shared preference
     *
     * @param context of application
     * @param uid     from firebase authentication service
     */
    public static void storeUserID(Context context, String uid) {
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
    public static void storeUserEmail(Context context, String email) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        SharedPreferences.Editor editor = appContext.edit();
        editor.putString(USER_EMAIL, email);
        editor.apply();
    }

    /**
     * Store user token to shared preference
     *
     * @param context of application
     * @param token   from firebase token generator service
     */
    public static void storeUserToken(Context context, String token) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        SharedPreferences.Editor editor = appContext.edit();
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    /**
     * Get UID stored in shared preference
     *
     * @param context of application
     *
     * @return UID for firebase
     */
    public static String getUID(Context context) {
        SharedPreferences appContext = context.getSharedPreferences(APP_CONTEXT, 0);
        return appContext.getString(USER_ID, "");
    }

    /**
     * Checking if debug mode is activated.
     *
     * @return true if debug mode is active. False, otherwise.
     */
    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

}
