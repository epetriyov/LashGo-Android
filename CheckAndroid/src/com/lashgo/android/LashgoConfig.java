package com.lashgo.android;

/**
 * Created by Eugene on 19.02.14.
 */
public final class LashgoConfig {

    public static final String BASE_URL = "http://192.168.0.2:1977/lashgo-api";
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss z";
    public static final String GCM_API_KEY = "701321427381";
    public static final String CLIENT_TYPE = "ANDROID";
    public static final String PHOTO_BASE_URI = "/photos/";

    public static enum CheckState {ACTIVE, VOTE, FINISHED}

    private LashgoConfig() {

    }


}
