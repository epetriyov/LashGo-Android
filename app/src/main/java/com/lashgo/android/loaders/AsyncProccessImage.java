package com.lashgo.android.loaders;

import android.os.AsyncTask;
import com.lashgo.android.utils.PhotoUtils;

/**
 * Created by Eugene on 30.11.2014.
 */
public class AsyncProccessImage extends AsyncTask<Void, Void, String> {

    private String imgPath;

    private OnPhotoProcessedListener listener;

    public AsyncProccessImage(String imgPath, OnPhotoProcessedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener for image processong can't be null");
        }
        this.imgPath = imgPath;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return PhotoUtils.compressImage(imgPath);
    }

    @Override
    protected void onPostExecute(String fileName) {
        if (fileName != null) {
            listener.onPhotoProcessed(fileName);
        } else {
            listener.onErrorOccured();
        }
    }

    public static interface OnPhotoProcessedListener {
        void onPhotoProcessed(String imgPath);

        void onErrorOccured();
    }
}