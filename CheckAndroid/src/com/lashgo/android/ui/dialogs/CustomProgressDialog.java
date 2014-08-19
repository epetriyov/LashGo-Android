package com.lashgo.android.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import com.lashgo.android.R;

/**
 * Created by Eugene on 11.08.2014.
 */
public class CustomProgressDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setView(getActivity().getLayoutInflater().inflate(
                R.layout.dialog_progress, null)).setCancelable(false).create();
    }
}
