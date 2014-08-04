package com.lashgo.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import com.lashgo.android.LashgoConfig;

/**
 * Created by Eugene on 20.07.2014.
 */
public final class PhotoUtils {

    private PhotoUtils() {
    }

    public static String getFullPhotoUrl(String photoName) {
        return new StringBuilder(LashgoConfig.BASE_URL).append(LashgoConfig.PHOTO_BASE_URI).append(photoName).toString();
    }

    public static int convertPixelsToDp(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public static int getScreenWidth(Activity context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

}
