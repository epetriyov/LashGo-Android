package com.lashgo.android.utils;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by Eugene on 05.08.2014.
 */
public final class IntentUtils {

    public static Intent makeOpenCameraIntent(Uri photoUri) {
        if (photoUri != null) {
            final Intent intent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    photoUri);
            return intent;
        } else {
            throw new IllegalArgumentException("Camera photo uri is null");
        }
    }

    public static Intent makeOpenGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }
}
