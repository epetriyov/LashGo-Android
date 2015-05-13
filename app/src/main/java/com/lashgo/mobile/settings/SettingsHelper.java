package com.lashgo.mobile.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lashgo.mobile.LashgoConfig;
import com.lashgo.model.dto.LoginInfo;
import com.lashgo.model.dto.SessionInfo;
import com.lashgo.model.dto.SocialInfo;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: eugene.petriyov
 * Date: 27.06.13
 * Time: 13:35
 */
public class SettingsHelper {

    private static final String KEY_SESSION = "session_id";
    private static final String KEY_LOGIN_INFO = "login_info";
    private static final String GCM_REGISTRATION_ID = "gcm_registration_id";
    private static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";
    private static final String KEY_SOCIAL_INFO = "social_info";
    private static final String KEY_LAST_SUBSCRIPTIONS_VIEW = "last_subscription_date";
    private static final String KEY_LAST_NEWS_VIEW = "last_news_view";
    private static final String KEY_ALREADY_VOTED = "already_voted";
    private SessionInfo sessionInfo;
    private SharedPreferences preferences;
    private static volatile SettingsHelper instance;

    private SettingsHelper(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        initSessionInfo();
    }

    public static SettingsHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SettingsHelper.class) {
                if (instance == null) {
                    instance = new SettingsHelper(context);
                }
            }
        }
        return instance;
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

    private void saveSessionInfo(SessionInfo sessionInfo) {
        saveSerializable(KEY_SESSION, sessionInfo);
        this.sessionInfo = sessionInfo;
    }

    public void saveLoginInfo(LoginInfo loginInfo) {
        saveSerializable(KEY_LOGIN_INFO, loginInfo);
    }

    public void login(SessionInfo sessionInfo, LoginInfo loginInfo) {
        saveSessionInfo(sessionInfo);
        saveSerializable(KEY_LOGIN_INFO, loginInfo);
    }

    public void logout() {
        sessionInfo = null;
        remove(KEY_SESSION);
        remove(KEY_SOCIAL_INFO);
        remove(KEY_LOGIN_INFO);
        remove(KEY_SOCIAL_INFO);
        remove(KEY_LAST_NEWS_VIEW);
        remove(KEY_LAST_SUBSCRIPTIONS_VIEW);
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

    public boolean isLoggedIn() {
        return sessionInfo != null;
    }

    public void setFirstLaunch() {
        setBoolean(KEY_IS_FIRST_LAUNCH, false);
    }

    public SocialInfo getSocialInfo() {
        return (SocialInfo) getSerializable(KEY_SOCIAL_INFO, SocialInfo.class);
    }

    public LoginInfo getLoginInfo() {
        return (LoginInfo) getSerializable(KEY_LOGIN_INFO, LoginInfo.class);
    }

    private Serializable getSerializable(String key, Class<? extends Serializable> clazz) {
        String infoString = getString(key, null);
        if (!TextUtils.isEmpty(infoString)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(infoString, clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void saveSerializable(String key, Serializable info) {
        ObjectMapper objectMapper = new ObjectMapper();
        String infoString = null;
        try {
            infoString = objectMapper.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        setString(key, infoString);
    }

    public void socialLogin(SessionInfo sessionInfo, SocialInfo socialInfo) {
        saveSessionInfo(sessionInfo);
        saveSerializable(KEY_SOCIAL_INFO, socialInfo);
    }

    private void initSessionInfo() {
        sessionInfo = (SessionInfo) getSerializable(KEY_SESSION, SessionInfo.class);
    }

    public int getUserId() {
        if (sessionInfo != null) {
            return sessionInfo.getUserId();
        }
        return -1;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public String getLastNewsView() {
        return getString(KEY_LAST_NEWS_VIEW, new SimpleDateFormat(LashgoConfig.DATE_FORMAT).format(new Date()));
    }

    public void setLastSubscriptionsView(Date date) {
        setString(KEY_LAST_SUBSCRIPTIONS_VIEW, new SimpleDateFormat(LashgoConfig.DATE_FORMAT).format(date));
    }

    public String getLastSubscriptionsView() {
        return getString(KEY_LAST_SUBSCRIPTIONS_VIEW, new SimpleDateFormat(LashgoConfig.DATE_FORMAT).format(new Date()));
    }

    public boolean alreadyVoted() {
        return getBoolean(KEY_ALREADY_VOTED, false);
    }

    public void firstVote() {
        setBoolean(KEY_ALREADY_VOTED, true);
    }
}
