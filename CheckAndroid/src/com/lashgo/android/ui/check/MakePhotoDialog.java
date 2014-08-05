package com.lashgo.android.ui.check;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import com.lashgo.android.R;
import com.lashgo.android.utils.IntentUtils;

import java.io.File;
import java.util.Date;

/**
 * Created by Eugene on 05.08.2014.
 */
public class MakePhotoDialog extends DialogFragment {
    public static final String TAG = "MAKE_PHOTO_DIALOG_FRAGMENT";
    final public static int PICK_IMAGE = 1;
    final public static int CAPTURE_IMAGE = 2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.make_photo).setItems(R.array.make_photo_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    getActivity().startActivityForResult(IntentUtils.makeOpenCameraIntent(setImageUri()), CAPTURE_IMAGE);
                    dismiss();
                } else if (i == 1) {
                    getActivity().startActivityForResult(
                            Intent.createChooser(IntentUtils.makeOpenGalleryIntent(), ""),
                            PICK_IMAGE);
                    dismiss();
                }
            }
        }).create();
    }

    private Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory()
                + "/DCIM/Camera/", "IMG_" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        ((CheckInfoActivity) getActivity()).setImgPath(file.getAbsolutePath());
        return imgUri;
    }
}
