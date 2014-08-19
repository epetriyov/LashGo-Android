package com.lashgo.android.ui.auth;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.lashgo.android.R;
import com.lashgo.android.service.ServiceHelper;
import com.lashgo.android.service.handlers.BaseIntentHandler;
import com.lashgo.android.social.FacebookHelper;
import com.lashgo.android.social.TwitterHelper;
import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.main.MainActivity;
import com.lashgo.android.utils.ContextUtils;
import com.lashgo.android.utils.Md5Util;
import com.lashgo.model.ErrorCodes;
import com.lashgo.model.dto.*;
import com.vk.sdk.VKSdk;

import javax.inject.Inject;

/**
 * Created by Eugene on 09.07.2014.
 */
public class AuthController implements View.OnClickListener, EnterEmailDialog.EmailEnterListener {

    private SocialInfo socialInfo;

    private EditText login;

    private EditText password;

    @Inject
    BaseActivity baseActivity;

    @Inject
    ServiceHelper serviceHelper;

    @Inject
    FacebookHelper facebookHelper;

    @Inject
    TwitterHelper twitterHelper;

    public void initViews(View rootView) {
        login = (EditText) rootView.findViewById(R.id.edit_email);
        password = (EditText) rootView.findViewById(R.id.edit_password);
        rootView.findViewById(R.id.btn_login).setOnClickListener(this);
        rootView.findViewById(R.id.btn_register).setOnClickListener(this);
        rootView.findViewById(R.id.btn_facebook).setOnClickListener(this);
        rootView.findViewById(R.id.btn_vk).setOnClickListener(this);
        rootView.findViewById(R.id.btn_twitter).setOnClickListener(this);
        rootView.findViewById(R.id.btn_recover_password).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_facebook) {
            facebookHelper.loginWithFacebook();
        } else if (view.getId() == R.id.btn_vk) {
            VKSdk.authorize("email");
        } else if (view.getId() == R.id.btn_twitter) {
            twitterHelper.loginWithTwitter();
        } else if (view.getId() == R.id.btn_login) {
            LoginInfo loginInfo = buildLoginInfo();
            if (loginInfo != null) {
                serviceHelper.login(loginInfo);
            }
        } else if (view.getId() == R.id.btn_password_recover) {
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
            login.setError(baseActivity.getString(R.string.error_empty_email));
        } else if (TextUtils.isEmpty(passwordValue)) {
            password.setError(baseActivity.getString(R.string.error_empty_password));
        } else {
            return new LoginInfo(login.getText().toString(), Md5Util.md5(password.getText().toString()));
        }
        return null;
    }

    public void handleServerResponse(String action, int resultCode, Bundle data) {
        if (BaseIntentHandler.ServiceActionNames.ACTION_LOGIN.name().equals(action)) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                onLoginSuccessFull();
            } else {
                baseActivity.showErrorToast(data);
            }
        } else if (BaseIntentHandler.ServiceActionNames.ACTION_REGISTER.name().equals(action)) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                if (data != null) {
                    onRegisterSuccessFull((RegisterResponse) data.getSerializable(BaseIntentHandler.ServiceExtraNames.REGISTER_RESPONSE_INFO.name()));
                }
            } else {
                baseActivity.showErrorToast(data);
            }
        } else if (BaseIntentHandler.ServiceActionNames.ACTION_PASSWORD_RECOVER.name().equals(action)) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                ContextUtils.showToast(baseActivity, R.string.password_was_reset);
            } else {
                baseActivity.showErrorToast(data);
            }
        } else if (BaseIntentHandler.ServiceActionNames.ACTION_SOCIAL_SIGN_IN.name().equals(action)) {
            if (resultCode == BaseIntentHandler.FAILURE_RESPONSE) {
                ErrorDto errorDto = (ErrorDto) data.getSerializable(BaseIntentHandler.ERROR_EXTRA);
                if (errorDto != null && ErrorCodes.EMAIL_NEEDED.equals(errorDto.getErrorCode())) {
                    socialInfo = (SocialInfo) data.getSerializable(BaseIntentHandler.ServiceExtraNames.SOCIAL_DTO.name());
                    DialogFragment dialogFragment = EnterEmailDialog.newInstance(this);
                    dialogFragment.show(baseActivity.getFragmentManager(), EnterEmailDialog.DIALOG_TAG);
                } else {
                    baseActivity.showErrorToast(data);
                }
            } else {
                if (data != null) {
                    onRegisterSuccessFull((RegisterResponse) data.getSerializable(BaseIntentHandler.ServiceExtraNames.REGISTER_RESPONSE_INFO.name()));
                }
            }
        } else if (BaseIntentHandler.ServiceActionNames.ACTION_CONFIRM_SOCIAL_SIGN_UP.name().equals(action)) {
            if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
                if (data != null) {
                    onRegisterSuccessFull((RegisterResponse) data.getSerializable(BaseIntentHandler.ServiceExtraNames.REGISTER_RESPONSE_INFO.name()));
                }
            } else {
                baseActivity.showErrorToast(data);
            }
        }

    }

    private void onRegisterSuccessFull(RegisterResponse registerResponse) {
        if (registerResponse != null) {
            if (registerResponse.getUserName() != null) {
                baseActivity.startActivity(SuccessfulRegisterActivity.buildIntent(baseActivity, registerResponse));
                baseActivity.setResult(Activity.RESULT_OK);
                baseActivity.finish();
            } else {
                onLoginSuccessFull();
            }
        }
    }

    private void onLoginSuccessFull() {
        baseActivity.startActivity(new Intent(baseActivity, MainActivity.class));
        baseActivity.setResult(Activity.RESULT_OK);
        baseActivity.finish();
    }

    @Override
    public void sendSocialEmail(String email) {
        serviceHelper.socialSignUp(new ExtendedSocialInfo(socialInfo, email));
    }
}
