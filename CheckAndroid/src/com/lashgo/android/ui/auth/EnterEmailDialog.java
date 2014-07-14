package com.lashgo.android.ui.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.lashgo.android.R;

/**
 * Created by Eugene on 24.06.2014.
 */
public class EnterEmailDialog extends DialogFragment implements View.OnClickListener {

    private EmailEnterListener emailEnterListener;

    private EditText emailEdit;

    public EnterEmailDialog(EmailEnterListener emailEnterListener) {
        super();
        this.emailEnterListener = emailEnterListener;
    }

    public static DialogFragment newInstance(EmailEnterListener emailEnterListener) {
        return new EnterEmailDialog(emailEnterListener);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_enter_email, null);
        emailEdit = (EditText) dialogView.findViewById(R.id.edit_email);
        dialogView.findViewById(R.id.btn_ready).setOnClickListener(this);
        return new AlertDialog.Builder(getActivity()).setView(dialogView).create();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ready) {
            if (!TextUtils.isEmpty(emailEdit.getText().toString())) {
                emailEnterListener.sendSocialEmail(emailEdit.getText().toString());
                dismiss();
            }
        }
    }

    public interface EmailEnterListener {
        void sendSocialEmail(String email);
    }
}
