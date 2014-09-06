package com.lashgo.android.ui.auth;

import android.app.DialogFragment;
import android.content.Context;
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

    private OpenMode openMode;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.OPEN_MODE.name(), openMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        Intent intent = getIntent();
        if (intent != null) {
            openMode = (OpenMode) intent.getSerializableExtra(ExtraNames.OPEN_MODE.name());
        }
        if (openMode == null && savedInstanceState != null) {
            openMode = (OpenMode) savedInstanceState.getSerializable(ExtraNames.OPEN_MODE.name());
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle(R.string.login);
        authController.setOpenMode(openMode);
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

    public static Intent buildIntent(Context context, OpenMode openMode) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(ExtraNames.OPEN_MODE.name(), openMode);
        return intent;
    }

    public static enum OpenMode {
        FROM_SPLASH, FROM_CHECK
    }
}
