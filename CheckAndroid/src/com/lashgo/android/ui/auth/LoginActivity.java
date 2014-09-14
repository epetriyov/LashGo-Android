package com.lashgo.android.ui.auth;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.social.TwitterHelper;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.dialogs.CustomProgressDialog;

/**
 * Created by Eugene on 18.02.14.
 */
public class LoginActivity extends BaseActivity {

    private AuthController authController;
    private DialogFragment progressDialog;

    private OpenMode openMode;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ExtraNames.OPEN_MODE.name(), openMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onUpClicked() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        initCustomActionBar(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        initExtras(savedInstanceState);
        authController = new AuthController(this, serviceHelper, facebookHelper, twitterHelper);
        authController.setOpenMode(openMode);
        authController.initViews(getWindow().getDecorView().getRootView());
        progressDialog = new CustomProgressDialog();
    }

    private void initExtras(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            openMode = (OpenMode) intent.getSerializableExtra(ExtraNames.OPEN_MODE.name());
        }
        if (openMode == null && savedInstanceState != null) {
            openMode = (OpenMode) savedInstanceState.getSerializable(ExtraNames.OPEN_MODE.name());
        }
    }

    @Override
    protected void registerActionsListener() {
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_LOGIN.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_REGISTER.name());
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == TwitterHelper.TWITTER_AUTH) {

            if (resultCode == Activity.RESULT_OK) {
                twitterHelper.handleCallbackUrl(data.getData());
            }
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
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name());
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
