package com.lashgo.android.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Eugene on 23.06.2014.
 */
public final class ContextUtils {

    private ContextUtils() {

    }

    public static void showToast(final Context context, Handler handler, final int stringId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, stringId);
            }
        });
    }

    public static void showToast(final Context context, Handler handler, final String errorMessage) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, errorMessage);
            }
        });
    }

    public static void showToast(Context context, int stringId) {
        Toast.makeText(context, context.getString(stringId), Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, String errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }
}