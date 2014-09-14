package com.lashgo.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.ui.images.CircleTransformation;
import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 20.07.2014.
 */
public final class PhotoUtils {

    private PhotoUtils() {
    }

    public static String getFullPhotoUrl(String photoName) {
        return new StringBuilder(LashgoConfig.BASE_URL).append(LashgoConfig.PHOTO_BASE_URI).append(photoName).toString();
    }

    public static void displayImage(Context context, ImageView imageView, String imageSource, int imageSize, int placeHolderId, boolean displayStroke) {
        if (!TextUtils.isEmpty(imageSource)) {
            Picasso.with(context).load(imageSource).centerCrop().
                    resize(imageSize, imageSize).transform(new CircleTransformation(context, displayStroke)).placeholder(placeHolderId).into(imageView);
        }
    }

    public static int convertDpToPixels(float dp, Context context) {
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

    public static int getScreenHeight(Activity context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    public static Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of
            // 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getAbsolutePath(Activity activity, Uri uri) {
        if (uri != null) {
            String[] projection = {MediaStore.MediaColumns.DATA};
            @SuppressWarnings("deprecation")
            Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Gallery photo uri is null");
        }
    }

    public static void displayImage(Context context, ImageView imageView, Uri uri, int imageSize, int placeHolderId, boolean displayStroke) {
        if (uri != null) {
            Picasso.with(context).load(uri).centerCrop().
                    resize(imageSize, imageSize).transform(new CircleTransformation(context, displayStroke)).placeholder(placeHolderId).into(imageView);
        }
    }
}
