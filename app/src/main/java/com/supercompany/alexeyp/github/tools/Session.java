package com.supercompany.alexeyp.github.tools;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Class helper to save and load access token.
 */
public class Session {

    /**
     * Shared pref instance.
     */
    private SharedPreferences sharedPreferences;

    /**
     * Editor instance.
     */
    private SharedPreferences.Editor editor;

    /**
     * Key for git hub preferences.
     */
    private static final String KEY_PREFERENCES = "Github_Preferences";

    /**
     * Key to save access token in {@link SharedPreferences}.
     */
    private static final String KEY_ACCESS_TOKEN = "access_token";

    /**
     * Key to save login in {@link SharedPreferences}.
     */
    private static final String KEY_LOGIN = "login";

    public Session(Context context) {
        sharedPreferences = context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void saveAccessToken(String accessToken) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public void saveLogin(String login) {
        editor.putString(KEY_LOGIN, login);
        editor.commit();
    }

    @TargetApi(19)
    public void resetAccessToken(Context context) {
        editor.clear();
        editor.commit();
        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE))
                    .clearApplicationUserData();
        }
    }

    public String getLogin() {
        return sharedPreferences.getString(KEY_LOGIN, null);
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

}
