package com.lashgo.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import com.lashgo.android.LashgoConfig;
import com.lashgo.android.ui.check.CheckActivity;
import com.lashgo.android.ui.check.PhotoLoadListener;
import com.lashgo.android.ui.images.CircleTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Eugene on 20.07.2014.
 */
public final class PhotoUtils {

    private PhotoUtils() {
    }

    public static String getFullPhotoUrl(String photoName) {
        return new StringBuilder(LashgoConfig.BASE_URL).append(LashgoConfig.PHOTO_BASE_URI).append(photoName).toString();
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

    public static String sanitizeURI(String uri) {
        String filePathOriginal = uri;
        // Local storage
        if (uri.startsWith("file://")) {
            filePathOriginal = uri.substring(7);
        }
        return filePathOriginal;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void displayFullImage(Context context, ImageView imageView, String imageSource) {
        if (!TextUtils.isEmpty(imageSource)) {
            Picasso.with(context)
                    .load(imageSource)
                    .fit()
                    .centerInside()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    public static void displayImage(Context context, ImageView imageView, String imageSource, int imageSize, int placeHolderId, boolean displayStroke) {
        displayImage(context, imageView, imageSource, imageSize, imageSize, placeHolderId, displayStroke, true, null);
    }

    public static void displayImage(Context context, ImageView imageView, Uri uri, int imageSize, int placeHolderId, boolean displayStroke) {
        displayImage(context, imageView, uri, imageSize, imageSize, placeHolderId, displayStroke, true, null);
    }

    public static void displayFullImage(CheckActivity context, ImageView imageView, Uri uri) {
        if (uri != null) {
            Picasso.with(context).load(uri).fit().centerInside().
                    into(imageView);
        }
    }

    public static void displayImage(Context context, ImageView photoImg, String fullPhotoUrl, int imageWidth, int imageHeight, int placeHolder, boolean displayStroke, boolean useTransform, final PhotoLoadListener photoLoadListener) {
        if (!TextUtils.isEmpty(fullPhotoUrl)) {
            RequestCreator requestCreator =
                    Picasso.with(context)
                            .load(fullPhotoUrl)
                            .centerCrop()
                            .resize(imageWidth, imageHeight);
            if (useTransform) {
                requestCreator.transform(new CircleTransformation(context, displayStroke));
            }
            if (placeHolder > 0) {
                requestCreator.placeholder(placeHolder)
                        .error(placeHolder);
            }
            requestCreator.into(photoImg, new Callback() {
                @Override
                public void onSuccess() {
                    if (photoLoadListener != null) {
                        photoLoadListener.onPhotoLoaded();
                    }
                }

                @Override
                public void onError() {

                }
            });
        }

    }

    public static void displayImage(Context context, ImageView photoImg, String fullPhotoUrl, int imageWidth, int imageHeight) {
        displayImage(context, photoImg, fullPhotoUrl, imageWidth, imageHeight, -1, false, false, null);
    }

    public static void displayImage(Context context, ImageView photoImg, Uri uri, int screenWidth, int imageHeight) {
        displayImage(context, photoImg, uri, screenWidth, imageHeight, -1, false, false, null);
    }

    private static void displayImage(Context context, ImageView photoImg, Uri uri, int screenWidth, int imageHeight, int placeHolder, boolean displayStroke, boolean useTransform, final PhotoLoadListener photoLoadListener) {
        if (uri != null) {
            RequestCreator requestCreator = Picasso.with(context).load(uri).centerCrop().
                    resize(screenWidth, imageHeight);
            if (useTransform) {
                requestCreator.transform(new CircleTransformation(context, displayStroke));
            }
            if (placeHolder > 0) {
                requestCreator.placeholder(placeHolder)
                        .error(placeHolder);
            }
            requestCreator.into(photoImg, new Callback() {
                @Override
                public void onSuccess() {
                    if (photoLoadListener != null) {
                        photoLoadListener.onPhotoLoaded();
                    }
                }

                @Override
                public void onError() {

                }
            });
            requestCreator.into(photoImg);
        }
    }

    public static void displayImage(Context context, ImageView photoView, String fullPhotoUrl, int imageSize) {
        displayImage(context, photoView, fullPhotoUrl, imageSize, imageSize);
    }

    public static String compressImage(String filePath) {
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "LashGo/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public static void displayImage(Context context, ImageView imageView, int imageResource, int imageSize, boolean transform) {
        RequestCreator requestCreator = Picasso.with(context).load(imageResource).centerCrop().
                resize(imageSize, imageSize);
        if (transform) {
            requestCreator.transform(new CircleTransformation(context, false));
        }
        requestCreator.into(imageView);
    }

    public static void displayImage(Context context, ImageView imageView, String photoUrl, int imageSize, boolean transform) {
        displayImage(context, imageView, photoUrl, imageSize, -1, transform);
    }

    public static void displayImage(Context context, ImageView imageView, Uri uri, int imageSize, boolean transform) {
        displayImage(context, imageView, uri, imageSize, -1, transform);
    }

    public static void displayImage(Context context, ImageView imageView, Uri uri, int imageSize, boolean transform, PhotoLoadListener photoLoadListener) {
        displayImage(context, imageView, uri, imageSize, imageSize, -1, false, true, photoLoadListener);
    }

    public static void displayImage(Context context, ImageView imageView, String photoUrl, int imageSize, boolean tranform, PhotoLoadListener photoLoadListener) {
        displayImage(context, imageView, photoUrl, imageSize, imageSize, -1, false, true, photoLoadListener);
    }
}
