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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle(R.string.login);
        authController.initViews(getWindow().getDecorView().getRootView());
    }

    @Override
    protected void registerActionsListener() {
        addActionListener(RestHandlerFactory.ACTION_LOGIN);
        addActionListener(RestHandlerFactory.ACTION_REGISTER);
        addActionListener(RestHandlerFactory.ACTION_PASSWORD_RECOVER);
        addActionListener(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN);
        addActionListener(RestHandlerFactory.ACTION_CONFIRM_SOCIAL_SIGN_UP);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getData() != null) {
            twitterHelper.handleCallbackUrl(intent.getData());
        }
    }

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        authController.handleServerResponse(action, resultCode, data);
    }

    @Override
    protected void unregisterActionsListener() {
        removeActionListener(RestHandlerFactory.ACTION_LOGIN);
        removeActionListener(RestHandlerFactory.ACTION_REGISTER);
        removeActionListener(RestHandlerFactory.ACTION_PASSWORD_RECOVER);
        removeActionListener(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN);
        removeActionListener(RestHandlerFactory.ACTION_CONFIRM_SOCIAL_SIGN_UP);
    }
}
