package com.lashgo.android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.RestHandlerFactory;
import com.lashgo.android.ui.BaseActivity;

import javax.inject.Inject;

/**
 * Created by Eugene on 18.02.14.
 */
public class LoginActivity extends BaseActivity {

    @Inject
    AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        authController.initViews(getWindow().getDecorView().getRootView());
    }

    @Override
    protected void registerActionsListener() {
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_LOGIN, this);
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_REGISTER, this);
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_PASSWORD_RECOVER, this);
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN, this);
        serviceHelper.addActionListener(RestHandlerFactory.ACTION_CONFIRM_SOCIAL_SIGN_UP, this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getData() != null) {
            twitterHelper.handleCallbackUrl(intent.getData());
        }
    }

    @Override
    protected void processServerResult(String action, int resultCode, Bundle data) {
        authController.handleServerResponse(action, resultCode, data);
    }

    @Override
    protected void unregisterActionsListener() {
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_LOGIN);
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_REGISTER);
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_PASSWORD_RECOVER);
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN);
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_CONFIRM_SOCIAL_SIGN_UP);
    }
}
