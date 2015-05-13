package com.lashgo.mobile.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Eugene on 23.06.2014.
 */
public final class ContextUtils {

    private ContextUtils() {

    }

    public static void showToast(Context context, int stringId) {
        Toast.makeText(context, context.getString(stringId), Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, String errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }
}
