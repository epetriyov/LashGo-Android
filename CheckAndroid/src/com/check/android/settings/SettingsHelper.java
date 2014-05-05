package com.check.android.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.check.model.dto.LoginInfo;
import com.check.model.dto.SessionInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * User: eugene.petriyov
 * Date: 27.06.13
 * Time: 13:35
 */
public class SettingsHelper {

    private static final String KEY_SESSION = "session_id";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";
    private static final String GCM_REGISTRATION_ID = "gcm_registration_id";
    private SharedPreferences preferences;

    public SettingsHelper(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    private void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        synchronized (editor) {
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    private void setFloat(String key, float value) {
        SharedPreferences.Editor editor = preferences.edit();
        synchronized (editor) {
            editor.putFloat(key, value);
            editor.commit();
        }
    }

    private void setString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        synchronized (editor) {
            editor.putString(key, value);
            editor.commit();
        }
    }

    private void setInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        synchronized (editor) {
            editor.putInt(key, value);
            editor.commit();
        }
    }

    private void setLong(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        synchronized (editor) {
            editor.putLong(key, value);
            editor.commit();
        }
    }

    private long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    private int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    private String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public void login(SessionInfo sessionInfo, LoginInfo loginInfo) {
        setString(KEY_SESSION, sessionInfo.getSessionId());
        setString(KEY_LOGIN, loginInfo.getLogin());
        setString(KEY_PASSWORD, loginInfo.getPasswordHash());
    }

    public void logout() {
        remove(KEY_SESSION);
        remove(KEY_LOGIN);
        remove(KEY_PASSWORD);
    }

    private void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        synchronized (editor) {
            editor.remove(key);
            editor.commit();
        }
    }

    public void saveRegistrationId(String registrationId) {
        setString(GCM_REGISTRATION_ID, registrationId);
    }

    public String getRegistrationId() {
        return getString(GCM_REGISTRATION_ID, "");
    }
}
