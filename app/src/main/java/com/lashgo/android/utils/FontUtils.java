package com.lashgo.android.utils;

/**
 * Created by Eugene on 28.07.2014.
 */

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public final class FontUtils {
    private static final String FONT_PATH_FORMAT = "fonts/%s.ttf";
    private static Map<String, Typeface> typefaceCache = new HashMap<>();

    private FontUtils() {
    }

    /**
     * Creates Roboto typeface and puts it into cache
     */
    public  static Typeface getRobotoTypeface(Context context, String fontName) {
        if (!typefaceCache.containsKey(fontName)) {
            typefaceCache.put(fontName, Typeface.createFromAsset(context.getAssets(), String.format(FONT_PATH_FORMAT, fontName)));
        }
        return typefaceCache.get(fontName);
    }
}
