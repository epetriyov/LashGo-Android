package com.lashgo.android.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.model.dto.ErrorDto;

/**
 * Created by Eugene on 18.10.2014.
 */
public class ErrorDialog extends DialogFragment {

    public static final String TAG = "error_dialog";

    private ErrorDto errorDto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null || args.getSerializable(BaseIntentHandler.ERROR_EXTRA) == null) {
            throw new IllegalStateException("Error dialog args can't be null");
        }
        errorDto = (ErrorDto) args.getSerializable(BaseIntentHandler.ERROR_EXTRA);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivity()).setMessage(errorDto.getErrorMessage()).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return aBuilder.create();
    }

    public static DialogFragment newInstance(ErrorDto errorDto) {
        ErrorDialog errorDialog = new ErrorDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseIntentHandler.ERROR_EXTRA, errorDto);
        errorDialog.setArguments(bundle);
        return errorDialog;
    }
}
