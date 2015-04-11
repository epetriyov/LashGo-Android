package com.lashgo.android.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;

/**
 * Created by Eugene on 18.10.2014.
 */
public class ErrorDialog extends DialogFragment {

    public static final String TAG = "error_dialog";

    private String error;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null || args.getString(BaseIntentHandler.ERROR_EXTRA) == null) {
            throw new IllegalStateException("Error dialog args can't be null");
        }
        error = args.getString(BaseIntentHandler.ERROR_EXTRA);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivity()).setMessage(error).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return aBuilder.create();
    }

    public static DialogFragment newInstance(String error) {
        ErrorDialog errorDialog = new ErrorDialog();
        Bundle bundle = new Bundle();
        bundle.putString(BaseIntentHandler.ERROR_EXTRA, error);
        errorDialog.setArguments(bundle);
        return errorDialog;
    }
}
