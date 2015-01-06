package com.lashgo.android;

/**
 * Created by Eugene on 19.02.14.
 */
public final class LashgoConfig {

//    public static final String BASE_URL = "http://192.168.0.168:1977/lashgo-api";
public static final String BASE_URL = "http://78.47.39.245:8080/lashgo-api";
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss Z";
    public static final String GCM_API_KEY = "226352210286";
    public static final String CLIENT_TYPE = "ANDROID";
    public static final String PHOTO_BASE_URI = "/photos/";
    public static final String CRITTERCISM_APP_ID = "541aa4390729df3292000002";

    public static enum CheckState {ACTIVE, VOTE, FINISHED}

    private LashgoConfig() {

    }


}
