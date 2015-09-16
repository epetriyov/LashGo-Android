package com.lashgo.mobile.utils;

import android.util.Log;

import com.lashgo.mobile.BuildConfig;

/**
 * Created by Eugene on 19.06.2014.
 */
public final class LogUtils {

    private static final String DEBUG_TAG = "LashGo";

    private LogUtils() {

    }

    public static void log(String logInfo) {
        if (BuildConfig.DEBUG) {
            Log.d(DEBUG_TAG, logInfo);
        }
    }
}
