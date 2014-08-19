package com.lashgo.android.ui.auth;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.dialogs.CustomProgressDialog;

import javax.inject.Inject;

/**
 * Created by Eugene on 18.02.14.
 */
public class LoginActivity extends BaseActivity {

    @Inject
    AuthController authController;
    private DialogFragment progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle(R.string.login);
        authController.initViews(getWindow().getDecorView().getRootView());
        progressDialog = new CustomProgressDialog();
    }

    @Override
    protected void registerActionsListener() {
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LOGIN.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_REGISTER.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_PASSWORD_RECOVER.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_CONFIRM_SOCIAL_SIGN_UP.name());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getData() != null) {
            twitterHelper.handleCallbackUrl(intent.getData());
        }
    }

    @Override
    public void startProgress() {
        showDialog(progressDialog, PROGRESS_DIALOG);
    }

    @Override
    public void stopProgress() {
        dismissDialog(progressDialog);
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        stopProgress();
        authController.handleServerResponse(action, resultCode, data);
    }

    @Override
    protected void unregisterActionsListener() {
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LOGIN.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_REGISTER.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_PASSWORD_RECOVER.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name());
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_CONFIRM_SOCIAL_SIGN_UP.name());
    }
}
