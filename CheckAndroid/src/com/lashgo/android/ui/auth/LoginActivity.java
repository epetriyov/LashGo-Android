package com.lashgo.android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.lashgo.android.LashgoApplication;
import com.lashgo.android.LoginModule;
import com.lashgo.android.R;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.service.handlers.LoginHandler;
import com.lashgo.android.service.handlers.RestHandlerFactory;
import com.lashgo.android.service.handlers.SocialSignInHandler;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.main.MainActivity;
import com.lashgo.android.utils.ContextUtils;
import com.lashgo.android.utils.Md5Util;
import com.lashgo.model.ErrorCodes;
import com.lashgo.model.dto.ErrorDto;
import com.lashgo.model.dto.ExtendedSocialInfo;
import com.lashgo.model.dto.LoginInfo;
import com.lashgo.model.dto.SocialInfo;
import com.vk.sdk.VKSdk;
import dagger.ObjectGraph;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.EditText;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Eugene on 18.02.14.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ObjectGraph loginGraph;

    private final String DIALOG_TAG = "EnterEmailDialog";

    private EditText login;

    private EditText password;

    private SocialInfo socialInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loginGraph = LashgoApplication.getInstance().getApplicationGraph().plus(getModules().toArray());
        loginGraph.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        login = (EditText) findViewById(R.id.edit_login);
        password = (EditText) findViewById(R.id.edit_password);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_auth_facebook).setOnClickListener(this);
        findViewById(R.id.btn_auth_vk).setOnClickListener(this);
        findViewById(R.id.btn_auth_twitter).setOnClickListener(this);
        findViewById(R.id.btn_password_recover).setOnClickListener(this);
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
        switch (action) {
            case RestHandlerFactory.ACTION_LOGIN:
                if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case RestHandlerFactory.ACTION_REGISTER:
                if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                    serviceHelper.login((LoginInfo) data.getSerializable(LoginHandler.LOGIN_DTO));
                }
                break;
            case RestHandlerFactory.ACTION_PASSWORD_RECOVER:
                if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                    ContextUtils.showToast(this, R.string.password_was_reset);
                }
                break;
            case RestHandlerFactory.ACTION_SOCIAL_SIGN_IN:
                if (resultCode == BaseIntentHandler.FAILURE_RESPONSE) {
                    ErrorDto errorDto = (ErrorDto) data.getSerializable(BaseIntentHandler.ERROR_EXTRA);
                    if (errorDto != null && ErrorCodes.EMAIL_NEEDED.equals(errorDto.getErrorCode())) {
                        socialInfo = (SocialInfo) data.getSerializable(SocialSignInHandler.SOCIAL_DTO);
                        DialogFragment dialogFragment = EnterEmailDialog.newInstance();
                        dialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
                    }
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case RestHandlerFactory.ACTION_CONFIRM_SOCIAL_SIGN_UP:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void unregisterActionsListener() {
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_LOGIN);
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_REGISTER);
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_PASSWORD_RECOVER);
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_SOCIAL_SIGN_IN);
        serviceHelper.removeActionListener(RestHandlerFactory.ACTION_CONFIRM_SOCIAL_SIGN_UP);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_auth_facebook) {
            facebookHelper.loginWithFacebook();
        } else if (view.getId() == R.id.btn_auth_vk) {
            VKSdk.authorize("email");
        } else if (view.getId() == R.id.btn_auth_twitter) {
            twitterHelper.loginWithTwitter();
        } else if (view.getId() == R.id.btn_login) {

        } else if (view.getId() == R.id.btn_password_recover) {
            LoginInfo loginInfo = buildLoginInfo();
            if (loginInfo != null) {
                serviceHelper.login(loginInfo);
            }
        } else if (view.getId() == R.id.btn_register) {
            LoginInfo loginInfo = buildLoginInfo();
            if (loginInfo != null) {
                serviceHelper.register(loginInfo);
            }
        }
    }

    private LoginInfo buildLoginInfo() {
        String loginValue = login.getText().toString();
        String passwordValue = password.getText().toString();
        if (TextUtils.isEmpty(loginValue)) {
            login.setError(getString(R.string.error_empty_field));
        } else if (TextUtils.isEmpty(passwordValue)) {
            password.setError(getString(R.string.error_empty_field));
        } else {
            return new LoginInfo(login.getText().toString(), Md5Util.md5(password.getText().toString()));
        }
        return null;
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new LoginModule(this));
    }

    public void sendSocialEmail(String email) {
        serviceHelper.socialSignUp(new ExtendedSocialInfo(socialInfo, email));
    }
}
