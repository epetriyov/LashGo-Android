package com.lashgo.android.utils;

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

}
