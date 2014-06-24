package com.lashgo.android.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.lashgo.android.R;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.EditText;

import javax.inject.Inject;

/**
 * Created by Eugene on 24.06.2014.
 */
public class EnterEmailDialog extends DialogFragment implements View.OnClickListener {

    @Inject
    private LoginActivity loginActivity;

    private EditText emailEdit;

    public static DialogFragment newInstance() {
        return new DialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = LayoutInflater.inflate(getActivity(), R.layout.dialog_enter_email);
        emailEdit = (EditText) dialogView.findViewById(R.id.edit_email);
        dialogView.findViewById(R.id.btn_ready).setOnClickListener(this);
        return new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ready) {
            if (!TextUtils.isEmpty(emailEdit.getText().toString())) {
                loginActivity.sendSocialEmail(emailEdit.getText().toString());
                dismiss();
            }
        }
    }
}
